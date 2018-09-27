package ui;

import com.jfoenix.controls.JFXButton;
import data.Quiz;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class ShowQuizStudentController implements Initializable {

    protected static HashMap<String, Quiz> quizMap = null;
    @FXML
    ScrollPane scrollPane;

    VBox vBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        quizMap = new HashMap<>();
        vBox = new VBox(10);
        vBox.setPadding(new Insets(15));
        try{
            init();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

     private void init() throws Exception{

        if(quizMap!=null) {
            HashMap<String, Quiz> map = quizMap;

            System.out.println("init ShowQuizzes");

            for (Map.Entry<String, Quiz> q : map.entrySet()) {
                Quiz quiz = q.getValue();
                System.out.println("quizName" + quiz.getQuizName());
                System.out.println(quiz.getTotalMarks() + "");
                System.out.println(quiz.getTotalDuration() + "");
                AnchorPane pane = createItem(quiz);
                vBox.getChildren().add(pane);

            }

            scrollPane.setPadding(new Insets(15));
            scrollPane.setContent(vBox);
        }
     }


     private AnchorPane createItem(Quiz quiz) throws Exception{

        AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/quizItemLayout.fxml"));
        Label quizNameLabel, sectionsLabel, markLabel;
        quizNameLabel = (Label)(pane.getChildren().get(0));
        sectionsLabel = (Label)(pane.getChildren().get(1));
        markLabel = (Label)(pane.getChildren().get(2));
        JFXButton attempButton = (JFXButton)(pane.getChildren().get(3));

        quizNameLabel.setText(quiz.getQuizName());
        sectionsLabel.setText("Sections: "+quiz.getSectionArrayListOfPair().size()+"");
        markLabel.setText("Max Marks: "+quiz.getTotalMarks()+"");

        attempButton.setOnAction(e-> {
            // TODO: Start quiz
            MainQuizFrameController.quiz = quiz;
            MainQuizFrameController.sectionMap = quiz.getSectionArrayListOfPair();

            try {
                Stage playWindow = new Stage();
                playWindow.initModality(Modality.APPLICATION_MODAL);
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/mainQuizFrame.fxml"));
                Scene sc = new Scene(root);
                playWindow.setScene(sc);
                playWindow.showAndWait();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

        return pane;
     }
}
