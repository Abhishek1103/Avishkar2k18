package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import constants.Constants;
import data.Subject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import utility.NotifyServerAddSubjectService;


import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddSubjectController implements Initializable {

    @FXML
    JFXTextField subjectNameTxtFeild, subjectIdTxtFeild;
    @FXML
    JFXTextArea additionalDetailsTxtArea;

    @FXML
    JFXButton addSubjectButton, cancelButton;




    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Add subject loaded");

    }


    public void addSubjectButtonClicked(){
        String subJectName = subjectNameTxtFeild.getText().trim();
        String subjectId = subjectIdTxtFeild.getText().trim();

        System.out.println("Subject name: "+subJectName);

        Subject subject = new Subject(subJectName, Constants.USERNAME);
        Constants.SUBJECT_MAP.put(subJectName, subject);

        System.out.println("subjectMap: "+Constants.SUBJECT_MAP);

        // TODO: Notify Server
        NotifyServerAddSubjectService notify = new NotifyServerAddSubjectService();
        notify.start();
        notify.setOnSucceeded(e -> {
            System.out.println("Notify subject completed");
        });

        clear();
    }

    private void clear(){
        subjectNameTxtFeild.clear();
        subjectIdTxtFeild.clear();
        additionalDetailsTxtArea.clear();
    }

}
