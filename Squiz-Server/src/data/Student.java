package data;

import java.io.Serializable;

public class Student implements Serializable
{
    private String studentName;

    public Student(String studentName)
    {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
