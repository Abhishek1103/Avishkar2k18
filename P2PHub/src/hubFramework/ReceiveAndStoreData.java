package hubFramework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ReceiveAndStoreData implements Runnable
{
    Peer peer;
    String targetUsername, targetIP;

    ReceiveAndStoreData(Peer peer, String targetUsername, String targetIP)
    {
        this.peer = peer;
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
        for(int i = 0;i < n;i++)
        {
            try {
                receiveFile(sc, storagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new Thread(new SendData(storagePath));
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
