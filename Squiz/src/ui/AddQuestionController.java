package ui;

import com.jfoenix.controls.*;
import constants.Constants;
import data.Question;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    JFXTextField marksTxt, op1Txt, op2Txt,op3Txt,op4Txt, opm1Txt,opm2Txt,opm3Txt,opm4Txt;

    @FXML
    JFXButton addQuestionButton;

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
        try {
            String question = questionTxt.getText().trim();
            String type = "";


            if (singleButton.isSelected()) {
                type = Constants.SINGLE;
            } else if (multipleButton.isSelected()) {
                type = Constants.MULTIPLE;
            } else if (trueFalseButton.isSelected()) {
                type = Constants.TF;
            }

            ArrayList<Integer> answer = getAnswers();
            ArrayList<String> options = getOptions();

            int marks = Integer.parseInt(marksTxt.getText());

            Question questionObj = new Question(question, type, AddSectionController.sectionName, marks);
            questionObj.setOptions(options);
            if (answer.size() > 0) {
                questionObj.setAnswer(answer);
            }
            try {
                String questionId = getSHA256(question);
                questionObj.setQuestionId(questionId);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            int queNum = AddSectionController.questionMap.size() + 1;
            AddSectionController.questionMap.put(queNum, questionObj);

            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
            stage.close();
        }catch (Exception ex){
            ex.printStackTrace();
            JFXPopup pop = new JFXPopup();
            Label l  = new Label("Some Error occured while adding question\nFailed to add question..!!");
            pop.setPopupContent(l);
            pop.show(addQuestionButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
        }

    }


    private ArrayList<Integer> getAnswers(){
        ArrayList<Integer> ans = new ArrayList<>();
        if(singleButton.isSelected()){
            if(op1Button.isSelected()){
                ans.add(1);
            }else if(op2Button.isSelected()){
                ans.add(2);
            }else if(op3Button.isSelected()){
                ans.add(3);
            }else if(op4Button.isSelected()){
                ans.add(4);
            }
            return ans;
        }else if(multipleButton.isSelected()){
            if(op1CheckBox.isSelected()){
                ans.add(1);
            }
            if(op2CheckBox.isSelected()){
                ans.add(2);
            }
            if(op3CheckBox.isSelected()){
                ans.add(3);
            }
            if(op4CheckBox.isSelected()){
                ans.add(4);
            }
            return ans;
        }
        return ans;
    }

    private ArrayList<String> getOptions(){
        ArrayList<String> list = new ArrayList<>();
        if(singleButton.isSelected()){
            list.add(op1Txt.getText());
            list.add(op2Txt.getText());
            list.add(op3Txt.getText());
            list.add(op4Txt.getText());
        }else if(multipleButton.isSelected()){
            list.add(opm1Txt.getText());
            list.add(opm2Txt.getText());
            list.add(opm3Txt.getText());
            list.add(opm4Txt.getText());
        }
        return list;
    }

    private String getSHA256(String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(text.getBytes());

        byte[] byteData = messageDigest.digest();

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            stringBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }
}
