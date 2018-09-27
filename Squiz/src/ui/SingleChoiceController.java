package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import data.Question;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SingleChoiceController implements Initializable {

    @FXML
    JFXRadioButton op1RB, op2RB,op3RB,op4RB;
    @FXML
    JFXButton submitButton;
    @FXML
    JFXTextArea quesTextArea;

    ToggleGroup toggleGroup;

    protected static ArrayList<Integer> responsesSingle;
    protected static Question question;

    JFXPopup popup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleGroup = new ToggleGroup();
        op1RB.setToggleGroup(toggleGroup);
        op2RB.setToggleGroup(toggleGroup);
        op3RB.setToggleGroup(toggleGroup);
        op4RB.setToggleGroup(toggleGroup);

        quesTextArea.setText(question.getQuestion());
        op1RB.setText(question.getOptions().get(0));
        op2RB.setText(question.getOptions().get(1));
        op3RB.setText(question.getOptions().get(2));
        op4RB.setText(question.getOptions().get(3));


        if(responsesSingle == null || responsesSingle.size() == 0){
            responsesSingle = new ArrayList<>();
        }else {

            int i = responsesSingle.get(0);

            switch (i) {
                case 1:
                    op1RB.setSelected(true);
                    break;
                case 2:
                    op2RB.setSelected(true);
                    break;
                case 3:
                    op3RB.setSelected(true);
                    break;
                case 4:
                    op4RB.setSelected(true);
                    break;
            }
        }

        popup = new JFXPopup();
        Label l = new Label("No options Selected");
        popup.setPopupContent(l);
    }


    public void submitButtonClicked(){
        // TODO: Record answer

        responsesSingle.clear(); // Single Choice can have only one option response

        if(op1RB.isSelected()){
            responsesSingle.add(1);
        }else if(op2RB.isSelected()){
            responsesSingle.add(2);
        }else if(op3RB.isSelected()){
            responsesSingle.add(3);
        }else if(op4RB.isSelected()){
            responsesSingle.add(4);
        }else{
            popup.show(submitButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }
        Question q = MainQuizFrameController.currentQuestionsList.get(MainQuizFrameController.currentQuestionIndex);
        ArrayList<Integer> responses = responsesSingle;
        MainQuizFrameController.questionResponsesMap.put(question, responses);
    }
}
