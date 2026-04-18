package com.example.school.dao;

import com.example.school.model.Attendance;
import java.util.List;

public interface AttendanceDao {
    void save(Attendance attendance);

    List<Attendance> findByStudentId(int studentId);

    java.util.Optional<Attendance> findBySessionIdAndStudentId(int courseSessionId, int studentId);

    void update(Attendance attendance);

    double getAttendanceRate(int studentId);
}
