package ru.GeekBrains.Lesson8.client;

import ru.GeekBrains.Lesson8.server.Message;

import java.io.IOException;

public interface ServerService {
    boolean isConnected();
    void openConnection();
    void closeConnection();
    void sendMessage(String message);
    Message readMessages();
    String authorization(String login, String password) throws IOException;
}
