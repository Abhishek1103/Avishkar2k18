package Test;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.FileInputStream;


public class VideoPlayTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new Group(), 600, 400);
        stage.setScene(scene);

        // Name and display the Stage.
        stage.setTitle("Hello Media");
        stage.show();

        // Create the media source.
        String source = "http://localhost:8000/Videos/Screencast_Friday%2030%20March%202018_08:01:27%20%20IST.webm";
         //source = getParameters().getRaw().get(0);
        System.out.println(source);
        Media media = new Media(source);


        // Create the player and set to play automatically.
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        // Create the view and add it to the Scene.
        MediaView mediaView = new MediaView(mediaPlayer);

        DoubleProperty mvw = mediaView.fitWidthProperty();
        DoubleProperty mvh = mediaView.fitHeightProperty();
        mvw.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        mediaView.setPreserveRatio(false);

        ((Group) scene.getRoot()).getChildren().add(mediaView);
    }
}
