package Server;

import Client.Login.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

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

    public ReceiveData(int n, ArrayList<String> list){
        this.n = n;
        this.list = list;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    ServerSocketChannel ssc = ServerSocketChannel.open();
                    ssc.socket().bind(new InetSocketAddress(15004));

                    SocketChannel sc = ssc.accept();

                    for(int i=0;i<n;i++){
                        String vidName = list.get(i);

                        String path = System.getProperty("user.home")+"/starkhub/"+ Main.USERNAME+"/premium/"+vidName;

                        if(save(sc, path)){
                            System.out.println("File saved successfully");
                        }else{
                            System.out.println("Some error occured while saving file: "+vidName);
                        }
                    }

                    sc.close();


                }catch (Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }


    boolean save(SocketChannel sc, String path){

        try{

            FileChannel fc = new RandomAccessFile(path,"rw").getChannel();
            ByteBuffer buf = ByteBuffer.allocate(2048);

            int bytesRead = sc.read(buf);
            while(bytesRead!= -1){
                buf.flip();
                fc.write(buf);
                buf.clear();
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
