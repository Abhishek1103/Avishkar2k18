package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ShowResultsLocallyController implements Initializable {

    public static int mrks;
    public static String quizName;

    @FXML
    Label quizNameLabel, marksLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quizNameLabel.setText("Quiz Name: "+quizName);
        marksLabel.setText("Marks: "+mrks);
    }
}
