package com.example.school.model;

public class ExamResult {
    private int id;
    private int examId;
    private int studentId;
    private int score;
    private String gradedAt;
    private String remark;

    public ExamResult(int id, int examId, int studentId, int score, String gradedAt, String remark) {
        this.id = id;
        this.examId = examId;
        this.studentId = studentId;
        this.score = score;
        this.gradedAt = gradedAt;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(String gradedAt) {
        this.gradedAt = gradedAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
