package com.example.school.util.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.db.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SeederUtils {
    private static final Logger log = LoggerFactory.getLogger(SeederUtils.class);
    public static List<Integer> getIds(String tableName) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM " + tableName;
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return ids;
    }
}
