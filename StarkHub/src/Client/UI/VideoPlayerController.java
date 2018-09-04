package Client.UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class VideoPlayerController implements Initializable {

    @FXML
    ScrollPane scrollPane;

    VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            init();
        }catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        // TODO: Service to receive Comments
    }



    void init() throws Exception{
        vbox = new VBox(10);
        vbox.setPrefWidth(1280);
        vbox.setMaxWidth(1280);

        AnchorPane mediaControlPane = FXMLLoader.load(getClass().getResource("../Layouts/mediaPlayerAndControls.fxml"));
        vbox.getChildren().add(mediaControlPane);

        Separator separator = new Separator();
        separator.setPrefWidth(1270);
        separator.setHalignment(HPos.CENTER);
        vbox.getChildren().add(separator);

        AnchorPane commentsSectionPane = FXMLLoader.load(getClass().getResource("../Layouts/commentsSectionLayout.fxml"));
        vbox.getChildren().add(commentsSectionPane);
    }

}
