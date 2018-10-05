package utility;

import constants.Constants;
import constants.Flags;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import login.LayoutController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginTimeService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    Socket socket = new Socket(Constants.SERVER_IP, 7001);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    dos.writeBoolean(Flags.isTeacher);
                    dos.writeUTF("#LOGIN");
                    System.out.println("Sent #LOGIN Flag");
                    if(dis.readBoolean()){
                        int n = dis.readInt();
                        for(int i=0;i<n;i++){
                            String s = dis.readUTF();
                            LayoutController.messageUsers.put(s,s);
                        }
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
    }
}
