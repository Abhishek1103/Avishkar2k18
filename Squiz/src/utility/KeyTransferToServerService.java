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
import java.nio.channels.SocketChannel;
import java.security.PublicKey;

public class KeyTransferToServerService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Socket socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());

                // TODO: Send Keys to server
                dos.writeBoolean(Flags.isTeacher);
                if(Flags.isTeacher) {
                    dos.writeUTF("#NEWTEACHER");
                }else if(Flags.isStudent){
                    dos.writeUTF("#NEWSTUDENT");
                }
                dos.writeUTF(Constants.USERNAME);
                System.out.println("Username sent");

                SaveFile saveFile = new SaveFile();
                Constants.SERVER_PUBIC_KEY_PATH = saveFile.saveFile(Constants.USER_DIR+"keys/"+"ServerPblic.key", ois);
                System.out.println("Server public key received");

//                SendFile sendFile = new SendFile();
//                sendFile.sendFile(Constants.PUBLIC_KEY_PATH, oos);



                PublicKey serverPublicKey = Constants.rsa.readPublicKeyFromFile(Constants.SERVER_PUBIC_KEY_PATH);
                byte[] RSAEncryptedAESKey = Constants.rsa.encryptSecretKey(Constants.aes.getSecretKey(), serverPublicKey);

                oos.writeObject(RSAEncryptedAESKey);
                System.out.println("RSA Encrypted AES key sent to server");


                return null;
            }
        };
    }
}
