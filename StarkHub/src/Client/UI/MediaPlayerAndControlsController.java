package Client.UI;

import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.net.URL;
import java.util.ResourceBundle;

public class MediaPlayerAndControlsController implements Initializable {

    @FXML
    JFXButton likeButton, dislikeButton;
    @FXML
    MediaView mediaView;
    @FXML
    AnchorPane mediaViewAnchorPane;

    public static String videoPeerIP = "";
    public static String VIDEO_URL = "";
    public static String videoPath = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            likeButton.setGraphic(new ImageView(new Image("Client/Resuorces/like-24.png")));
            dislikeButton.setGraphic(new ImageView(new Image("Client/Resuorces/dislike-24.png")));

            

        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            System.out.println(videoPath);
            VIDEO_URL = "http://"+videoPeerIP+"/Videos/"+videoPath.substring(videoPath.lastIndexOf('/')+1);
            System.out.println("Video URL: "+VIDEO_URL);
            Media media = new Media(VIDEO_URL);


            // Create the player and set to play automatically.
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);

            // Create the view and add it to the Scene.
            mediaView.setMediaPlayer(mediaPlayer);

            DoubleProperty mvw = mediaView.fitWidthProperty();
            DoubleProperty mvh = mediaView.fitHeightProperty();
            mvw.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            mediaView.setPreserveRatio(false);
        }catch(Exception e){

        }
    }

    public void increaseLikes(){

    }

    public void decreaseLikes(){

    }

    public void subscribe(){

    }

}
