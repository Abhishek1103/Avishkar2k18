package utility;

import constants.Constants;
import data.Question;
import encryption.AES;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class SubmitQuizService extends Service {

    private String quizName;
    private String teacherName;
    private String studentName;
    private int rating;
    private HashMap<Question, ArrayList<Integer>> questionresponseMap;

    public SubmitQuizService(String quizName, String teacherName, String studentName, int rating, HashMap<Question, ArrayList<Integer>> map){
        this.quizName = quizName;
        this.teacherName = teacherName;
        this.studentName =studentName;
        this.rating = rating;
        this.questionresponseMap = map;
    }

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

                    System.out.println("Starting Encryption");
                    ByteArrayOutputStream baos = null;
                    {
                        AES aes = new AES();
                        BufferedReader br = new BufferedReader(new FileReader(Constants.SERVER_SECRET_KEY_PATH));
                        SecretKey serverSecretKey = aes.decodeKey(br.readLine());
                        br.close();
                        aes.setSecretKey(serverSecretKey);
                        baos = new ByteArrayOutputStream();
                        aes.encryptWithAES(questionresponseMap, baos);
                    }
                    System.out.println("Encryption done");

                    dout.writeBoolean(false);
                    System.out.println("Boolean sent: "+false );

                    dout.writeUTF("#SUBMITQUIZ");
                    System.out.println("Flag sent: "+"#SUBMITQUIZ");

                    dout.writeUTF(quizName);
                    dout.writeUTF(teacherName);
                    dout.writeUTF(studentName);
                    System.out.println("Sent Names");

                    dout.writeInt(rating);
                    System.out.println("Rating: "+rating);

                    System.out.println("Received farzi boolean: "+dis.readBoolean());

                    System.out.println("Sending encrypted data:" );
                    oos.writeObject(baos.toByteArray());
                    System.out.println("Sent Encrypted data");



                }catch (Exception e){
                    e.printStackTrace();
                }



                return null;
            }
        };
    }
}
