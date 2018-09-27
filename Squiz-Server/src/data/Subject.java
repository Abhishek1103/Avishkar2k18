package data;

import java.io.Serializable;
import java.util.HashMap;

public class Subject implements Serializable
{
    private String subjectName;
    private String teacherNameUnderWhichThisSubjectIs;
    private HashMap<String, Quiz> quizHashMap;

    public Subject(String subjectName, String teacherNameUnderWhichThisSubjectIs) {
        this.subjectName = subjectName;
        this.teacherNameUnderWhichThisSubjectIs = teacherNameUnderWhichThisSubjectIs;
        this.quizHashMap = new HashMap<String, Quiz>();
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherNameUnderWhichThisSubjectIs() {
        return teacherNameUnderWhichThisSubjectIs;
    }

    public void setTeacherNameUnderWhichThisSubjectIs(String teacherNameUnderWhichThisSubjectIs) {
        this.teacherNameUnderWhichThisSubjectIs = teacherNameUnderWhichThisSubjectIs;
    }

    public HashMap<String, Quiz> getQuizHashMap() {
        return quizHashMap;
    }

    public void setQuizHashMap(String nameOfQuiz, Quiz quiz) {
        this.quizHashMap.put(nameOfQuiz, quiz);
    }
}
