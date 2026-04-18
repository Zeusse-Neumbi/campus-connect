package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.StudentGroupDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.StudentGroup;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentGroupDaoSqliteImpl implements StudentGroupDao {
    private static final Logger log = LoggerFactory.getLogger(StudentGroupDaoSqliteImpl.class);

    @Override
    public int save(StudentGroup studentGroup) {
        String sql = "INSERT OR IGNORE INTO student_groups (student_id, course_group_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, studentGroup.getStudentId());
            pstmt.setInt(2, studentGroup.getCourseGroupId());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return -1;
    }

    @Override
    public void delete(int studentId, int courseGroupId) {
        String sql = "DELETE FROM student_groups WHERE student_id = ? AND course_group_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseGroupId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public List<StudentGroup> findByCourseGroupId(int courseGroupId) {
        List<StudentGroup> list = new ArrayList<>();
        String sql = "SELECT * FROM student_groups WHERE course_group_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseGroupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    @Override
    public List<StudentGroup> findByStudentId(int studentId) {
        List<StudentGroup> list = new ArrayList<>();
        String sql = "SELECT * FROM student_groups WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    private StudentGroup mapResultSet(ResultSet rs) throws SQLException {
        StudentGroup sg = new StudentGroup();
        sg.setId(rs.getInt("id"));
        sg.setStudentId(rs.getInt("student_id"));
        sg.setCourseGroupId(rs.getInt("course_group_id"));
        String joinedAtStr = rs.getString("joined_at");
        if (joinedAtStr != null) {
            try {
                sg.setJoinedAt(LocalDateTime.parse(joinedAtStr.replace(" ", "T")));
            } catch (Exception e) {
                // ignore parse errors
            }
        }
        return sg;
    }
}
