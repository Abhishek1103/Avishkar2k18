package server;

import data.User;
import javafx.util.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;


// ALL chat feature between student and professor will be designed here NOT IN ANY OTHER class
public class Chat implements  Runnable
{
    protected static Queue<Pair<String,String> > userListToNotify;
    protected static HashSet<String> unSentMessagesUserIpList;
    protected static HashMap<String, Queue<String> > ipMsgMap;

    @Override
    public void run()
    {
        userListToNotify = new LinkedList<Pair<String,String> >();
        unSentMessagesUserIpList = new HashSet<String>();
        ipMsgMap = new HashMap<String, Queue<String>>();

        SendRealTimeMessageDaemon realTime = new SendRealTimeMessageDaemon();
        new Thread(realTime).start();

        ServerSocket messaging = null;
        try {
            messaging = new ServerSocket(13001);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true)
        {
            try {
                System.out.println("Waiting for chat client");
                Socket userSocket = messaging.accept();
                User user = new User(userSocket);
                System.out.println("Got a new user in chat and starting thread for"+user.getUserSocket().getInetAddress().getHostAddress());
                new Thread(new ChatHandler(user)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
