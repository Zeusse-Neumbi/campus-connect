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

                // 20% chance to not have groups
                if (random.nextDouble() > 0.8) continue;

                if (enrolledStudentIds.isEmpty()) continue;

                // Random capacity for this course (10, 15, 20 or 25)
                int courseCapacity = (random.nextInt(4) + 2) * 5; 
                int numGroups = (int) Math.ceil((double) enrolledStudentIds.size() / courseCapacity);
                if (numGroups == 0) numGroups = 1;

                List<Integer> groupIds = new ArrayList<>();
                String[] groupTypes = {"Group ", "Lab ", "Tutorial ", "Seminar "};
                String type = groupTypes[random.nextInt(groupTypes.length)];

                for (int i = 0; i < numGroups; i++) {
                    pstmtGroup.setInt(1, courseId);
                    pstmtGroup.setString(2, type + groupSuffixes[i % groupSuffixes.length]);
                    pstmtGroup.setInt(3, courseCapacity);
                    pstmtGroup.executeUpdate();

                    try (ResultSet rsGroup = pstmtGroup.getGeneratedKeys()) {
                        if (rsGroup.next()) {
                            groupIds.add(rsGroup.getInt(1));
                        }
                    }
                }

                // Randomly divide students
                java.util.Collections.shuffle(enrolledStudentIds);
                int studentIdx = 0;
                for (int groupId : groupIds) {
                    for (int i = 0; i < courseCapacity && studentIdx < enrolledStudentIds.size(); i++) {
                        pstmtStudentGroup.setInt(1, enrolledStudentIds.get(studentIdx++));
                        pstmtStudentGroup.setInt(2, groupId);
                        try {
                            pstmtStudentGroup.executeUpdate();
                        } catch (SQLException ex) { }
                    }
                }

                // (Students already randomly divided among the groups)
            }

            conn.commit();

        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }
}
