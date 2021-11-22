package ru.itis.client;

import lombok.Data;
import ru.itis.protocol.CustomMessage;

import java.io.*;
import java.net.Socket;

@Data
public class ClientSocket {
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private Thread receiverThread;

    public void startConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = socket.getOutputStream();
            in = socket.getInputStream();

            receiverThread = new Thread(messageReceiver);
            receiverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        CustomMessage messageToBeSend = CustomMessage.createMessage(1, message);
        try {
            out.write(messageToBeSend.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable messageReceiver = () -> {
        while (receiverThread.isAlive()) {
            CustomMessage message = CustomMessage.getFromInputStream(in);
            if (message.getType() == CustomMessage.TEXT_MESSAGE) {
                System.out.println(message.getMessageText());
            } else if (message.getType() == CustomMessage.DISCONNECT_MESSAGE){
                stopConnection();
                break;
            }
        }
    };


    //TODO Придумать как лучше обрабатывать типы сообщений
    private void proceedServiceMessage(CustomMessage message) {
        if (message.getMessageText().startsWith("available rooms: ")) {
            System.out.println(message.getMessageText());
        }
    }

    public void stopConnection() {
        try {
            receiverThread.interrupt();
            socket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
