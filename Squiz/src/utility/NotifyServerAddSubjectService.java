package utility;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import constants.Constants;
import constants.Flags;
import data.Subject;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NotifyServerAddSubjectService extends Service {

    private Subject subject;

    public NotifyServerAddSubjectService(Subject _subject){
        this.subject = _subject;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{
                    Socket sock = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                    DataInputStream dis= new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    dout.writeBoolean(Flags.isTeacher);
                    dout.writeUTF("#ADDSUBJECT");
                    dout.writeUTF(Constants.USERNAME);


//                    ByteOutputStream baos = new ByteOutputStream();
//                    Constants.aes.encryptWithAES(subject, baos);

                    oos.writeObject(subject);

                    System.out.println("Subject Object Written");


                }catch(Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }
}
