package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.ClassroomDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Classroom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassroomDaoSqliteImpl implements ClassroomDao {
    private static final Logger log = LoggerFactory.getLogger(ClassroomDaoSqliteImpl.class);

    @Override
    public int save(Classroom classroom) {
        String sql = "INSERT INTO classrooms (room_code, building, capacity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, classroom.getRoomCode());
            pstmt.setString(2, classroom.getBuilding());
            pstmt.setInt(3, classroom.getCapacity());
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
    public Optional<Classroom> findById(int id) {
        String sql = "SELECT * FROM classrooms WHERE id = ?";
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
    public List<Classroom> findAll() {
        List<Classroom> list = new ArrayList<>();
        String sql = "SELECT * FROM classrooms ORDER BY room_code ASC";
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
    public void update(Classroom classroom) {
        String sql = "UPDATE classrooms SET room_code = ?, building = ?, capacity = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classroom.getRoomCode());
            pstmt.setString(2, classroom.getBuilding());
            pstmt.setInt(3, classroom.getCapacity());
            pstmt.setInt(4, classroom.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM classrooms WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public List<Classroom> search(String query, int page, int pageSize) {
        List<Classroom> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        if (offset < 0) offset = 0;

        if (query == null || query.trim().isEmpty()) {
            String sql = "SELECT * FROM classrooms ORDER BY room_code ASC LIMIT ? OFFSET ?";
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
            String sql = "SELECT * FROM classrooms WHERE room_code LIKE ? OR building LIKE ? ORDER BY room_code ASC LIMIT ? OFFSET ?";
            String searchPattern = "%" + query + "%";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setInt(3, pageSize);
                pstmt.setInt(4, offset);
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
            String sql = "SELECT COUNT(*) FROM classrooms";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            } catch (SQLException e) {
                log.error("An exception occurred: ", e);
            }
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM classrooms WHERE room_code LIKE ? OR building LIKE ?";
        String searchPattern = "%" + query + "%";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return 0;
    }

    @Override
    public List<Classroom> searchWithFilters(String query, List<String> buildings, Integer minCapacity, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM classrooms WHERE 1=1");
        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (room_code LIKE ? OR building LIKE ?)");
        }
        if (buildings != null && !buildings.isEmpty()) {
            sql.append(" AND (");
            for (int i = 0; i < buildings.size(); i++) {
                sql.append("building LIKE ?");
                if (i < buildings.size() - 1) sql.append(" OR ");
            }
            sql.append(")");
        }
        if (minCapacity != null) {
            sql.append(" AND capacity >= ?");
        }
        sql.append(" ORDER BY room_code ASC LIMIT ? OFFSET ?");

        List<Classroom> classrooms = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (query != null && !query.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + query.trim() + "%");
                pstmt.setString(paramIndex++, "%" + query.trim() + "%");
            }
            if (buildings != null && !buildings.isEmpty()) {
                for (String building : buildings) {
                    pstmt.setString(paramIndex++, "%" + building + "%");
                }
            }
            if (minCapacity != null) {
                pstmt.setInt(paramIndex++, minCapacity);
            }
            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex++, (page - 1) * pageSize);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                classrooms.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return classrooms;
    }

    @Override
    public int countWithFilters(String query, List<String> buildings, Integer minCapacity) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM classrooms WHERE 1=1");
        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (room_code LIKE ? OR building LIKE ?)");
        }
        if (buildings != null && !buildings.isEmpty()) {
            sql.append(" AND (");
            for (int i = 0; i < buildings.size(); i++) {
                sql.append("building LIKE ?");
                if (i < buildings.size() - 1) sql.append(" OR ");
            }
            sql.append(")");
        }
        if (minCapacity != null) {
            sql.append(" AND capacity >= ?");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
             
            int paramIndex = 1;
            if (query != null && !query.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + query.trim() + "%");
                pstmt.setString(paramIndex++, "%" + query.trim() + "%");
            }
            if (buildings != null && !buildings.isEmpty()) {
                for (String building : buildings) {
                    pstmt.setString(paramIndex++, "%" + building + "%");
                }
            }
            if (minCapacity != null) {
                pstmt.setInt(paramIndex++, minCapacity);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return 0;
    }

    private Classroom mapResultSet(ResultSet rs) throws SQLException {
        Classroom c = new Classroom(
                rs.getInt("id"),
                rs.getString("room_code"),
                rs.getString("building"),
                rs.getInt("capacity"));
        c.setCreatedAt(rs.getString("created_at"));
        c.setUpdatedAt(rs.getString("updated_at"));
        return c;
    }
}

