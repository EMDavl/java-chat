package ru.itis.server;

import ru.itis.protocol.CustomMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiChatServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private Map<String, List<ClientHandler>> rooms;

    public MultiChatServer() {
        clients = new CopyOnWriteArrayList<>();

    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private InputStream in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean isEnded = false;
            while (!isEnded) {
                CustomMessage message = CustomMessage.getFromInputStream(in);
                if (message.getMessageText().equals("/end")) {
                    endConnection();
                    isEnded = true;
                } else {
                    sendAll(message);
                }
            }
        }

        private void sendAll(CustomMessage message) {
            System.out.println(message.getMessageText());
            for (ClientHandler client : clients) {
                try {
                    OutputStream stream = client.socket.getOutputStream();
                    stream.write(message.getBytes());
                    stream.flush();
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        private void endConnection() {
            clients.remove(this);
            try {
                OutputStream stream = this.socket.getOutputStream();
                stream.write(CustomMessage
                        .createMessage(CustomMessage.DISCONNECT_MESSAGE,
                                "interrupting connection")
                        .getBytes());
                stream.flush();
                socket.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.interrupt();
        }
    }
}
