package Client.UI;


import Client.Login.Main;
import Client.Utility.GetIpService;
import com.jfoenix.controls.JFXButton;
import hubFramework.Video;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static Client.UI.MediaPlayerAndControlsController.videoPeerIP;

public class ShowHistoryController implements Initializable {

    // TODO: Could be used both for History and Trending


    @FXML
    ScrollPane clientScrollPane;


    private ArrayList<Video> list;
    VBox vbox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        if(MainPageController.IS_HISTORY){
            // TODO: History stuff
            try {
                initHistory();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(MainPageController.IS_TRENDING){
            // TODO: Trending stuff
        }

    }


    void initHistory() throws Exception{
        list = MainPageController.historyList;

        vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        int count = 50;
        for(Video v: list){
            if(count < 0)
                break;
            count--;

            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/historyItem.fxml"));
            JFXButton button = (JFXButton) pane.getChildren().get(1);
            ImageView imgv = (ImageView)(pane.getChildren().get(0));

            button.setText(v.getVideoName());
            String thumbNailPath = v.getThumbnailPath();
            imgv.setImage(new Image(new FileInputStream(thumbNailPath)));

            button.setOnAction(e -> {
                String userName = v.getOwnerName();
                String channel = v.getChannelName();

                MediaPlayerAndControlsController.ownerName = userName;
                MediaPlayerAndControlsController.channelName = channel;
                MediaPlayerAndControlsController.videoPath = v.getPathOfVideo();
                VideoPlayerController.videoName = v.getVideoName();

                GetIpService gis = new GetIpService(userName, v.getVideoName(), channel);
                gis.start();

                gis.setOnSucceeded(event -> {
                    Stage playWindow = new Stage();
                    playWindow.initModality(Modality.APPLICATION_MODAL);

                    try{
                        Parent parent = FXMLLoader.load(getClass().getResource("../Layouts/videoPlayer.fxml"));
                        Scene sc = new Scene(parent);
                        playWindow.setScene(sc);
                        //playWindow.showAndWait();
                        playWindow.show();

                        playWindow.setOnCloseRequest(evt -> {
                            System.out.println("Video Player SetonClose calles");
                            MediaPlayerAndControlsController.mediaPlayer.stop();
                            try {
                                Socket sock = new Socket(videoPeerIP, 15001);
                                DataInputStream dis = new DataInputStream(sock.getInputStream());
                                DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                                ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                                dout.writeUTF(Main.USERNAME);
                                dis.readBoolean();
                                dout.writeUTF("#DISCONNECT");

                                System.out.println("VideoPlayer Set on Close request exiting");

                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        });


                    }catch (Exception ex){

                    }

                });


            });

            vbox.getChildren().add(0,pane);

        }
        clientScrollPane.setContent(vbox);
    }
}
