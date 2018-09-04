package Client.UI;

import com.google.common.net.UrlEscapers;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;


import java.net.URL;
import java.util.ResourceBundle;

public class MediaPlayerAndControlsController implements Initializable {

    @FXML
    JFXButton likeButton, dislikeButton, playPauseButton, forwardButton, previousButton;
    @FXML
    MediaView mediaView;
    @FXML
    AnchorPane mediaViewAnchorPane, controlAnchorPane, mainMediaAnchorPane;
    @FXML
    JFXSlider videoSlider, volumeSlider;
    @FXML
    Label videoNameLabel, channelNameLabel;


    public static String videoPeerIP = "";
    public static String VIDEO_URL = "";
    public static String videoPath = "";

    protected MediaPlayer mediaPlayer;

    Timeline slideIn, slideOut;
    double previousVol = 50.0;

    public boolean IS_PLAYING  = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            likeButton.setGraphic(new ImageView(new Image("Client/Resuorces/like-24.png")));
            dislikeButton.setGraphic(new ImageView(new Image("Client/Resuorces/dislike-24.png")));
            forwardButton.setGraphic(new ImageView(new Image("Client/Resuorces/forward-24.png")));
            previousButton.setGraphic(new ImageView(new Image("Client/Resuorces/rewind-24.png")));

        }catch(Exception e){
            e.printStackTrace();
        }

        slideIn = new Timeline();
        slideOut = new Timeline();

        try{
            System.out.println(videoPath);
            VIDEO_URL = "http://"+videoPeerIP+"/Videos/"+ UrlEscapers.urlFragmentEscaper().escape(videoPath.substring(videoPath.lastIndexOf('/')+1));

            VIDEO_URL = "http://"+"172.31.85.85"+"/Videos/"+"Berklee.mp4";

            System.out.println("Video URL: "+VIDEO_URL);
            Media media = new Media(VIDEO_URL);


            // Create the player and set to play automatically.
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);

            // Create the view and add it to the Scene.
            mediaView.setMediaPlayer(mediaPlayer);

            DoubleProperty mvw = mediaView.fitWidthProperty();
            DoubleProperty mvh = mediaView.fitHeightProperty();
//            mvw.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
//            mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            mvw.bind(mediaViewAnchorPane.widthProperty());
            mvh.bind(mediaViewAnchorPane.heightProperty());

            mediaView.setPreserveRatio(false);


            mediaPlayer.setOnReady(() -> {
                videoSlider.setMin(0.0);
                videoSlider.setValue(0.0);
                videoSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
                initVolumeSlider();
            });

            initVideoSlider();
            initAnimations();
            initPlayPauseButton();
            initControlAnchorPane();
            playPauseButton.setGraphic(new ImageView(new Image("Client/Resuorces/pause-24.png")));


        }catch(Exception e){
            e.printStackTrace();
        }


    }


    void initVideoSlider(){

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            videoSlider.setValue(newValue.toSeconds());
        });

        videoSlider.setOnMousePressed(e -> {
            mediaPlayer.seek(Duration.seconds(videoSlider.getValue()));
        });

        videoSlider.setOnMouseDragged(e -> {
            mediaPlayer.seek(Duration.seconds(videoSlider.getValue()));
        });
    }

    void initVolumeSlider(){
//        volumeSlider.setMin(0.0);
//        volumeSlider.setMax(100.0);
//        volumeSlider.setValue(50.0);

        //mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());

        volumeSlider.setOnMousePressed(e -> {
            double diff = volumeSlider.getValue() - previousVol;
            previousVol = volumeSlider.getValue();
            mediaPlayer.setVolume(mediaPlayer.getVolume() + 10*diff);
        });

        volumeSlider.setOnMouseDragged(e -> {
            double diff = volumeSlider.getValue() - previousVol;
            previousVol = volumeSlider.getValue();
            mediaPlayer.setVolume(mediaPlayer.getVolume() + 10*diff);
        });
    }

    void initPlayPauseButton(){
        playPauseButton.setOnAction(e -> {
            if(IS_PLAYING){
                playPauseButton.setGraphic(new ImageView(new Image("Client/Resuorces/play-24.png")));
                mediaPlayer.pause();
                IS_PLAYING = false;
            }else{
                playPauseButton.setGraphic(new ImageView(new Image("Client/Resuorces/pause-24.png")));
                mediaPlayer.play();
                IS_PLAYING = true;
            }
        });
    }

    void initControlAnchorPane(){
        mainMediaAnchorPane.setOnMouseEntered(e -> {
            slideIn.play();
        });

        mainMediaAnchorPane.setOnMouseExited(e->{
            slideOut.play();
        });
    }

    void initAnimations(){
        int h = (int)mediaViewAnchorPane.getHeight();
        int w = (int)mediaViewAnchorPane.getWidth();

        slideIn.getKeyFrames().addAll(
          new KeyFrame(new Duration(0),
                  new KeyValue(controlAnchorPane.translateYProperty(), h+100),
                  new KeyValue(controlAnchorPane.opacityProperty(), 0.0)
                  ),
          new KeyFrame(new Duration(300),
                  new KeyValue(controlAnchorPane.translateYProperty(),h),
                  new KeyValue(controlAnchorPane.opacityProperty(), 0.7)
                  )
        );

        slideOut.getKeyFrames().addAll(
                new KeyFrame(new Duration(0),
                        new KeyValue(controlAnchorPane.translateYProperty(), h),
                        new KeyValue(controlAnchorPane.opacityProperty(), 0.7)
                ),
                new KeyFrame(new Duration(300),
                        new KeyValue(controlAnchorPane.translateYProperty(),h+100),
                        new KeyValue(controlAnchorPane.opacityProperty(), 0.0)
                )
        );
    }

    public void increaseLikes(){

    }

    public void decreaseLikes(){

    }

    public void subscribe(){

    }

    public void previousButtonClicked(){
        System.out.println("Previous button clicked");
        mediaPlayer.seek(Duration.seconds(mediaPlayer.getCurrentTime().toSeconds() - 30.0));
    }

    public void forwardButtonClicked(){
        System.out.println("forward button clicked");
        mediaPlayer.seek(Duration.seconds(mediaPlayer.getCurrentTime().toSeconds()+30.0));
    }

}
