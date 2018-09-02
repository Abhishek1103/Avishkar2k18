package hubFramework;

import java.net.*;
import java.io.*;
import java.util.*;

public class HubServer
{
    public static void main(String[] args)throws IOException
    {
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
