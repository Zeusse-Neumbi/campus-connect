package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.ExamResultDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.ExamResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamResultDaoSqliteImpl implements ExamResultDao {
    private static final Logger log = LoggerFactory.getLogger(ExamResultDaoSqliteImpl.class);

    @Override
    public void save(ExamResult result) {
        String sql = "INSERT INTO exam_results (exam_id, student_id, score, remark) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, result.getExamId());
            pstmt.setInt(2, result.getStudentId());
            pstmt.setInt(3, result.getScore());
            pstmt.setString(4, result.getRemark());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void update(ExamResult result) {
        String sql = "UPDATE exam_results SET score = ?, remark = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, result.getScore());
            pstmt.setString(2, result.getRemark());
            pstmt.setInt(3, result.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public List<ExamResult> findByExamId(int examId) {
        List<ExamResult> list = new ArrayList<>();
        String sql = "SELECT * FROM exam_results WHERE exam_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExamResult(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    @Override
    public List<ExamResult> findByStudentId(int studentId) {
        List<ExamResult> list = new ArrayList<>();
        String sql = "SELECT * FROM exam_results WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExamResult(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    @Override
    public Optional<ExamResult> findByExamIdAndStudentId(int examId, int studentId) {
        String sql = "SELECT * FROM exam_results WHERE exam_id = ? AND student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            pstmt.setInt(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExamResult(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return Optional.empty();
    }

    private ExamResult mapResultSetToExamResult(ResultSet rs) throws SQLException {
        return new ExamResult(
                rs.getInt("id"),
                rs.getInt("exam_id"),
                rs.getInt("student_id"),
                rs.getInt("score"),
                rs.getString("graded_at"),
                rs.getString("remark"));
    }
}
