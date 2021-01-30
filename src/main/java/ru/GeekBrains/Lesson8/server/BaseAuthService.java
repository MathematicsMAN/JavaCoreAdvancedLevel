package ru.GeekBrains.Lesson8.server;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
//    Реализует интерфейс AuthService, работающий на основе списка,
//    по которому будет происходить авторизация пользователей в чате
    private List<Entry> entries;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("ivan", "password", "Neivanov"));
        entries.add(new Entry("sharik", "gav", "Auf"));
        entries.add(new Entry("otvertka", "shurup", "KrucuVerchu"));
    }

    private class Entry {
        private String login;
        private String password;
        private String nick;

        public Entry(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }

    @Override
    public void start() {
        System.out.println("Сервис авторизации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис авторизации остановлен");
    }

    @Override
    public String getNickByLoginAndPass(String login, String pass) {
        for (Entry entry : entries) {
            if(login.equals(entry.login) && pass.equals(entry.password)){
                return entry.nick;
            }

        }
        return null;
    }
}
