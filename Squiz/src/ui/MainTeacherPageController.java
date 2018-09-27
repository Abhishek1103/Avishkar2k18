package ui;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainTeacherPageController implements Initializable {

    @FXML
    JFXButton homeButton, showResultsButton,addSubjectButton, addQuizButton, addSolutionButton, messagesButton;

    @FXML
    AnchorPane contentAnchorPane;

    public static String currentChatUser = "";
    public static HashMap<String, String> queryUsername;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initializer strted");
        try{
            AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/firstTeacherPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void homeButtonClicked(){
        // TODO:Logic
    }

    public void addSubjectButtonClicked(){
        System.out.println("Add Subject button clicked");
        try{
            AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/addSubjectLayout.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addQuizButtonClicked() {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/addQuizLayout.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addSolutionButtonClicked(){
        // TODO:Logic
    }

    public void showResultsButtonClicked(){
        // TODO:Logic

        try{
            AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/ShowResultsToTeacherLayout.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void messagesButtonClicked() throws Exception{
        // TODO:Logic
        System.out.println("Message button clicked");
        Stage playWindow = new Stage();
        playWindow.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/chatPreviewLayout.fxml"));
        Scene sc = new Scene(root);
        playWindow.setScene(sc);
        playWindow.showAndWait();

    }


}

