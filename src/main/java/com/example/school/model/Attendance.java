package com.example.school.model;

public class Attendance {
    private int id;
    private int courseSessionId;
    private int studentId;
    private String status;
    private String markedAt;

    public Attendance(int id, int courseSessionId, int studentId, String status, String markedAt) {
        this.id = id;
        this.courseSessionId = courseSessionId;
        this.studentId = studentId;
        this.status = status;
        this.markedAt = markedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseSessionId() {
        return courseSessionId;
    }

    public void setCourseSessionId(int courseSessionId) {
        this.courseSessionId = courseSessionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(String markedAt) {
        this.markedAt = markedAt;
    }
}
