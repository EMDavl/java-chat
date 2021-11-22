package ru.itis.starters;

import ru.itis.client.ClientSocket;

import java.util.Scanner;

public class ClientStarter {
    public static void main(String[] args) {
        ClientSocket socket = new ClientSocket();
        socket.startConnection("127.0.0.1", 6060);
        Scanner scanner = new Scanner(System.in);

        boolean isEnded = false;
        while (!isEnded) {
            String message = scanner.nextLine();
            if (message.equals("/end")) {
                isEnded = true;
            }
            socket.sendMessage(message);
        }
    }
}
