package Client.Utility;

import Client.DataClasses.Video;
import Client.UI.MainPageController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class SendData extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                try{

                    Socket sock = new Socket("172.31.84.87",1111);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    dout.writeUTF("#PREMIUM");
                    boolean bool = dis.readBoolean();


                    if(bool){
                        try {
                            ServerSocketChannel ssc = ServerSocketChannel.open();
                            ssc.socket().bind(new InetSocketAddress(5000));
                            SocketChannel sc = ssc.accept();

                            ArrayList<Pair<Video, String>> list = MainPageController.premiumVideoList;

                            for (Pair<Video, String> p: list) {
                                try {
                                    dout.writeUTF(p.getKey().getVideoName());
                                    dout.writeUTF(p.getValue());
                                    String vidPath = p.getKey().getVideoPath();
                                    System.out.println("VidPath: " + vidPath);
                                    if (sendFile(sc, vidPath)) {
                                        System.out.println("File sent successfully");
                                    } else {
                                        System.out.println("Some error occured while transfering: " + p.getKey().getVideoName() + ", path: " + p.getKey().getVideoPath());
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }


                    ois.close();
                    oos.close();
                    sock.close();

                }catch(Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
    }


    boolean sendFile(SocketChannel sc, String filePath){
        try {
            FileChannel fc = new RandomAccessFile(filePath, "rw").getChannel();
            ByteBuffer buf = ByteBuffer.allocate(2048);
            int bytesRead = fc.read(buf);

            while (bytesRead != -1) {
                buf.flip();
                sc.write(buf);
                buf.clear();
                bytesRead = fc.read(buf);
            }

            fc.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
