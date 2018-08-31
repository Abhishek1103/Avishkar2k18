package Client.UI;

import Client.DataClasses.Video;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.deploy.util.SessionState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static Client.Login.Main.*;

public class AddChannelController implements Initializable {

    @FXML
    JFXButton addVideosButton, createChannelButton, discardButton;

    @FXML
    JFXTextField channelNameTxt;

    @FXML
    AnchorPane addChannelAnchorPane;

    ArrayList<Video> videoList;
    FileChooser fileChooser ;

    String userHome;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        videoList = new ArrayList<>();
        fileChooser = new FileChooser();
        userHome = System.getProperty("user.home");
    }


    public  void createChannel(){
        String channelName = channelNameTxt.getText();
        File channelFile = new File(userHome+"/.starkhub/"+channelName);

        if(channelFile.exists()){
            System.out.println("Error...Select another Channel name");
        }else{
            try{
                PrintWriter pw = new PrintWriter(channelFile);
                for(Video v: videoList){
                    pw.println(v.getVideoName());
                }
                pw.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                notifyNewChannel(channelName);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    // Notifying Hub
    void notifyNewChannel(String channelName){
        try{
            Socket socket = new Socket(HUB_IP, PORT);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            dout.writeUTF("#MAKECHANNEL");
            dout.writeUTF(USERNAME);
            dout.writeUTF(channelName);

            if(!(videoList.isEmpty())){

                for(Video v: videoList){
                    dout.writeUTF("#ADDVIDEOINCHANNEL");
                    dout.writeUTF(USERNAME);
                    dout.writeUTF(channelName);

                    dout.writeUTF(v.getVideoPath());
                    String outPath = generateThumbnail(v.getVideoPath());
                    v.setThumbnail(outPath);
                    oos.writeObject(v.getThumbnail());
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void addVideos(){

        File file = fileChooser.showOpenDialog((Stage)(addChannelAnchorPane.getScene().getWindow()));
        Video v = new Video(file.getName(),null, file.getAbsolutePath());
        videoList.add(v);
    }

    public void discard(){

    }

    // Generate Thumbnails from videos
    String generateThumbnail(String path) throws Exception{

            String time = "00:00:20";
            String outPath = System.getProperty("user.home") + "/.starkhub/out.png";
            String command = "ffmpeg "+" -ss "+time+" -i "+path+" -vf scale=-1:120  -vcodec png "+outPath;
            System.out.println(command);
            Runtime run = Runtime.getRuntime();
            Process p = run.exec(command);
            p.waitFor();

        return outPath;
    }

}
