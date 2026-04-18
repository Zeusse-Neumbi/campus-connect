package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.StudentParentDao;
import com.example.school.dao.db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentParentDaoSqliteImpl implements StudentParentDao {
    private static final Logger log = LoggerFactory.getLogger(StudentParentDaoSqliteImpl.class);

    @Override
    public void link(int studentId, int parentId, String relationship) {
        String sql = "INSERT OR IGNORE INTO student_parents (student_id, parent_id, relationship) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, parentId);
            pstmt.setString(3, relationship);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void unlink(int studentId, int parentId) {
        String sql = "DELETE FROM student_parents WHERE student_id = ? AND parent_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, parentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public List<Map<String, Object>> findStudentsByParentId(int parentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT student_id, relationship FROM student_parents WHERE parent_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, parentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getInt("student_id"));
                    row.put("relationship", rs.getString("relationship"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> findParentsByStudentId(int studentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT parent_id, relationship FROM student_parents WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("parentId", rs.getInt("parent_id"));
                    row.put("relationship", rs.getString("relationship"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT student_id, parent_id, relationship FROM student_parents ORDER BY student_id ASC";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentId", rs.getInt("student_id"));
                row.put("parentId", rs.getInt("parent_id"));
                row.put("relationship", rs.getString("relationship"));
                list.add(row);
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> search(String query, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        if (offset < 0) offset = 0;

        if (query == null || query.trim().isEmpty()) {
            String sql = "SELECT student_id, parent_id, relationship FROM student_parents ORDER BY student_id ASC LIMIT ? OFFSET ?";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, pageSize);
                pstmt.setInt(2, offset);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("studentId", rs.getInt("student_id"));
                        row.put("parentId", rs.getInt("parent_id"));
                        row.put("relationship", rs.getString("relationship"));
                        list.add(row);
                    }
                }
            } catch (SQLException e) {
                log.error("An exception occurred: ", e);
            }
        } else {
            String sql = "SELECT sp.student_id, sp.parent_id, sp.relationship FROM student_parents sp " +
                    "LEFT JOIN students s ON sp.student_id = s.id " +
                    "LEFT JOIN parents p ON sp.parent_id = p.id " +
                    "LEFT JOIN users su ON s.user_id = su.id " +
                    "LEFT JOIN users pu ON p.user_id = pu.id " +
                    "WHERE su.first_name LIKE ? OR su.last_name LIKE ? OR pu.first_name LIKE ? OR pu.last_name LIKE ? OR sp.relationship LIKE ? " +
                    "ORDER BY sp.student_id ASC LIMIT ? OFFSET ?";
            String searchPattern = "%" + query + "%";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
                pstmt.setString(4, searchPattern);
                pstmt.setString(5, searchPattern);
                pstmt.setInt(6, pageSize);
                pstmt.setInt(7, offset);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("studentId", rs.getInt("student_id"));
                        row.put("parentId", rs.getInt("parent_id"));
                        row.put("relationship", rs.getString("relationship"));
                        list.add(row);
                    }
                }
            } catch (SQLException e) {
                log.error("An exception occurred: ", e);
            }
        }
        return list;
    }

    @Override
    public int count(String query) {
        if (query == null || query.trim().isEmpty()) {
            String sql = "SELECT COUNT(*) FROM student_parents";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            } catch (SQLException e) {
                log.error("An exception occurred: ", e);
            }
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM student_parents sp " +
                "LEFT JOIN students s ON sp.student_id = s.id " +
                "LEFT JOIN parents p ON sp.parent_id = p.id " +
                "LEFT JOIN users su ON s.user_id = su.id " +
                "LEFT JOIN users pu ON p.user_id = pu.id " +
                "WHERE su.first_name LIKE ? OR su.last_name LIKE ? OR pu.first_name LIKE ? OR pu.last_name LIKE ? OR sp.relationship LIKE ?";
        String searchPattern = "%" + query + "%";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return 0;
    }
}

