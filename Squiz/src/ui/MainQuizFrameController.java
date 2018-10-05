package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import constants.Constants;
import data.Question;
import data.Quiz;
import data.Section;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import utility.GenerateResultsLocally;
import utility.SubmitQuizService;
import utility.TimerClock;
import utility.TimerClockService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainQuizFrameController implements Initializable {

    @FXML AnchorPane mainAnchorPane, sectionAnchorPane, questionAnchorPane;
    @FXML VBox sectionVbox;
    @FXML Label timerLabel;
    @FXML JFXButton submitButton, previousQuestionButton, nextQuestionButton;

    protected static int rating;


    protected static ArrayList<Pair<String, Section>> sectionMap;
    protected static HashMap<String, Section> sectionHashMap;

    protected static Quiz quiz;

    protected static String currentQuestion;
    public static String currentSection;
    protected static HashMap<Integer, Question> currentQuestonsMap;
    protected static ArrayList<Question> currentQuestionsList;
    protected static int currentQuestionIndex;

    protected static HashMap<Question, ArrayList<Integer>> questionResponsesMap;

    public static boolean isResultGenerated = false;

    //TimerClockService timerService;

    HashMap<Section, TimerClockService> sectionTimerMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            sectionTimerMap = new HashMap<>();
            questionResponsesMap = new HashMap<>();

            sectionHashMap = createSectionHashMap(sectionMap);

            sectionVbox.setSpacing(10);
            sectionVbox.setPadding(new Insets(10));
            currentSection = sectionMap.get(0).getKey();
            currentQuestonsMap = sectionMap.get(0).getValue().getQuestionHashMap();

            currentQuestionsList = new ArrayList<>();
            for(Map.Entry<Integer, Question> entry:currentQuestonsMap.entrySet()){
                currentQuestionsList.add(entry.getValue());
            }
            currentQuestionIndex = 0;
            System.out.println("Current QuestionsMap: "+currentQuestonsMap );
            System.out.println("Current QuestionsList: "+currentQuestionsList);
            currentSection = sectionMap.get(0).getKey();

            initSections();

            // TODO: setInitial section and question

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    HashMap<String, Section> createSectionHashMap(ArrayList<Pair<String, Section>> sectionMap){
        HashMap<String, Section> sectionHashMap = new HashMap<>();

        for(Pair<String, Section> p: sectionMap){
            sectionHashMap.put(p.getKey(), p.getValue());
        }
        System.out.println("Generated sectionHashMap: "+sectionHashMap);
        return sectionHashMap;
    }

    private void initSections(){
        sectionMap = quiz.getSectionArrayListOfPair();
        for(Pair<String, Section> p: sectionMap){
            JFXButton button = createButton(p.getValue());
            sectionVbox.getChildren().add(button);
        }

    }

    private JFXButton createButton(Section sectionObject){
        JFXButton button = new JFXButton(sectionObject.getSectionName());
        button.setOnAction(e -> {
            //TODO: logic
            if(Integer.parseInt(sectionObject.getSectionTime())>0) {
                Section section = sectionHashMap.get(currentSection);
                if (section!=null){
                    try {
                        TimerClockService ts = sectionTimerMap.get(section);
                        if(ts!=null) {
                            System.out.println("Section: "+sectionObject.getSectionName()+"-> time: "+sectionObject.getSectionTime());
                            section.setSectionTime(ts.getInterval() + "");
                            sectionTimerMap.get(section).cancel();
                            ts.cancel();
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                currentSection = sectionObject.getSectionName();
                currentQuestonsMap = sectionObject.getQuestionHashMap();
                TimerClockService timerClockService = new TimerClockService(timerLabel, Integer.parseInt(sectionObject.getSectionTime()), sectionObject.getSectionName());
                currentQuestionsList = new ArrayList<>();
                for(Map.Entry<Integer, Question> entry:currentQuestonsMap.entrySet()){
                    currentQuestionsList.add(entry.getValue());
                }
                currentQuestionIndex = 0;
                Question q = currentQuestionsList.get(0);
                ArrayList<Integer> lst  = new ArrayList<>();
                if(questionResponsesMap.containsKey(q)) {
                    lst = questionResponsesMap.get(q);
                }
                timerClockService.start();
                sectionTimerMap.put(sectionObject,timerClockService);

                if(q.getType().equals(Constants.SINGLE)){
                    SingleChoiceController.question = q;
                    SingleChoiceController.responsesSingle = lst;
                    if(questionResponsesMap.containsKey(q)){
                        SingleChoiceController.responsesSingle = questionResponsesMap.get(q);
                    }
                    try{
                        AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/singleChoiceLayout.fxml"));
                        questionAnchorPane.getChildren().setAll(pane);
                    }catch (Exception exp){
                        exp.printStackTrace();
                    }

                }else if(q.getType().equals(Constants.MULTIPLE)){
                    MultipleChoiceController.question = q;
                    if(questionResponsesMap.containsKey(q)){
                        MultipleChoiceController.responsesMultiple = questionResponsesMap.get(q);
                    }
                    try {
                        AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/multipleChoiceLayout.fxml"));
                        questionAnchorPane.getChildren().setAll(pane);
                    }catch (Exception exp){
                        exp.printStackTrace();
                    }
                }
            }else{
                // TODO: Show popup the section time has expired
                button.setDisable(true);
                JFXPopup pop = new JFXPopup();
                Label popLabel = new Label("This section has Timed Out");
                pop.setPopupContent(popLabel);
                pop.show(button, JFXPopup.PopupVPosition.BOTTOM, JFXPopup.PopupHPosition.RIGHT);
                // TODO: Switch to next section
                // TODO: if all sections are timedout submit the quiz
            }
        });
        return button;
    }

    public void nextQuestionButtonPressed() {

        if(currentQuestionIndex < currentQuestionsList.size()-1){
            // TODO: show next question
            Question q = currentQuestionsList.get(currentQuestionIndex+1);
            currentQuestionIndex++;
            System.out.println("QuestionResponsesMap: "+questionResponsesMap);
            ArrayList<Integer> lst  = new ArrayList<>();
            if(questionResponsesMap.containsKey(q)) {
                 lst = questionResponsesMap.get(q);
                System.out.println("Responses List for question: "+q.getQuestion()+": "+lst);
            }
            // Todo: set Current question on the display
            if(q.getType().equals(Constants.SINGLE)){
                SingleChoiceController.question = q;
                SingleChoiceController.responsesSingle = lst;
                try{

                    AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/singleChoiceLayout.fxml"));
                    questionAnchorPane.getChildren().setAll(pane);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(q.getType().equals(Constants.MULTIPLE)){
                MultipleChoiceController.question = q;
                MultipleChoiceController.responsesMultiple = lst;
                try {
                    AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/multipleChoiceLayout.fxml"));
                    questionAnchorPane.getChildren().setAll(pane);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            // TODO: this section's questions have ended
        }
    }

    public void previousQuestionButtonPressed() {
        if(currentQuestionIndex > 0){
            Question q = currentQuestionsList.get(currentQuestionIndex-1);
            currentQuestionIndex--;
            ArrayList<Integer> lst  = new ArrayList<>();
            System.out.println("QuestionResponsesMap: "+questionResponsesMap);
            if(questionResponsesMap.containsKey(q)) {
                lst = questionResponsesMap.get(q);

                System.out.println("Responses List for question: "+q.getQuestion()+": "+lst);
            }
            if(q.getType().equals(Constants.SINGLE)){
                SingleChoiceController.question = q;
                SingleChoiceController.responsesSingle = lst;
                try{

                    AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/singleChoiceLayout.fxml"));
                    questionAnchorPane.getChildren().setAll(pane);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(q.getType().equals(Constants.MULTIPLE)){
                MultipleChoiceController.question = q;
                SingleChoiceController.responsesSingle = lst;
                try {
                    AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/multipleChoiceLayout.fxml"));
                    questionAnchorPane.getChildren().setAll(pane);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            // Todo: first question of the section
        }
    }


    public void submitButtonClicked(){
        System.out.println("Submit button clicked");
        try{
            Stage playWindow = new Stage();
            playWindow.initModality(Modality.APPLICATION_MODAL);
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/rateQuizLayout.fxml"));
            Scene sc = new Scene(root);
            playWindow.setScene(sc);
            playWindow.showAndWait();
            System.out.println("Rating Received: "+ rating);

            GenerateResultsLocally grl = new GenerateResultsLocally(quiz, questionResponsesMap);
            grl.start();

            grl.setOnSucceeded(e -> {
                System.out.println("Get Results locally service ended");
                int marks = grl.getMarks();
                ShowResultsLocallyController.mrks = marks;
                ShowResultsLocallyController.quizName = quiz.getQuizName();

                try {
                    Socket sock = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    dout.writeBoolean(false);
                    dout.writeUTF("#SAVESCORE");
                    System.out.println("sent "+"#SAVESCORE");
                    dout.writeUTF(Constants.USERNAME);
                    dout.writeUTF(ShowTeachersSubjectsController.teacherName);
                    dout.writeUTF(quiz.getQuizName());
                    dout.writeInt(marks);
                    System.out.println("written everything in SAVESCORE");

                    ois.close();
                    oos.close();
                    sock.close();

                }catch (Exception exp){
                    exp.printStackTrace();
                }

                try{
                    Parent root1 = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/showResultLocally.fxml"));
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    Scene s = new Scene(root1);
                    stage.setScene(s);
                    stage.showAndWait();

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });

            SubmitQuizService service = new SubmitQuizService(quiz.getQuizName(), ShowTeachersSubjectsController.teacherName, Constants.USERNAME, rating, questionResponsesMap);
            service.start();
            service.setOnSucceeded(e->{
                System.out.println("Submit quiz Service Ended");
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}