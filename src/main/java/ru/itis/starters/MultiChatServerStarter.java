package ru.itis.starters;

import ru.itis.server.MultiChatServer;

public class MultiChatServerStarter {
    public static void main(String[] args) {
        MultiChatServer server = new MultiChatServer();
        server.startServer(6060);
    }
}
