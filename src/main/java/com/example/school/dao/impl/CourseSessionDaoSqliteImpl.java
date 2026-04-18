package com.example.school.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.CourseSessionDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.CourseSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseSessionDaoSqliteImpl implements CourseSessionDao {
    private static final Logger log = LoggerFactory.getLogger(CourseSessionDaoSqliteImpl.class);

    @Override
    public int save(CourseSession session) {
        String sql = "INSERT INTO course_sessions (course_id, course_group_id, classroom_id, session_date, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, session.getCourseId());
            if (session.getCourseGroupId() != null) {
                pstmt.setInt(2, session.getCourseGroupId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setInt(3, session.getClassroomId());
            pstmt.setString(4, session.getSessionDate());
            pstmt.setString(5, session.getStartTime());
            pstmt.setString(6, session.getEndTime());
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
    public Optional<CourseSession> findById(int id) {
        String sql = "SELECT * FROM course_sessions WHERE id = ?";
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
    public List<CourseSession> findAll() {
        List<CourseSession> list = new ArrayList<>();
        String sql = "SELECT * FROM course_sessions ORDER BY session_date DESC, start_time ASC";
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
    public List<CourseSession> findByCourseId(int courseId) {
        List<CourseSession> list = new ArrayList<>();
        String sql = "SELECT * FROM course_sessions WHERE course_id = ? ORDER BY session_date DESC, start_time ASC";
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
    public List<CourseSession> findByDate(String date) {
        List<CourseSession> list = new ArrayList<>();
        String sql = "SELECT * FROM course_sessions WHERE session_date = ? ORDER BY start_time ASC";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
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
    public List<CourseSession> findByDateRange(String startDate, String endDate) {
        List<CourseSession> list = new ArrayList<>();
        String sql = "SELECT * FROM course_sessions WHERE session_date >= ? AND session_date <= ? ORDER BY session_date ASC, start_time ASC";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
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
    public List<CourseSession> findByCourseIdAndDate(int courseId, String date) {
        List<CourseSession> list = new ArrayList<>();
        String sql = "SELECT * FROM course_sessions WHERE course_id = ? AND session_date = ? ORDER BY start_time ASC";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, date);
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
    public void update(CourseSession session) {
        String sql = "UPDATE course_sessions SET course_id = ?, course_group_id = ?, classroom_id = ?, session_date = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, session.getCourseId());
            if (session.getCourseGroupId() != null) {
                pstmt.setInt(2, session.getCourseGroupId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setInt(3, session.getClassroomId());
            pstmt.setString(4, session.getSessionDate());
            pstmt.setString(5, session.getStartTime());
            pstmt.setString(6, session.getEndTime());
            pstmt.setInt(7, session.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM course_sessions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public List<CourseSession> search(String query, int page, int pageSize) {
        List<CourseSession> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        if (offset < 0) offset = 0;

        if (query == null || query.trim().isEmpty()) {
            String sql = "SELECT * FROM course_sessions ORDER BY session_date DESC, start_time ASC LIMIT ? OFFSET ?";
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
            String sql = "SELECT cs.* FROM course_sessions cs " +
                    "LEFT JOIN courses c ON cs.course_id = c.id " +
                    "LEFT JOIN classrooms cr ON cs.classroom_id = cr.id " +
                    "WHERE c.course_name LIKE ? OR c.course_code LIKE ? OR cr.room_code LIKE ? OR cs.session_date LIKE ? " +
                    "ORDER BY cs.session_date DESC, cs.start_time ASC LIMIT ? OFFSET ?";
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
            String sql = "SELECT COUNT(*) FROM course_sessions";
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            } catch (SQLException e) {
                log.error("An exception occurred: ", e);
            }
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM course_sessions cs " +
                "LEFT JOIN courses c ON cs.course_id = c.id " +
                "LEFT JOIN classrooms cr ON cs.classroom_id = cr.id " +
                "WHERE c.course_name LIKE ? OR c.course_code LIKE ? OR cr.room_code LIKE ? OR cs.session_date LIKE ?";
        String searchPattern = "%" + query + "%";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return 0;
    }

    private CourseSession mapResultSet(ResultSet rs) throws SQLException {
        int groupId = rs.getInt("course_group_id");
        Integer courseGroupId = rs.wasNull() ? null : groupId;
        CourseSession session = new CourseSession(
                rs.getInt("id"),
                rs.getInt("course_id"),
                courseGroupId,
                rs.getInt("classroom_id"),
                rs.getString("session_date"),
                rs.getString("start_time"),
                rs.getString("end_time"));
        session.setCreatedAt(rs.getString("created_at"));
        session.setUpdatedAt(rs.getString("updated_at"));
        return session;
    }
}

