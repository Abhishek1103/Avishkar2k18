package Client.Utility;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ThumbnailReceiverService extends Service<Void> {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{
                    System.out.println("Listening for thumbnails");
                    ServerSocket serverSocket = new ServerSocket(11234);
                    Socket sock = serverSocket.accept();

                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    int numberOfThumbs = ois.readInt();
                    System.out.println("number of thumbs: "+numberOfThumbs );
                    for(int i=0;i<numberOfThumbs;i++){
                        String thumbName = ois.readUTF();
                        String savePath = System.getProperty("user.home")+"/starkhub/temp/"+thumbName;
                        (new SaveFile()).saveFile(savePath,ois);
                    }
                    //serverSocket.close();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        };

    }

}