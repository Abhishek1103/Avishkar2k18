package utility;

import constants.Constants;
import constants.Flags;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ui.MainTeacherPageController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SendMessageService extends Service {

    private String recepient;
    private String message;

    public SendMessageService(String receipient, String message){
        this.recepient = receipient;
        this.message = message;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    Socket sock = new Socket(Constants.SERVER_IP, 13001);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    dout.writeUTF("#SENDMESSAGE");
                    dout.writeUTF(Constants.USERNAME);
                    dout.writeUTF(recepient);
                    dout.writeUTF(message);
                    dout.writeBoolean(!Flags.isTeacher);

                    ois.close();
                    oos.close();
                    sock.close();

                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
    }
}
