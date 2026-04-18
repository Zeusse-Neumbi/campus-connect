package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.TeacherDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Teacher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDaoSqliteImpl implements TeacherDao {
    private static final Logger log = LoggerFactory.getLogger(TeacherDaoSqliteImpl.class);

    @Override
    public void save(Teacher teacher) {
        String sql = "INSERT INTO teachers (user_id, employee_id, specialization, hire_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacher.getUserId());
            pstmt.setString(2, teacher.getEmployeeId());
            pstmt.setString(3, teacher.getSpecialization());
            pstmt.setString(4, teacher.getHireDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void update(Teacher teacher) {
        String sql = "UPDATE teachers SET user_id = ?, employee_id = ?, specialization = ?, hire_date = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacher.getUserId());
            pstmt.setString(2, teacher.getEmployeeId());
            pstmt.setString(3, teacher.getSpecialization());
            pstmt.setString(4, teacher.getHireDate());
            pstmt.setInt(5, teacher.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM teachers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public Optional<Teacher> findById(int id) {
        String sql = "SELECT * FROM teachers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Teacher> findByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM teachers WHERE employee_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Teacher> findByUserId(int userId) {
        String sql = "SELECT * FROM teachers WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM teachers";
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teachers.add(mapResultSetToTeacher(rs));
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return teachers;
    }

    @Override
    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM teachers WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public List<Teacher> search(String query, int page, int pageSize) {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT t.* FROM teachers t " +
                "JOIN users u ON t.user_id = u.id " +
                "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR t.employee_id LIKE ? OR t.specialization LIKE ? " +
                "LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
        if (offset < 0)
            offset = 0;
        String searchPattern = "%" + query + "%";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setInt(5, pageSize);
            pstmt.setInt(6, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    teachers.add(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return teachers;
    }

    @Override
    public int count(String query) {
        String sql = "SELECT COUNT(*) FROM teachers t " +
                "JOIN users u ON t.user_id = u.id " +
                "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR t.employee_id LIKE ? OR t.specialization LIKE ?";
        String searchPattern = "%" + query + "%";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return 0;
    }

    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher t = new Teacher(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("employee_id"),
                rs.getString("specialization"),
                rs.getString("hire_date"));
        t.setCreatedAt(rs.getString("created_at"));
        t.setUpdatedAt(rs.getString("updated_at"));
        return t;
    }
}
