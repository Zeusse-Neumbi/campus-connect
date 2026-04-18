package com.example.school.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.db.DatabaseManager;
import com.example.school.util.seeder.*;
import net.datafaker.Faker;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class DataSeeder {
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final Faker faker;
    private final Random random;

    public DataSeeder() {
        this.faker = new Faker();
        this.random = new Random();
    }

    public void seed() {
        if (isDatabaseEmpty()) {
            System.out.println("Seeding database...");
            UserSeeder.seed(faker, random);
            StudentParentSeeder.seed(random);
            CourseSeeder.seed(faker, random);
            ClassroomSeeder.seed(random);
            EnrollmentAndExamSeeder.seed(faker, random);
            GroupSeeder.seed(faker, random);
            System.out.println("Database seeding completed.");
        } else {
            System.out.println("Database already contains data. Skipping seeding.");
        }
    }

    private boolean isDatabaseEmpty() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
        return true;
    }
}
