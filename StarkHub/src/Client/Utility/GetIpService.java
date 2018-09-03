package Client.Utility;

import Client.DataClasses.Video;
import Client.UI.MediaPlayerAndControlsController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import static Client.Login.Main.HUB_IP;

public class GetIpService extends Service {

    String userName;

    public GetIpService(String userName){
        this.userName = userName;
    }

    @Override
    protected Task<Void> createTask()  {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    Socket socket = new Socket(HUB_IP, 1111);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    dout.writeUTF("#GETIP");
                    dout.writeUTF(userName);

                    MediaPlayerAndControlsController.videoPeerIP = dis.readUTF();

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
