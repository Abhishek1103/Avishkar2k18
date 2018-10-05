package utility;

import constants.Constants;
import constants.Flags;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NotifyServerAddQuizService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    Socket socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());


                    dout.writeBoolean(Flags.isTeacher);
                    dout.writeUTF("#ADDQUIZ");



                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
    }
}
