package data;

import java.io.Serializable;
import java.util.HashMap;

public class Section implements Serializable
{
    private String sectionName;
    private String sectionDescription;
    private String sectionTime;
    private String sectionMarks;
    private String quizName;
    private HashMap<Integer, Question> questionHashMap;

    public Section(String quizName, String sectionName, String sectionDescription, String sectionTime) {
        this.quizName = quizName;
        this.sectionName = sectionName;
        this.sectionDescription = sectionDescription;
        this.sectionTime = sectionTime;
        //this.sectionMarks = sectionMarks;
        this.questionHashMap = new HashMap<Integer, Question>();
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionDescription() {
        return sectionDescription;
    }

    public void setSectionDescription(String sectionDescription) {
        this.sectionDescription = sectionDescription;
    }

    public String getSectionTime() {
        return sectionTime;
    }

    public void setSectionTime(String sectionTime) {
        this.sectionTime = sectionTime;
    }

    public String getSectionMarks() {
        return sectionMarks;
    }

    public void setSectionMarks(String sectionMarks) {
        this.sectionMarks = sectionMarks;
    }

    public HashMap<Integer, Question> getQuestionHashMap() {
        return questionHashMap;
    }

    public void setQuestionHashMap(HashMap<Integer, Question> map) {
        this.questionHashMap  = map;
    }
}
