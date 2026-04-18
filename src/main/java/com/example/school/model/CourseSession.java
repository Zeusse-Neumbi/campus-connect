package com.example.school.model;

public class CourseSession {
    private int id;
    private int courseId;
    private Integer courseGroupId;
    private int classroomId;
    private String sessionDate;
    private String startTime;
    private String endTime;
    private String createdAt;
    private String updatedAt;

    public CourseSession(int id, int courseId, Integer courseGroupId, int classroomId,
            String sessionDate, String startTime, String endTime) {
        this.id = id;
        this.courseId = courseId;
        this.courseGroupId = courseGroupId;
        this.classroomId = classroomId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Integer getCourseGroupId() {
        return courseGroupId;
    }

    public void setCourseGroupId(Integer courseGroupId) {
        this.courseGroupId = courseGroupId;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
