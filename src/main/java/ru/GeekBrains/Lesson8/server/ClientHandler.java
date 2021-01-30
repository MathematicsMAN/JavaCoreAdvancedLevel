package ru.GeekBrains.Lesson8.server;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
//    обработчик подключающихся клиентов
    private MyServer myServer;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String nick;

    public String getNick() {
        return nick;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        myServer.unsubscribe(this);
        Message message = new Message();
        message.setMessage(nick + " вышел из чата");
        myServer.broadcastMessage(message);
        try {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authentication() {
        while (true){
            try {
                AuthMessage message = new Gson().fromJson(dataInputStream.readUTF(),AuthMessage.class);
                String nick = myServer.getAuthService().getNickByLoginAndPass(message.getLogin(),message.getPassword());
                if(nick != null && !myServer.isNickBusy(nick)){
                    message.setAuthenticated(true);
                    message.setNick(nick);
                    this.nick = nick;
                    dataOutputStream.writeUTF(new Gson().toJson(message));
                    Message broadcastMsg = new Message();
                    broadcastMsg.setMessage(nick + " вошёл в чат");
                    myServer.broadcastMessage(broadcastMsg);
                    myServer.subscribe(this);
                    return;
                } else {
                    message.setAuthenticated(false);
                    this.nick = nick;
                    dataOutputStream.writeUTF(new Gson().toJson(message));
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

    private void readMessages() throws IOException{
        while (true){
            Message message = new Gson().fromJson(dataInputStream.readUTF(), Message.class);
            message.setNick(nick);
            System.out.println(message);
            if (!message.getMessage().startsWith("/")){
                myServer.broadcastMessage(message);
                continue;
            }
            // общий вид команды в сообщении:
            // /<command> <message>
            String[] tokens = message.getMessage().split("\\s");
            switch (tokens[0]){
                case "/end":{
                    return;
                }
                case "/w":{// /w <nick> <message>
                    if (tokens.length < 3){
                        Message msg = new Message();
                        msg.setMessage("Не хватает параметров, необходимо " +
                                "отправить команду следующего вида: " +
                                "/w <ник> <сообщение>");
                        this.sendMessage(msg);
                        continue;
                    }
                    String nick = tokens[1];
                    StringBuilder msg = new StringBuilder();
                    for (int i = 2; i < tokens.length; i++) {
                        msg.append(tokens[i]).append(" ");
                    }
                    myServer.sendMsgToClient(this,nick, msg.toString());
                    break;
                }
            }
//            if ("/end".equals(message.getMessage())){
//                return;
//            }
//            myServer.broadcastMessage(message);
        }
    }

    public void sendMessage(Message message) {
        try {
            dataOutputStream.writeUTF(new Gson().toJson(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
