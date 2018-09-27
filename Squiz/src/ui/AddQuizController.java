package ui;

import com.jfoenix.controls.*;
import constants.Constants;
import data.Question;
import data.Quiz;
import data.Section;
import javafx.collections.ObservableList;
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
import utility.NotifyServerAddSubjectService;

import java.net.URL;
import java.util.*;

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

        sectionMap = new ArrayList<>();

        // TODO: Initialize subjectNameComboBox
        Set<String> set = Constants.SUBJECT_MAP.keySet();

        for(String str: set){
            subjectNameComboBox.getItems().add(str);
        }


    }


    public void prepareQuizButtonClicked(){
        String quizName = quizNameTxt.getText().trim();
        String quizDesc = quizDescTxtArea.getText().trim();

        String subName = subjectNameComboBox.getSelectionModel().getSelectedItem();

        int marks = 0;
        // TODO: Calculate total marks of the quiz

        int totalTime = 0;
        // TODO: Calculate totalTime
        for(Pair<String, Section> p : sectionMap){
            totalTime += Integer.parseInt(p.getValue().getSectionTime());
            marks += Integer.parseInt(p.getValue().getSectionMarks());
        }

        Quiz newQuiz = new Quiz(quizName, subName, marks, totalTime, quizDesc);
        newQuiz.setSectionArrayListOfPair(sectionMap);

        Constants.SUBJECT_MAP.get(subName).getQuizHashMap().put(quizName, newQuiz);

        NotifyServerAddSubjectService service = new NotifyServerAddSubjectService();
        service.start();
        service.setOnSucceeded(e -> {
            System.out.println("Notify service succeded");
        });
        clear();

    }

    public void addSectionButtonClicked() throws Exception{
        Stage playWindow = new Stage();
        playWindow.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/addSectionLayout.fxml"));
        Scene sc = new Scene(root);
        playWindow.setScene(sc);
        playWindow.showAndWait();
    }

    public void clear(){
        quizDescTxtArea.clear();
        quizNameTxt.clear();
        subjectNameComboBox.getSelectionModel().clearSelection();
    }
}
