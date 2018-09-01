package Client.UI;

import Client.DataClasses.Video;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.sun.deploy.util.SessionState;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import static Client.Login.Main.*;

public class AddChannelController implements Initializable {

    @FXML
    JFXButton addVideosButton, createChannelButton, discardButton;

    @FXML
    JFXTextField channelNameTxt;

    @FXML
    AnchorPane addChannelAnchorPane;

    @FXML
    JFXTextArea tagsTextArea;

    @FXML
    JFXListView<Label> videoListView;

    @FXML
    ScrollPane scrollPane;

    HBox hbox;

    ArrayList<Video> videoList;
    FileChooser fileChooser ;

    String userHome;

    String outPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        videoList = new ArrayList<>();
        fileChooser = new FileChooser();
        userHome = System.getProperty("user.home");
        videoListView.setExpanded(true);
        videoListView.setPadding(new Insets(10));
        videoListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        hbox = new HBox(25);
        hbox.setPadding(new Insets(10));
        scrollPane.setContent(hbox);
        outPath = userHome+"/out.png";
    }


    public  void createChannel(){
        String channelName = channelNameTxt.getText();
        File channelFile = new File(userHome+"/.starkhub/"+channelName);

        if(channelFile.exists()){
            System.out.println("Error...Select another Channel name");
        }else{
            try{
                channelFile.createNewFile();
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(channelFile)));

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
                    oos.writeObject(v.getTags());

//                    String outPath = generateThumbnail(v.getVideoPath());
//                    v.setThumbnail(outPath);

                    oos.writeObject(v.getThumbnail());

                    System.out.println("ThumbNail Sent");
                    }
            }

            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void addVideos(){

        try {
            fileChooser.setInitialDirectory(new File(userHome));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Videos", "*.mp4","*.mkv","*.avi","*.webm"),
                    new FileChooser.ExtensionFilter("All","*.*")
            );
            File file = fileChooser.showOpenDialog((Stage) (addChannelAnchorPane.getScene().getWindow()));
            Video v = new Video(file.getName(), null, file.getAbsolutePath());
            videoList.add(v);

            videoListView.getItems().add(new Label(file.getName()));

            File f = new File(outPath);
            if(f.exists() && f.delete()){
                System.out.println("ThumbNail deleted Successfully");
            }else System.out.println("Failed to delete Thumbnail");

            String outPath = generateThumbnail(v.getVideoPath());
            v.setThumbnail(outPath);



            AnchorPane thumbPane = FXMLLoader.load(getClass().getResource("../Layouts/videoThumbnailView.fxml"));
            ((ImageView)thumbPane.getChildren().get(0)).setImage(v.getThumbnail());
            ((JFXButton)thumbPane.getChildren().get(1)).setText(v.getVideoName());
            hbox.getChildren().add(thumbPane);


        }catch(Exception e){
            System.out.println(e.getMessage());
            //System.out.println("No file Selected");
            e.printStackTrace();
        }
    }

    public void discard(){
        /*
            Load back the main UI
         */
    }

    // Generate Thumbnails from videos
    String generateThumbnail(String path) throws Exception{

            String time = "00:00:20";
           // String outPath = System.getProperty("user.home") + "/Desktop/out.png";
            String command = "ffmpeg "+" -ss "+time+" -i "+path+" -vf scale=-1:120  -vcodec png "+outPath;
            System.out.println(command);
            Runtime run = Runtime.getRuntime();
            Process p = run.exec(command);
            System.out.println(p.getInputStream().read());

            p.waitFor();
        System.out.println("Thumbnail Created");

        return outPath;
    }

    // Adding tags to selected Videos
    public void addTagsToVideo(){

        System.out.println("Add Tags Clicked");

        ArrayList<String > videoNames = new ArrayList<>();

        ObservableList<Label> selectedLabels =  videoListView.getSelectionModel().getSelectedItems();
        if(selectedLabels.isEmpty()) {
            System.out.println("No Videos Selected..!!");
            return;
        }

        for(Label l: selectedLabels){
            videoNames.add(l.getText());
        }

        System.out.println("VideoNames: "+videoNames);

        ArrayList<String> tags = getTagsFromTextArea();

        if(tags.isEmpty()) {
            System.out.println("No tags specified");
            return;
        }

        for(String videoName: videoNames) {
            for (Video v : videoList) {
                if (videoName.equals(v.getVideoName())) {
                    v.setTags(tags);
                }
            }
        }

        System.out.println("Tags Added");

        videoListView.getSelectionModel().clearSelection();
    }


    ArrayList<String> getTagsFromTextArea(){
        String rawString = tagsTextArea.getText();
        StringTokenizer tokenizer = new StringTokenizer(rawString, ",");
        ArrayList<String> extractedTags = new ArrayList<>();

        while(tokenizer.hasMoreTokens()){
            extractedTags.add((tokenizer.nextToken()).trim());
        }

        return extractedTags;
    }

}
