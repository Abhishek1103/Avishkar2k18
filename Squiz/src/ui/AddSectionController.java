package ui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import data.Question;
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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AddSectionController implements Initializable {

    @FXML
    JFXListView<Label> quizListView;
    @FXML
    JFXTextField sectionNameText, sectionTimeText;

    @FXML
    JFXTextArea descriptionTxtArea;

    @FXML
    AnchorPane mainAnchorPane;

    protected static String sectionName;
    protected static HashMap<Integer, Question> questionMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionMap = new HashMap<>();
    }


    public void createSectionButtonClicked(){

        String sectionName = sectionNameText.getText().trim();
        String sectionTime = sectionTimeText.getText().trim();
        int time = 10;
        try{
            time = Integer.parseInt(sectionTime);
        }catch (Exception e){
            e.printStackTrace();
        }

        String description = descriptionTxtArea.getText().trim();

        Section newSection = new Section(AddQuizController.quizName, sectionName, description, time+"");
        newSection.setQuestionHashMap(questionMap);

        int marks=0;
        // TODO: Calculate total marks of section
        for(Map.Entry<Integer, Question> entry: questionMap.entrySet()){
            marks += entry.getValue().getMarks();
        }

        newSection.setSectionMarks(marks+"");
        AddQuizController.sectionMap.add(new Pair<>(sectionName, newSection));

        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        stage.close();


    }

    public void addQuestionButtonClicked() throws IOException {
        // TODO: Open question window
        Stage playWindow = new Stage();
        playWindow.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/addQuestionLayout.fxml"));
        Scene sc = new Scene(root);
        playWindow.setScene(sc);
        playWindow.showAndWait();
        //playWindow.show();
    }
}
