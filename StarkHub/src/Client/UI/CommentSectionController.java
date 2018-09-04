package Client.UI;

import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class CommentSectionController implements Initializable {

    @FXML
    JFXTextArea commentTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void cancelButtonClicked(){
        commentTextArea.clear();
    }

    public void commentButtonClicked(){
        // TODO: Run a service
        String comment = commentTextArea.getText();
        commentTextArea.clear();
    }
}
