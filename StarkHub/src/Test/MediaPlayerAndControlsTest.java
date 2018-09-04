package Test;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MediaPlayerAndControlsTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../Client/Layouts/mediaPlayerAndControls.fxml"));
        primaryStage.setTitle("Stark Hub");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        Client.UI.MediaPlayerAndControlsController.videoPeerIP = "172.31.85.85";
        Client.UI.MediaPlayerAndControlsController.videoPath = "Berklee.mp4";
    }


    public static void main(String[] args) {
        launch(args);
    }
}
