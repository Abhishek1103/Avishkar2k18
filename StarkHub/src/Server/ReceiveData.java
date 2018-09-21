package Server;

import Client.Login.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class ReceiveData extends Service {

    int n;
    ArrayList<String> list;
    DataInputStream dis;

    public ReceiveData(int n, ArrayList<String> list, DataInputStream dis){
        this.n = n;
        this.list = list;
        this.dis = dis;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    ServerSocketChannel ssc = ServerSocketChannel.open();
                    ssc.socket().bind(new InetSocketAddress(15004));



                    for(int i=0;i<n;i++){
                        String vidName = list.get(i);

                        SocketChannel sc = ssc.accept();
                        System.out.println("socket: "+sc);
                        String path = System.getProperty("user.home")+"/starkhub/"+ Main.USERNAME+"/premium/"+vidName;

                        if(save(sc, path, dis)){
                            System.out.println("File saved successfully");
                        }else{
                            System.out.println("Some error occured while saving file: "+vidName);
                        }
                        sc.close();
                    }

                    ssc.close();


                }catch (Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }


    boolean save(SocketChannel sc, String path, DataInputStream dis){

        try{

            FileChannel fc = new RandomAccessFile(path,"rw").getChannel();
            ByteBuffer buf = ByteBuffer.allocate(2048);

            //int n = dis.readInt();
            int bytesRead = sc.read(buf);
            while(bytesRead!= -1){
                buf.flip();
                fc.write(buf);
                buf.clear();
                //n = dis.readInt();
                if(n == -1)
                    break;
                bytesRead = sc.read(buf);
            }
            fc.close();

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
