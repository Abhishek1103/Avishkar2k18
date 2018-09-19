package hubFramework;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class SendData implements Runnable
{
    private String targetIP;
    private String pathOfFiles;
    SendData(String pathOfFiles)
    {
        this.targetIP = targetIP;
        this.pathOfFiles = pathOfFiles;
    }


    @Override
    public void run()
    {
        SocketChannel ssc = null;
        try {
            ssc = SocketChannel.open();
            ssc.connect(new InetSocketAddress(targetIP, 5000));
            File dir = new File(pathOfFiles);
            for(File file : dir.listFiles())
            {
                System.out.println("sending video "+file.getAbsolutePath());
                sendFile(ssc, file.getAbsolutePath());
            }
        }
        catch (SocketException e) {
            //TODO: manage if connection is broken
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(SocketChannel ssc, String path)
    {
        try {
            FileChannel fc = new RandomAccessFile(path,"rw").getChannel();
            ByteBuffer buf = ByteBuffer.allocate(2048);
            int bytesRead = fc.read(buf);
            while(bytesRead!= -1){
                buf.flip();
                ssc.write(buf);
                buf.clear();
                bytesRead = fc.read(buf);
            }

            fc.close();
            ssc.close();
            ssc.close();

        } catch (Exception ex) {
            System.out.println(""+ex);
        }
    }
}
