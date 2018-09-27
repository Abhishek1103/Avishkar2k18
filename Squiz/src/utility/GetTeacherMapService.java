package utility;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import constants.Constants;
import constants.Flags;
import encryption.AES;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class GetTeacherMapService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    Socket sock = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    dout.writeBoolean(Flags.isTeacher);
                    System.out.println("Boolean Sent: " + Flags.isTeacher);

                    dout.writeUTF("#FETCHTEACHERLIST");
                    System.out.println("Flag sent: "+"#FETCHTEACHERLIST");

                    if(dis.readBoolean()){

                        byte[] encryptedTeacherMap = (byte[])ois.readObject();
                        BufferedReader br = new BufferedReader(new FileReader(Constants.SERVER_SECRET_KEY_PATH));
                        String encodedSecretKey = br.readLine();
                        SecretKey secretKey = Constants.aes.decodeKey(encodedSecretKey);
                        ByteArrayInputStream bais = new ByteArrayInputStream(encryptedTeacherMap);
                        HashMap<String, byte[]> map = (HashMap<String, byte[]>) Constants.aes.decryptWithAES(bais, secretKey);

                        Constants.TEACHER_MAP = map;
                        System.out.println(Constants.TEACHER_MAP);

                    }else{
                        System.out.println("There are no teachers");
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
    }
}
