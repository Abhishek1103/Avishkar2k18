package data;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz implements Serializable
{
    private String quizName;
    private String subjectName;
    private int totalMarks;
    private int totalDuration;
    private String quizInfo;
    private ArrayList<Pair<String, Section> > sectionArrayListOfPair;

    public Quiz(String quizName, String subjectName, int totalMarks, int totalDuration, String quizInfo) {
        this.quizName = quizName;
        this.subjectName = subjectName;
        this.totalMarks = totalMarks;
        this.totalDuration = totalDuration;
        this.quizInfo = quizInfo;
        this.sectionArrayListOfPair = new ArrayList<Pair<String, Section> >();
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getQuizInfo() {
        return quizInfo;
    }

    public void setQuizInfo(String quizInfo) {
        this.quizInfo = quizInfo;
    }

    public ArrayList<Pair<String, Section>> getSectionArrayListOfPair() {
        return sectionArrayListOfPair;
    }

    public void setSectionArrayListOfPair(ArrayList<Pair<String, Section>> p) {
        this.sectionArrayListOfPair = p;
    }
}