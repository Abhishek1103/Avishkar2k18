package data;

import java.util.ArrayList;

public class Question
{
    private String question;
    private String type;
    private String section;
    private ArrayList<String> options;
    private ArrayList<Integer> answer;
    private int marks;

    public Question(String question, String type, String section, int marks) {
        this.question = question;
        this.type = type;
        this.section = section;
        this.options = new ArrayList<String>();
        this.answer = new ArrayList<Integer>();
        this.marks = marks;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public ArrayList<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList<Integer> answer) {
        this.answer = answer;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

}