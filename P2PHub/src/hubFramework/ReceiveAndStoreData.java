package hubFramework;

import javafx.util.Pair;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class ReceiveAndStoreData implements Runnable
{
    Peer peer;
    String targetUsername, targetIP, premiumUsername;

    ReceiveAndStoreData(String premiumUsername, Peer peer, String targetUsername, String targetIP)
    {
        this.peer = peer;
        this.premiumUsername = premiumUsername;
        this.targetUsername = targetUsername;
        this.targetIP = targetIP;
    }

    @Override
    public void run()
    {
        System.out.println("Starting to receive video");
        String storagePath = System.getProperty("user.home")+"/Hub/tmp/"+targetUsername;
        File tempDir = new File(storagePath);
        tempDir.mkdirs();
        ArrayList<String> videoList = new ArrayList<String>();
        ArrayList<String> channelList = new ArrayList<String>();
        int n = 0;
        try {
            n = peer.dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            sc.connect(new InetSocketAddress(peer.peerSocket.getInetAddress().getHostName(), 5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String videoName, channelName;
        for(int i = 0;i < n;i++)
        {
            try {
                videoName = peer.dis.readUTF();
                videoList.add(videoName);
                channelName = peer.dis.readUTF();
                channelList.add(channelName);
                receiveFile(sc, storagePath+videoName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Socket socket = null;
        try {
            socket = new Socket(peer.peerSocket.getInetAddress().getHostName(), 15001);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataInputStream dis;
        DataOutputStream dos;
        String alternatePathPrefix = "";
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            dos.writeUTF("#RECEIVEDATA");
            dos.writeInt(n);
            oos.writeObject(videoList);
            alternatePathPrefix = dis.readUTF();
            dis.readBoolean();//for stopping starting new sender thread
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new SendData(storagePath));
        File file = new File(System.getProperty("user.home") + "/Hub/Client/" + premiumUsername);
        ObjectInputStream readSerializedObject = null;
        try {
            readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            for(int i=0;i<channelList.size();i++)
            {
                String chName = channelList.get(i);
                for(Channel ch:obj.channels)
                {
                    int f = 0;
                    if(ch.channelName.equals(chName))
                    {
                        for(Video vid:ch.videos)
                        {
                            if(vid.videoName.equals(videoList.get(i))) {
                                f = 1;
                                vid.alternatePathOfVideo = alternatePathPrefix + videoList.get(i);
                                break;
                            }
                        }
                        if(f == 1)
                            break;
                    }
                }
            }
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + premiumUsername)));
            writeSerializedObject.writeObject(obj);
            writeSerializedObject.close();
            System.out.println("Done Writting back object " + premiumUsername);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(SocketChannel sc, String path) throws IOException
    {
        FileChannel fc = new RandomAccessFile(path, "rw").getChannel();
        ByteBuffer buf = ByteBuffer.allocate(2048);

        int bytesRead = sc.read(buf);
        while (bytesRead != -1)
        {
            buf.flip();
            fc.write(buf);
            buf.clear();
            bytesRead = sc.read(buf);
        }
        fc.close();
        sc.close();
    }
}
