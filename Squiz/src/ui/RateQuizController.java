package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class RateQuizController implements Initializable {

    @FXML
    AnchorPane mainAnchorPane;
    @FXML
    JFXRadioButton greatRadioButton, goodRadioButton, averageRadioButton, notGoodRadioButton, badRadioButton;
    @FXML
    Label tickLabel;
    @FXML
    ImageView tickImageView;
    @FXML
    JFXButton submitButton;

    ToggleGroup toggleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleGroup = new ToggleGroup();
        goodRadioButton.setToggleGroup(toggleGroup);
        greatRadioButton.setToggleGroup(toggleGroup);
        averageRadioButton.setToggleGroup(toggleGroup);
        notGoodRadioButton.setToggleGroup(toggleGroup);
        badRadioButton.setToggleGroup(toggleGroup);
    }

    public void submitButtonClicked() {
        int rating = 3;
        if(goodRadioButton.isSelected()){
            rating = 4;
        }else if(greatRadioButton.isSelected()){
            rating = 5;
        }else if(averageRadioButton.isSelected()){
            rating = 3;
        }else if(notGoodRadioButton.isSelected()){
            rating = 2;
        }else if(badRadioButton.isSelected()){
            rating = 1;
        }else{
            JFXPopup pop = new JFXPopup();
            Label l = new Label("Select a rating");
            pop.setPopupContent(l);
            pop.show(submitButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }

        MainQuizFrameController.rating = rating;
        ((Stage)mainAnchorPane.getScene().getWindow()).close();
    }


}