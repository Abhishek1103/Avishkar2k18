package ui;

import com.jfoenix.controls.JFXButton;
import data.Subject;
import data.Teacher;
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

public class ShowTeachersSubjectsController implements Initializable {

    protected static String teacherName;

    @FXML
    ScrollPane scrollPane;

    VBox vBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        vBox = new VBox(10);
        vBox.setPadding(new Insets(15));

        try{

            init();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void init() throws Exception{
        HashMap<String, Teacher> map = new HashMap<>();
        map = MainStudentPageController.stringTeacherMap;

        Teacher teacher = map.get(teacherName);
        HashMap<String, Subject> subMap = teacher.getSubjectHashMap();

        for(Map.Entry<String, Subject> entry: subMap.entrySet() ){
            String subName = entry.getKey();
            Subject sub = entry.getValue();

            AnchorPane pane = createItem(subName, sub.getTeacherNameUnderWhichThisSubjectIs(), sub);

            vBox.getChildren().add(pane);
        }

        scrollPane.setContent(vBox);
    }


    private AnchorPane createItem(String subjectName, String teacherName, Subject subject){
        AnchorPane pane = null;
        try{

            pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/subjectItemStudentLayout.fxml"));

            Label teacherLabel, subjectLabel;
            teacherLabel = (Label)(pane.getChildren().get(1));
            subjectLabel = (Label)(pane.getChildren().get(0));
            JFXButton showQuizButton = (JFXButton) (pane.getChildren().get(2));

            teacherLabel.setText(teacherName);
            subjectLabel.setText(subjectName);

            showQuizButton.setOnAction(e -> {
                // TODO: show quizzes

                ShowQuizStudentController.quizMap = subject.getQuizHashMap();
                System.out.println(ShowQuizStudentController.quizMap);
                try{

                    AnchorPane pane1= FXMLLoader.load(getClass().getClassLoader().getResource("layouts/showQuizStudentLayout.fxml"));
                    scrollPane.setContent(pane1);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            });

            return pane;
        }catch (Exception e){
            e.printStackTrace();
        }
        return pane;
    }
}
