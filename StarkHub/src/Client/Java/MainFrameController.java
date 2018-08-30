package Client.Java;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class MainFrameController {

    @FXML
    JFXButton btn;
    @FXML
    AnchorPane contentAnchorPane;

    @FXML
    protected void btnClicked() throws Exception{
            AnchorPane pane  = FXMLLoader.load(getClass().getResource("../Layouts/clientPage.fxml"));

            contentAnchorPane.getChildren().setAll(pane);
    }
}
