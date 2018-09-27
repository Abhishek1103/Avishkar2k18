package server;

import data.User;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.util.Queue;

public class SendLoginTimeMessageDaemon implements Runnable
{
    String ip;
    User user;
    SendLoginTimeMessageDaemon(String ip, User user)
    {
        this.ip = ip;
        this.user = user;
    }

    @Override
    public void run()
    {
        synchronized (this)
        {
            System.out.println("In login time noti");
//            Socket socket;
//            DataInputStream dis = null;
//            DataOutputStream dos = null;
//            ObjectOutputStream oos = null;
//            ObjectInputStream ois = null;
//            try {
//                socket = new Socket(ip, 13002);
//                dis = new DataInputStream(socket.getInputStream());
//                dos = new DataOutputStream(socket.getOutputStream());
//                oos = new ObjectOutputStream(socket.getOutputStream());
//                ois = new ObjectInputStream(socket.getInputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            if(Chat.unSentMessagesUserIpList.contains(ip)) {
                System.out.println("login time noti user present" + ip);
                try {
                    user.getDos().writeBoolean(true);
                    Queue<String> data = Chat.ipMsgMap.get(ip);
                    String msg;
                    user.getDos().writeInt(data.size());
                    while((msg=data.poll())!=null)
                    {
                        user.getDos().writeUTF(msg);
                    }
                    System.out.println("removed "+ip+" from hashset");
                    Chat.unSentMessagesUserIpList.remove(ip);
                    System.out.println("entry removed from hashmap");
                    Chat.ipMsgMap.remove(ip,data);
//                    dis.close();
//                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            else {
                try {
                    user.getDos().writeBoolean(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
