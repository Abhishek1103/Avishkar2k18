package Client.Java;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientPageController implements Initializable {

    @FXML
    ScrollPane clientScrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources)  {


        VBox vbox = new VBox(50);
        vbox.setFillWidth(true);

        try {
            for (int i = 0; i < 20; i++) {
                vbox.getChildren().add(createSection());
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        StackPane pane = new StackPane(vbox);
        pane.setPadding(new Insets(20));
        clientScrollPane.setContent(pane);
    }


    HBox createSection() throws Exception{
        HBox hbox  = new HBox(25);
        

        for(int i=0;i<4;i++){
            AnchorPane anch = FXMLLoader.load(getClass().getResource("../Layouts/testAnchorPane.fxml"));

            ((JFXTextField)(anch.getChildren().get(1))).setText("Vide Info "+i);
            hbox.getChildren().add(anch);
        }

        return hbox;
    }
}
