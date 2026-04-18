package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.ParentDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Parent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParentDaoSqliteImpl implements ParentDao {
    private static final Logger log = LoggerFactory.getLogger(ParentDaoSqliteImpl.class);

    @Override
    public int save(Parent parent) {
        String sql = "INSERT INTO parents (user_id, address, occupation) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, parent.getUserId());
            pstmt.setString(2, parent.getAddress());
            pstmt.setString(3, parent.getOccupation());
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
    public Optional<Parent> findById(int id) {
        String sql = "SELECT * FROM parents WHERE id = ?";
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
    public Optional<Parent> findByUserId(int userId) {
        String sql = "SELECT * FROM parents WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
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
    public List<Parent> findAll() {
        List<Parent> list = new ArrayList<>();
        String sql = "SELECT * FROM parents ORDER BY id ASC";
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
    public void update(Parent parent) {
        String sql = "UPDATE parents SET address = ?, occupation = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, parent.getAddress());
            pstmt.setString(2, parent.getOccupation());
            pstmt.setInt(3, parent.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM parents WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM parents WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM parents";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return 0;
    }

    @Override
    public int count(String query) {
        if (query == null || query.trim().isEmpty()) {
            return count();
        }
        String sql = "SELECT COUNT(*) FROM parents p JOIN users u ON p.user_id = u.id " +
                "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ? OR p.occupation LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            pstmt.setString(1, q);
            pstmt.setString(2, q);
            pstmt.setString(3, q);
            pstmt.setString(4, q);
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

    @Override
    public List<Parent> search(String query, int page, int pageSize) {
        List<Parent> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        if (query == null || query.trim().isEmpty()) {
            String sql = "SELECT p.* FROM parents p ORDER BY p.id ASC LIMIT ? OFFSET ?";
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
            String sql = "SELECT p.* FROM parents p JOIN users u ON p.user_id = u.id " +
                    "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ? OR p.occupation LIKE ? " +
                    "ORDER BY p.id ASC LIMIT ? OFFSET ?";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String q = "%" + query + "%";
                pstmt.setString(1, q);
                pstmt.setString(2, q);
                pstmt.setString(3, q);
                pstmt.setString(4, q);
                pstmt.setInt(5, pageSize);
                pstmt.setInt(6, offset);
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

    private Parent mapResultSet(ResultSet rs) throws SQLException {
        Parent p = new Parent(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("address"),
                rs.getString("occupation"));
        p.setCreatedAt(rs.getString("created_at"));
        p.setUpdatedAt(rs.getString("updated_at"));
        return p;
    }
}
