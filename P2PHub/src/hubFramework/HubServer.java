package hubFramework;

import javafx.util.Pair;

import java.net.*;
import java.io.*;
import java.util.*;

public class HubServer
{

    protected static Queue<Pair<String,String> > userListToNotify;
    protected static HashSet<String> unSentNotification;


    public static void main(String[] args)throws IOException
    {
        userListToNotify = new LinkedList<Pair<String,String> >();
        unSentNotification = new HashSet<String>();

        SendRealTimeNotificationDaemon realTime = new SendRealTimeNotificationDaemon();
        new Thread(realTime).start();

        ServerSocket hub = new ServerSocket(1111);
        while(true)
        {
            System.out.println("Waiting for connection accept");
            Socket connectedSocket = hub.accept();
            System.out.println("Connection arrived: "+connectedSocket.getInetAddress().getHostName());
            Peer peer = new Peer(connectedSocket);
            System.out.println("Streams initialized and objectified");
            new Thread(new HubPeerCommunication(peer)).start();
            System.out.println("Thread started for "+connectedSocket.getInetAddress().getHostName());
        }
    }
}
