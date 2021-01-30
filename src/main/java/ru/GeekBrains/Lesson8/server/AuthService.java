package ru.GeekBrains.Lesson8.server;

public interface AuthService {
//    Описывает сервис аутентификации клиента на стороне сервера
    void start();
    void stop();
    String getNickByLoginAndPass(String login, String pass);

}
