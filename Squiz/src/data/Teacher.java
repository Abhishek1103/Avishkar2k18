package data;

import java.io.Serializable;
import java.util.HashMap;

public class Teacher implements Serializable
{
    private String teacherName;
    private int teacherId;
    private HashMap<String, Subject> subjectHashMap;

    public Teacher(String teacherName, int teacherId) {
        this.teacherName = teacherName;
        this.teacherId = teacherId;
        this.subjectHashMap = new HashMap<String, Subject>();
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public HashMap<String, Subject> getSubjectHashMap() {
        return subjectHashMap;
    }

    public void setSubjectHashMap(HashMap<String, Subject> subjectHashMap) {
        this.subjectHashMap = subjectHashMap;
    }
}