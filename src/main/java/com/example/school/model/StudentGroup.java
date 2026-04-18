package com.example.school.model;

import java.time.LocalDateTime;

public class StudentGroup {
    private int id;
    private int studentId;
    private int courseGroupId;
    private LocalDateTime joinedAt;

    public StudentGroup() {
    }

    public StudentGroup(int id, int studentId, int courseGroupId, LocalDateTime joinedAt) {
        this.id = id;
        this.studentId = studentId;
        this.courseGroupId = courseGroupId;
        this.joinedAt = joinedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseGroupId() {
        return courseGroupId;
    }

    public void setCourseGroupId(int courseGroupId) {
        this.courseGroupId = courseGroupId;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
