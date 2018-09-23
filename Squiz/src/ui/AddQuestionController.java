package ui;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import constants.Constants;
import data.Question;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddQuestionController implements Initializable {

    @FXML
    JFXRadioButton op1Button,op2Button,op3Button,op4Button, singleButton, multipleButton, trueFalseButton;

    @FXML
    CheckBox op1CheckBox,op2CheckBox,op3CheckBox,op4CheckBox;

    @FXML
    AnchorPane singlePane, multiplePane, mainAnchorPane;

    @FXML
    JFXTextArea questionTxt;

    @FXML
    JFXTextField marksTxt;

    ToggleGroup typeGroup, optionMCQ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        typeGroup = new ToggleGroup();
        singleButton.setToggleGroup(typeGroup);
        multipleButton.setToggleGroup(typeGroup);
        trueFalseButton.setToggleGroup(typeGroup);

        singleButton.setSelected(true);

        optionMCQ = new ToggleGroup();
        op1Button.setToggleGroup(optionMCQ);
        op2Button.setToggleGroup(optionMCQ);
        op3Button.setToggleGroup(optionMCQ);
        op4Button.setToggleGroup(optionMCQ);

        singlePane.setVisible(true);
        multiplePane.setVisible(false);

        singleButton.setOnAction(e->{
            singlePane.setVisible(true);
            multiplePane.setVisible(false);
        });

        multipleButton.setOnAction(e -> {
            singlePane.setVisible(false);
            multiplePane.setVisible(true);
        });

    }


    public void addQuestionButtonClicked(){
        String question = questionTxt.getText().trim();
        String type = "" ;

        if(singleButton.isSelected()){
            type = Constants.SINGLE;
        }else if(multipleButton.isSelected()){
            type = Constants.MULTIPLE;
        }else if(trueFalseButton.isSelected()){
            type = Constants.TF;
        }

        ArrayList<Integer> answer = new ArrayList<>();
        answer = getAnswers();

        int marks = Integer.parseInt(marksTxt.getText());

        Question questionObj = new Question(question, type, AddSectionController.sectionName, marks);
        if(answer.size() > 0){
            questionObj.setAnswer(answer);
        }

        int queNum = AddSectionController.questionMap.size() + 1;
        AddSectionController.questionMap.put(queNum, questionObj);

        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        stage.close();

    }


    private ArrayList<Integer> getAnswers(){
        ArrayList<Integer> ans = new ArrayList<>();
        // TODO: add logic
        return ans;
    }
}
