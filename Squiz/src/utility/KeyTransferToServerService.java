package utility;

import constants.Constants;
import constants.Flags;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.security.PublicKey;

public class KeyTransferToServerService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Socket socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    // TODO: Send Keys to server
                    System.out.println("Bool sent: " + Flags.isTeacher);
                    dos.writeBoolean(Flags.isTeacher);
                    System.out.println("Bool sent: " + Flags.isTeacher);
                    if (Flags.isTeacher) {
                        dos.writeUTF("#NEWTEACHER");
                    } else if (Flags.isStudent) {
                        dos.writeUTF("#NEWSTUDENT");
                    }
                    dos.writeUTF(Constants.USERNAME);
                    System.out.println("Username sent");

                    SaveFile saveFile = new SaveFile();
                    Constants.SERVER_PUBIC_KEY_PATH = saveFile.saveFile(Constants.USER_DIR + "keys/" + "ServerPublic.key", ois);
                    System.out.println("Server public key received");

                    SendFile sendFile = new SendFile();
                    sendFile.sendFile(Constants.PUBLIC_KEY_PATH, oos);
                    System.out.println("Public key sent to server");


                    PublicKey serverPublicKey = Constants.rsa.readPublicKeyFromFile(Constants.SERVER_PUBIC_KEY_PATH);
                    byte[] RSAEncryptedAESKey = Constants.rsa.encryptSecretKey(Constants.aes.getSecretKey(), serverPublicKey);

                    oos.writeObject(RSAEncryptedAESKey);
                    System.out.println("RSA Encrypted AES key sent to server");

                    byte[] RSAEncryptedAESKeyServer = (byte[]) (ois.readObject());
                    System.out.println("RSA Encrypted Server Secret key Received");
                    SecretKey serverSecretKey = Constants.rsa.decryptAESKey(RSAEncryptedAESKeyServer, Constants.rsa.getPrivateKey());
                    System.out.println("Secret Key Decrypted");
                    String encodedServerSecretKey = Constants.aes.getEncodedKey(serverSecretKey);
                    Constants.SERVER_SECRET_KEY_PATH = Constants.USER_DIR + "keys/ServerSecretKey.key";
                    PrintWriter pw = new PrintWriter(new FileWriter(new File(Constants.SERVER_SECRET_KEY_PATH)));
                    pw.println(encodedServerSecretKey);
                    pw.close();
                    System.out.println("Server SecretKey Encoded and stored in file");
                    System.out.println("ServerSecretKey Path: " + Constants.SERVER_SECRET_KEY_PATH);
                }catch(Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
    }
}
