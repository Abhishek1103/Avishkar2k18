package Client.UI;


import Client.HubServices.Video;
import Client.Login.Main;
import Client.Utility.AddToWatchLater;
import Client.Utility.ThumbnailReceiverService;
import Client.Utility.ThumnailReceiverThread;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class ClientPageController implements Initializable {

    @FXML
    ScrollPane clientScrollPane;

    //ArrayList<Client.HubServices.Video> receivedVideos, receivedVideosRecommended;
    HashMap<String, Client.HubServices.Video> receivedVideos, receivedVideosRecommended;

    public HashMap<String, ImageView > nameImageViewMap;

    @Override
    public void initialize(URL location, ResourceBundle resources)  {

        receivedVideos = null;
        receivedVideosRecommended = null;
        nameImageViewMap = new HashMap<>();


        if(Client.Login.Main.isNewUser){
            try {
                Socket sock = new Socket(Main.HUB_IP, Main.PORT);
                DataInputStream dis = new DataInputStream(sock.getInputStream());
                DataOutputStream dout = new DataOutputStream(sock.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                dout.writeUTF("#NEWUSER");
                System.out.println("#NEWUSER flag sent");
                dout.writeUTF(Main.USERNAME);
                System.out.println("Sent username: "+ Main.USERNAME);
                String msg;
                if((msg = dis.readUTF()).equals( "#NOFILES")){
                    System.out.println("Received #NOFILES");
                    makeUpUI(receivedVideos, receivedVideosRecommended);
                    //TODO NOTHING
                }else if(msg.equals("#SENDING")){
                    //TODO: Some Operation

                    //new Thread(new ThumnailReceiverThread()).start();

                    ThumbnailReceiverService service = new ThumbnailReceiverService();
                    service.start();
                    service.setOnSucceeded(e -> putOnThumbnails());

                    receivedVideosRecommended = (HashMap<String, Client.HubServices.Video>) ois.readObject();
                     receivedVideos = (HashMap<String, Client.HubServices.Video>) ois.readObject();

                     makeUpUI(receivedVideos, receivedVideosRecommended);


                }
            }catch(Exception e){
                makeUpUI(receivedVideos, receivedVideosRecommended);
                e.printStackTrace();
            }
        }else{

            // TODO: if not NEWUSER
        }


    }


    public void makeUpUI(HashMap<String, Client.HubServices.Video> receivedVideos, HashMap<String, Client.HubServices.Video> receivedVideosRecommended){
        VBox vbox = new VBox(50);
        vbox.setFillWidth(true);

        try {
            Reflection reflection = new Reflection();
            reflection.setFraction(0.50);

            if(receivedVideosRecommended!=null){
                Iterator it = receivedVideosRecommended.entrySet().iterator();
                Label l = new Label("Recommeded Videos");
                l.setFont(Font.font(18));
                l.setEffect(reflection);
                vbox.getChildren().add(l);
                int k=0;
                for(int i=0;i<2 && it.hasNext();i++){
                    HBox h = createSection();
                    for(int j=0;j<4 && it.hasNext();j++){
                        String name = ((Map.Entry) it.next()).getKey().toString();
                        AnchorPane p = createSingleItem((name));
                        h.getChildren().add(p);
                        k++;
                    }
                    vbox.getChildren().add(h);
                }

            }

            if(receivedVideos!=null){
                Iterator it = receivedVideosRecommended.entrySet().iterator();
                Label l = new Label("More Videos");
                l.setFont(Font.font(18));
                l.setEffect(reflection);
                vbox.getChildren().add(l);
                int k=0;

                for(int i=0;i<2 && it.hasNext();i++){
                    HBox h = createSection();
                    for(int j=0;j<4 && it.hasNext();j++){
                        String name = ((Map.Entry) it.next()).getKey().toString();
                        AnchorPane p = createSingleItem((name));
                        h.getChildren().add(p);
                        k++;
                    }
                    vbox.getChildren().add(h);
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }
        StackPane pane = new StackPane(vbox);
        pane.setPadding(new Insets(15));
        clientScrollPane.setContent(pane);
    }



    // length of ArrayList should be 4
    HBox createSection() throws Exception{
        HBox hbox  = new HBox(15);
        return hbox;
    }

    AnchorPane createSingleItem( String videoName) throws Exception{
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("../Layouts/videoItemLayout.fxml"));
        ((Label)(anchorPane.getChildren().get(1))).setText(videoName);
        Label l = (Label)(anchorPane.getChildren().get(1));
        JFXButton mainButton = (JFXButton)(anchorPane.getChildren().get(2));
        JFXButton watchLaterButton = (JFXButton)(anchorPane.getChildren().get(4));
        ImageView imageView = (ImageView)(anchorPane.getChildren().get(0));

        nameImageViewMap.put(videoName, imageView);


        mainButton.setOnAction(e -> {
            System.out.println("Video button clicked");
            String vidName = l.getText();

            if(receivedVideosRecommended.containsKey(vidName)){
                Video v = receivedVideosRecommended.get(vidName);

                // TODO: open player and contact server
            }
            else if(receivedVideos.containsKey(vidName)){
                Video v = receivedVideos.get(vidName);

                // TODO: open player and contact server
            }

        });


        watchLaterButton.setOnAction(e -> {
            System.out.println("Watch Later clicked");
            Video v = null;
            String vidName = l.getText();
            if(receivedVideosRecommended.containsKey(vidName)){
                v = receivedVideosRecommended.get(vidName);
            }
            else if(receivedVideos.containsKey(vidName)){
                v = receivedVideos.get(vidName);
            }
            if(v!=null) {
                AddToWatchLater addToWatchLater = new AddToWatchLater(v);
                new Thread(addToWatchLater).run();
                System.out.println("Thread started");
            }else{
                System.out.println("Video object is null");

            }

        });


        l.setWrapText(true);
        return anchorPane;
    }


    void putOnThumbnails(){
        String path = System.getProperty("user.home")+"/starkhub/temp";
        File f = new File(path);
        File[] list = f.listFiles();

        for(File thumb: list){
            if(thumb.isFile()){
                String vidName = thumb.getName().substring(0, thumb.getName().lastIndexOf('.'));
                if(nameImageViewMap.containsKey(vidName)){
                    ImageView i = nameImageViewMap.get(vidName);

                    Platform.runLater(() -> {
                        try{
                            i.setImage(new Image(new FileInputStream(f)));
                        }catch(Exception e){
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
            }
        }

    }

}
