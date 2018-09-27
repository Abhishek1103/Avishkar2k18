package utility;


import data.Question;
import data.Quiz;
import data.Section;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenerateResultsLocally extends Service {

    private Quiz quiz;
    private HashMap<Question, ArrayList<Integer>> quesResponsesMap;
    private int marks;

    public GenerateResultsLocally(Quiz quiz, HashMap<Question, ArrayList<Integer>> quesRespList){
        this.quiz = quiz;
        this.quesResponsesMap = quesRespList;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try{

                    HashMap<Question, ArrayList<Integer>> questionAnsMap = new HashMap<>();

                    ArrayList<Pair<String, Section>> arrOfPair = quiz.getSectionArrayListOfPair();
                    for(Pair<String, Section> p: arrOfPair){
                        Section sec = p.getValue();
                        HashMap<Integer, Question> ques = sec.getQuestionHashMap();
                        for(Map.Entry<Integer, Question> entry: ques.entrySet()){
                            questionAnsMap.put(entry.getValue(), entry.getValue().getAnswer());
                        }
                    }
                    marks = 0;
                    for(Map.Entry<Question, ArrayList<Integer>> entry: quesResponsesMap.entrySet()){
                        ArrayList<Integer> ans = questionAnsMap.get(entry.getKey());
                        ArrayList<Integer> responses = entry.getValue();
                        boolean flag = true;
                        if(ans.size()!=responses.size()){
                            continue;
                        }else{
                            for(int i: ans){
                                if(!responses.contains(i)){
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag)
                                marks += entry.getKey().getMarks();
                        }

                    }



                }catch (Exception ex){
                    ex.printStackTrace();
                }

                return null;
            }
        };
    }

    public int getMarks(){
        return this.marks;
    }
}
