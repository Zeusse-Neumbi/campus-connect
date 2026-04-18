package com.example.school.util.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.db.DatabaseManager;
import net.datafaker.Faker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupSeeder {
    private static final Logger log = LoggerFactory.getLogger(GroupSeeder.class);

    public static void seed(Faker faker, Random random) {
        String insertGroup = "INSERT INTO course_groups (course_id, group_name, capacity) VALUES (?, ?, ?)";
        String insertStudentGroup = "INSERT INTO student_groups (student_id, course_group_id) VALUES (?, ?)";
        String selectEnrollments = "SELECT student_id FROM enrollments WHERE course_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmtGroup = conn.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtStudentGroup = conn.prepareStatement(insertStudentGroup);
             PreparedStatement pstmtEnrollments = conn.prepareStatement(selectEnrollments)) {

            conn.setAutoCommit(false);

            List<Integer> courseIds = SeederUtils.getIds("courses");
            if (courseIds.isEmpty()) return;

            String[] groupSuffixes = {"A", "B", "C", "D", "E"};

            for (int courseId : courseIds) {
                // Fetch enrolled students for this course
                pstmtEnrollments.setInt(1, courseId);
                List<Integer> enrolledStudentIds = new ArrayList<>();
                try (ResultSet rs = pstmtEnrollments.executeQuery()) {
                    while (rs.next()) {
                        enrolledStudentIds.add(rs.getInt("student_id"));
                    }
                }

                if (enrolledStudentIds.isEmpty()) continue;

                // Create 2 groups for the course
                int numGroups = 5; // Split into Group A and Group B
                List<Integer> groupIds = new ArrayList<>();

                for (int i = 0; i < numGroups; i++) {
                    pstmtGroup.setInt(1, courseId);
                    pstmtGroup.setString(2, "Group " + groupSuffixes[i]);
                    pstmtGroup.setInt(3, 10); // Standard capacity
                    pstmtGroup.executeUpdate();

                    try (ResultSet rsGroup = pstmtGroup.getGeneratedKeys()) {
                        if (rsGroup.next()) {
                            groupIds.add(rsGroup.getInt(1));
                        }
                    }
                }

                // Randomly assign enrolled students into these groups
                for (int studentId : enrolledStudentIds) {
                    int groupId = groupIds.get(random.nextInt(groupIds.size()));
                    pstmtStudentGroup.setInt(1, studentId);
                    pstmtStudentGroup.setInt(2, groupId);
                    try {
                        pstmtStudentGroup.executeUpdate();
                    } catch (SQLException ex) {
                        // Ignore constraint violations if a student is somehow assigned twice
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }
}
