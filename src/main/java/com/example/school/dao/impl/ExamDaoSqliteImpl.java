package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.ExamDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Exam;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamDaoSqliteImpl implements ExamDao {
    private static final Logger log = LoggerFactory.getLogger(ExamDaoSqliteImpl.class);

    @Override
    public void save(Exam exam) {
        String sql = "INSERT INTO exams (course_id, exam_name, exam_type, exam_date, max_score) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, exam.getCourseId());
            pstmt.setString(2, exam.getExamName());
            pstmt.setString(3, exam.getExamType());
            pstmt.setString(4, exam.getExamDate());
            pstmt.setDouble(5, exam.getMaxScore());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    exam.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void update(Exam exam) {
        String sql = "UPDATE exams SET course_id = ?, exam_name = ?, exam_type = ?, exam_date = ?, max_score = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, exam.getCourseId());
            pstmt.setString(2, exam.getExamName());
            pstmt.setString(3, exam.getExamType());
            pstmt.setString(4, exam.getExamDate());
            pstmt.setDouble(5, exam.getMaxScore());
            pstmt.setInt(6, exam.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM exams WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public Optional<Exam> findById(int id) {
        String sql = "SELECT * FROM exams WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExam(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Exam> findByCourseId(int courseId) {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT * FROM exams WHERE course_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExam(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    private Exam mapResultSetToExam(ResultSet rs) throws SQLException {
        Exam e = new Exam(
                rs.getInt("id"),
                rs.getInt("course_id"),
                rs.getString("exam_name"),
                rs.getString("exam_type"),
                rs.getString("exam_date"),
                rs.getDouble("max_score"));
        e.setCreatedAt(rs.getString("created_at"));
        return e;
    }
}
