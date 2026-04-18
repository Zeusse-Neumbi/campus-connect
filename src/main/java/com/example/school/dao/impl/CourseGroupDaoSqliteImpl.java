package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.CourseGroupDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.CourseGroup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseGroupDaoSqliteImpl implements CourseGroupDao {
    private static final Logger log = LoggerFactory.getLogger(CourseGroupDaoSqliteImpl.class);

    @Override
    public int save(CourseGroup group) {
        String sql = "INSERT INTO course_groups (course_id, group_name, capacity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, group.getCourseId());
            pstmt.setString(2, group.getGroupName());
            pstmt.setInt(3, group.getCapacity());
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
    public Optional<CourseGroup> findById(int id) {
        String sql = "SELECT * FROM course_groups WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<CourseGroup> findAll() {
        List<CourseGroup> list = new ArrayList<>();
        String sql = "SELECT * FROM course_groups ORDER BY course_id ASC, group_name ASC";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return list;
    }

    @Override
    public List<CourseGroup> findByCourseId(int courseId) {
        List<CourseGroup> list = new ArrayList<>();
        String sql = "SELECT * FROM course_groups WHERE course_id = ? ORDER BY group_name ASC";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
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
    public void update(CourseGroup group) {
        String sql = "UPDATE course_groups SET course_id = ?, group_name = ?, capacity = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, group.getCourseId());
            pstmt.setString(2, group.getGroupName());
            pstmt.setInt(3, group.getCapacity());
            pstmt.setInt(4, group.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM course_groups WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public List<CourseGroup> search(String query, int page, int pageSize) {
        List<CourseGroup> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        if (offset < 0) offset = 0;

        if (query == null || query.trim().isEmpty()) {
            String sql = "SELECT * FROM course_groups ORDER BY course_id ASC, group_name ASC LIMIT ? OFFSET ?";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, pageSize);
                pstmt.setInt(2, offset);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapResultSet(rs));
                    }
                }
            } catch (SQLException e) {
                log.error("An exception occurred: ", e);
            }
        } else {
            String sql = "SELECT cg.* FROM course_groups cg " +
                    "LEFT JOIN courses c ON cg.course_id = c.id " +
                    "WHERE cg.group_name LIKE ? OR c.course_name LIKE ? OR c.course_code LIKE ? " +
                    "ORDER BY cg.course_id ASC, cg.group_name ASC LIMIT ? OFFSET ?";
            String searchPattern = "%" + query + "%";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
                pstmt.setInt(4, pageSize);
                pstmt.setInt(5, offset);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapResultSet(rs));
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
            String sql = "SELECT COUNT(*) FROM course_groups";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            } catch (SQLException e) {
                log.error("An exception occurred: ", e);
            }
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM course_groups cg " +
                "LEFT JOIN courses c ON cg.course_id = c.id " +
                "WHERE cg.group_name LIKE ? OR c.course_name LIKE ? OR c.course_code LIKE ?";
        String searchPattern = "%" + query + "%";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return 0;
    }

    private CourseGroup mapResultSet(ResultSet rs) throws SQLException {
        CourseGroup g = new CourseGroup(
                rs.getInt("id"),
                rs.getInt("course_id"),
                rs.getString("group_name"),
                rs.getInt("capacity"));
        g.setCreatedAt(rs.getString("created_at"));
        g.setUpdatedAt(rs.getString("updated_at"));
        return g;
    }
}

