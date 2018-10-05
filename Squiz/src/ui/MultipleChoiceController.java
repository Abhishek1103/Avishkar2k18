package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextArea;
import data.Question;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MultipleChoiceController implements Initializable {

    @FXML
    CheckBox op1CB,op2CB, op3CB,op4CB;
    @FXML
    JFXButton submitButton;
    @FXML
    JFXTextArea quesTextArea;

    protected static ArrayList<Integer> responsesMultiple;

    JFXPopup popup;

    protected static Question question;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        quesTextArea.setText(question.getQuestion());
        op1CB.setText(question.getOptions().get(0));
        op2CB.setText(question.getOptions().get(1));
        op3CB.setText(question.getOptions().get(2));
        op4CB.setText(question.getOptions().get(3));


        popup = new JFXPopup();
        Label l = new Label("No options Selected");
        popup.setPopupContent(l);

        if(responsesMultiple == null){
            responsesMultiple = new ArrayList<>();
        }

        for(int i: responsesMultiple){
            switch(i){
                case 1:op1CB.setSelected(true);break;
                case 2:op2CB.setSelected(true);break;
                case 3:op3CB.setSelected(true);break;
                case 4:op4CB.setSelected(true);break;
            }
        }
    }

    public void submitButtonClicked(){
        // TODO: Record answer
        int flag = 0;
        responsesMultiple.clear();

        if(op1CB.isSelected()){
            responsesMultiple.add(1);
            flag = 1;
        }
        if(op2CB.isSelected()){
            responsesMultiple.add(2);
            flag = 1;
        }
        if(op3CB.isSelected()){
            responsesMultiple.add(3);
            flag = 1;
        }
        if(op4CB.isSelected()){
            responsesMultiple.add(4);
            flag = 1;
        }

        if(flag!=1){
            popup.show(submitButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }

        Question q = MainQuizFrameController.currentQuestionsList.get(MainQuizFrameController.currentQuestionIndex);
        ArrayList<Integer> responses = responsesMultiple;
        MainQuizFrameController.questionResponsesMap.put(question, responses);
    }
}
