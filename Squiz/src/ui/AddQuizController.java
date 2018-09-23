package ui;

import com.jfoenix.controls.*;
import constants.Constants;
import data.Question;
import data.Quiz;
import data.Section;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddQuizController implements Initializable {

    @FXML
    JFXButton addQuestionButton, prepareQuizButton;

    @FXML
    JFXTextField quizNameTxt;

    @FXML
    JFXComboBox<String> subjectNameComboBox;

    @FXML
    JFXTextArea quizDescTxtArea;


    protected static ArrayList<Pair<String, Section>> sectionMap;

    protected static String quizName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        // TODO: Initialize subjectNameComboBox
    }


    public void prepareQuizButtonClicked(){
        String quizName = quizNameTxt.getText().trim();
        String quizDesc = quizDescTxtArea.getText().trim();

        String subName = subjectNameComboBox.getSelectionModel().getSelectedItem();

        int marks = 100;
        // TODO: Calculate total marks of the quiz
        int totalTime = 60;
        // TODO: Calculate totalTime

        Quiz newQuiz = new Quiz(quizName, subName, marks, totalTime, quizDesc);
        newQuiz.setSectionArrayListOfPair(sectionMap);

        Constants.SUBJECT_MAP.get(subName).getQuizHashMap().put(quizName, newQuiz);

        clear();

    }

    public void addSectionButtonClicked() throws Exception{
        Stage playWindow = new Stage();
        playWindow.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("layouts/addSectionLayout.fxml"));
        Scene sc = new Scene(root);
        playWindow.setScene(sc);
        playWindow.showAndWait();
    }

    public void clear(){

    }
}
