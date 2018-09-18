package Client.UI;

import Client.Login.Main;
import Client.Utility.CommentPostService;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class CommentSectionController implements Initializable {

    @FXML
    JFXTextArea commentTextArea;

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
    }
}
