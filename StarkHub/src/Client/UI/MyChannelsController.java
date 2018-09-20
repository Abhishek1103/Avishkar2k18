package Client.UI;

import Client.DataClasses.Channel;
import Client.DataClasses.Video;
import Client.Login.Main;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MyChannelsController implements Initializable {

    HashMap<String,Channel> map;
    VBox vBox;

    @FXML
    ScrollPane  clientScrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            init();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void init() throws Exception{
        map = MainPageController.myChannelMap;
        vBox = new VBox(10);
        vBox.setPadding(new Insets(10));
//
//        File[] thumbList = new File(System.getProperty("user.home")+"/starkhub/"+ Main.USERNAME+"/thumbnails/").listFiles();
//        // Name of thumbnail: videoname.png (not videoName.mp4.png)

        // TODO: GET Number of likes, comments, views from hub

        for(Map.Entry<String, Channel> entry: map.entrySet()){

            Channel c = entry.getValue();
            Label l = new Label(c.getChannelName());
            l.setFont(new Font(20));
            vBox.getChildren().add(l);

            AnchorPane content = FXMLLoader.load(getClass().getResource("../Layouts/myChannelItemContainer"));
            JFXListView<AnchorPane> listView = (JFXListView<AnchorPane>) content.getChildren().get(0);


            for(Video v: c.getVideoList()) {

                AnchorPane item = FXMLLoader.load(getClass().getResource("../Layouts/myChannelItem"));
                ImageView imgv = (ImageView) (item.getChildren().get(0));
                Label videoNameLabel = (Label) (item.getChildren().get(1));
                Label likesLabel = (Label) (item.getChildren().get(2));
                Label commentsLabel = (Label) (item.getChildren().get(3));
                Label viewsLabel = (Label) (item.getChildren().get(4));

                imgv.setImage(new Image(new FileInputStream(new File(v.getThumbnailPath()))));
                videoNameLabel.setText(v.getVideoName());

                listView.getItems().add(item);
            }
            vBox.getChildren().add(listView);
            // TODO: Setup on UI
        }

        clientScrollPane.setContent(vBox);

    }


}
