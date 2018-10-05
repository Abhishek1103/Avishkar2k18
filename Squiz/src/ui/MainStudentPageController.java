package ui;

import com.jfoenix.controls.JFXButton;
import constants.Constants;
import data.Teacher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainStudentPageController implements Initializable {
    @FXML
    JFXButton subjectsButton, teachersButton;

    @FXML
    AnchorPane contentAnchorPane;

    protected static HashMap<String, Teacher> stringTeacherMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Constants.TEACHER_MAP = new HashMap<>();
        stringTeacherMap = new HashMap<>();
        // TODO: Show dashboard

    }



    public void subjectsButtonClicked(){

    }

    public void teachersButtonClicked(){
        try{
            AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/showTeachersLayout.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addSolutionButtonClicked(){

    }

    public void showResultsButtonClicked(){

    }

    public void messageButtonClicked() throws Exception{
        System.out.println("Message button clicked");
        Stage playWindow = new Stage();
        playWindow.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/chatPreviewLayout.fxml"));
        Scene sc = new Scene(root);
        playWindow.setScene(sc);
        playWindow.showAndWait();
    }

    public void homeButtonClicked(){

    }
}
