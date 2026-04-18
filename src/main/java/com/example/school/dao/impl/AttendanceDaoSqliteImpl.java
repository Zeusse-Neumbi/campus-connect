package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.AttendanceDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceDaoSqliteImpl implements AttendanceDao {
    private static final Logger log = LoggerFactory.getLogger(AttendanceDaoSqliteImpl.class);

    @Override
    public void save(Attendance attendance) {
        String sql = "INSERT INTO attendance (course_session_id, student_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getCourseSessionId());
            pstmt.setInt(2, attendance.getStudentId());
            pstmt.setString(3, attendance.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public List<Attendance> findByStudentId(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    @Override
    public Optional<Attendance> findBySessionIdAndStudentId(int courseSessionId, int studentId) {
        String sql = "SELECT * FROM attendance WHERE course_session_id = ? AND student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseSessionId);
            pstmt.setInt(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Attendance attendance) {
        String sql = "UPDATE attendance SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, attendance.getStatus());
            pstmt.setInt(2, attendance.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public double getAttendanceRate(int studentId) {
        // Only count sessions that have already happened (session_date <= today)
        String totalSql = "SELECT COUNT(*) FROM attendance a " +
                "JOIN course_sessions cs ON a.course_session_id = cs.id " +
                "WHERE a.student_id = ? AND cs.session_date <= date('now')";
        String presentSql = "SELECT COUNT(*) FROM attendance a " +
                "JOIN course_sessions cs ON a.course_session_id = cs.id " +
                "WHERE a.student_id = ? AND UPPER(a.status) = 'PRESENT' AND cs.session_date <= date('now')";

        int totalClasses = 0;
        int presentClasses = 0;

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(totalSql)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalClasses = rs.getInt(1);
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(presentSql)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        presentClasses = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }

        return totalClasses > 0 ? ((double) presentClasses / totalClasses) * 100.0 : 100.0;
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        return new Attendance(
                rs.getInt("id"),
                rs.getInt("course_session_id"),
                rs.getInt("student_id"),
                rs.getString("status"),
                rs.getString("marked_at"));
    }
}
