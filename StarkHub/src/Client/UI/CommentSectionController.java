package Client.UI;

import Client.Login.Main;
import Client.Utility.CommentPostService;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CommentSectionController implements Initializable {

    @FXML
    JFXTextArea commentTextArea;

    @FXML
    AnchorPane commentSectionAnchorPane;

    String peerIP,videoName,commentorName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Comment Section initialised+" + "");
        videoName = MediaPlayerAndControlsController.videoPath.substring(
                MediaPlayerAndControlsController.videoPath.lastIndexOf('/')
        );
    }

    public void cancelButtonClicked(){
        commentTextArea.clear();
    }

    public void commentButtonClicked(){
        // TODO: Run a service
        String comment = commentTextArea.getText();
        commentTextArea.clear();

        CommentPostService cps = new CommentPostService(peerIP, comment,videoName, Main.USERNAME);
        cps.start();

        // TODO: Post comment on UI

        try {
            VBox vbox = (VBox) commentSectionAnchorPane.getParent();
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/commentItem.fxml"));
            JFXTextArea ta = (JFXTextArea) pane.getChildren().get(0);
            ta.setPromptText(Main.USERNAME);
            ta.setText(comment);
            ta.setEditable(false);
            vbox.getChildren().add(3, pane);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
