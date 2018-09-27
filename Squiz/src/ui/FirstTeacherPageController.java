package ui;

import com.jfoenix.controls.JFXButton;
import data.Subject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static constants.Constants.SUBJECT_MAP;

public class FirstTeacherPageController implements Initializable {

    @FXML
    ScrollPane scrollPane;

    private VBox vBox;
    private HashMap<String, data.Subject> subjectMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        vBox = new VBox(10);
        vBox.setPadding(new Insets(15));

        subjectMap = SUBJECT_MAP;

        try {
            init();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void init() throws Exception{
        if(subjectMap!=null && !subjectMap.isEmpty()) {
            for (Map.Entry<String, Subject> entry : subjectMap.entrySet()) {
                Subject subject = entry.getValue();
                String subjectName = entry.getKey();
                int numQuizzes = subject.getQuizHashMap().size();

                AnchorPane pane = createSingleItem(subjectName, numQuizzes, subject);
                vBox.getChildren().add(pane);
            }

            scrollPane.setContent(vBox);
        }
    }

    private AnchorPane createSingleItem(String subjectName, int numberOfQuizzes, Subject subject) throws Exception{

        AnchorPane pane = FXMLLoader.load(getClass().getResource("layouts/subjectItemLayout.fxml"));
        Label subName = (Label)(pane.getChildren().get(0));
        Label numQuizzes = (Label)(pane.getChildren().get(1));
        JFXButton detailsButton = (JFXButton)(pane.getChildren().get(3));

        subName.setText(subjectName);
        numQuizzes.setText("Quizzes: "+numberOfQuizzes);

        detailsButton.setOnAction(e -> {
            // TODO: Logic
        });

        return pane;
    }

}
