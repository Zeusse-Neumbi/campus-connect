package com.example.school.util.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.db.DatabaseManager;
import net.datafaker.Faker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CourseSeeder {
    private static final Logger log = LoggerFactory.getLogger(CourseSeeder.class);
    public static void seed(Faker faker, Random random) {
        String sql = "INSERT INTO courses (course_code, course_name, description, credits, teacher_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            List<Integer> teacherIds = SeederUtils.getIds("teachers");
            if (teacherIds.isEmpty())
                return;

            Set<String> usedCodes = new HashSet<>();
            for (int i = 0; i < 200; i++) {
                String code;
                do {
                    code = "EPDSBI" + faker.number().digits(4);
                } while (usedCodes.contains(code));
                usedCodes.add(code);

                pstmt.setString(1, code);
                pstmt.setString(2, faker.educator().course());
                pstmt.setString(3, faker.lorem().sentence());
                pstmt.setInt(4, faker.number().numberBetween(2, 6));
                pstmt.setInt(5, teacherIds.get(random.nextInt(teacherIds.size())));
                pstmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }
}
