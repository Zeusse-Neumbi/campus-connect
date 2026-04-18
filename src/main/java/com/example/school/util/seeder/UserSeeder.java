package com.example.school.util.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.db.DatabaseManager;
import com.example.school.util.PasswordUtil;
import net.datafaker.Faker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UserSeeder {
    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);
    public static void seed(Faker faker, Random random) {
        String insertUser = "INSERT INTO users (email, password, role_id, first_name, last_name, phone) VALUES (?, ?, ?, ?, ?, ?)";
        String insertStudent = "INSERT INTO students (user_id, student_number, date_of_birth, gender, admission_date) VALUES (?, ?, ?, ?, ?)";
        String insertTeacher = "INSERT INTO teachers (user_id, employee_id, specialization, hire_date) VALUES (?, ?, ?, ?)";
        String insertParent = "INSERT INTO parents (user_id, address, occupation) VALUES (?, ?, ?)";

        String defaultPass = PasswordUtil.hashPassword("password123");
        String adminPass = PasswordUtil.hashPassword("admin123");

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmtUser = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement pstmtStudent = conn.prepareStatement(insertStudent);
                PreparedStatement pstmtTeacher = conn.prepareStatement(insertTeacher);
                PreparedStatement pstmtParent = conn.prepareStatement(insertParent)) {

            conn.setAutoCommit(false);

            // 1. Create Admin
            pstmtUser.setString(1, "admin@school.com");
            pstmtUser.setString(2, adminPass);
            pstmtUser.setInt(3, 1); // ADMIN
            pstmtUser.setString(4, "Super");
            pstmtUser.setString(5, "Admin");
            pstmtUser.setString(6, null);
            pstmtUser.executeUpdate();

            // 2. Create 50 Teachers
            Set<String> usedEmployeeIds = new HashSet<>();
            for (int i = 0; i < 50; i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@school.com";

                pstmtUser.setString(1, email);
                pstmtUser.setString(2, defaultPass);
                pstmtUser.setInt(3, 2); // TEACHER
                pstmtUser.setString(4, firstName);
                pstmtUser.setString(5, lastName);
                pstmtUser.setString(6, "+237 6" + faker.number().numberBetween(50000000, 99999999));
                pstmtUser.executeUpdate();

                try (java.sql.ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        pstmtTeacher.setInt(1, userId);

                        String empId;
                        do {
                            empId = "EMP" + faker.number().digits(4);
                        } while (usedEmployeeIds.contains(empId));
                        usedEmployeeIds.add(empId);

                        pstmtTeacher.setString(2, empId);
                        pstmtTeacher.setString(3, faker.educator().course());
                        LocalDate hireDate = LocalDate.now().minusYears(random.nextInt(10) + 1);
                        pstmtTeacher.setString(4, hireDate.toString());
                        pstmtTeacher.executeUpdate();
                    }
                }
            }

            // 3. Create 3000 Students
            Set<String> usedStudentNumbers = new HashSet<>();
            String[] genders = { "MALE", "FEMALE" };
            for (int i = 0; i < 3000; i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@student.school.com";

                pstmtUser.setString(1, email);
                pstmtUser.setString(2, defaultPass);
                pstmtUser.setInt(3, 3); // STUDENT
                pstmtUser.setString(4, firstName);
                pstmtUser.setString(5, lastName);
                pstmtUser.setString(6, "+237 6" + faker.number().numberBetween(50000000, 99999999));
                pstmtUser.executeUpdate();

                try (java.sql.ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        pstmtStudent.setInt(1, userId);

                        String studentNumber;
                        do {
                            studentNumber = faker.number().numberBetween(20, 25) + "G" + faker.number().numberBetween(1000, 9999);
                        } while (usedStudentNumbers.contains(studentNumber));
                        usedStudentNumbers.add(studentNumber);

                        pstmtStudent.setString(2, studentNumber);

                        LocalDate dob = faker.date().birthday(18, 25).toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        pstmtStudent.setString(3, dob.toString());
                        pstmtStudent.setString(4, genders[random.nextInt(genders.length)]);
                        pstmtStudent.setString(5, LocalDate.now().minusMonths(random.nextInt(24)).toString());

                        pstmtStudent.executeUpdate();
                    }
                }
            }

            // 4. Create 2000 parents
            for (int i = 0; i < 2000; i++) {
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@parent.school.com";

                pstmtUser.setString(1, email);
                pstmtUser.setString(2, defaultPass);
                pstmtUser.setInt(3, 4); // PARENT
                pstmtUser.setString(4, firstName);
                pstmtUser.setString(5, lastName);
                pstmtUser.setString(6, "+237 6" + faker.number().numberBetween(50000000, 99999999));
                pstmtUser.executeUpdate();

                try (java.sql.ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        pstmtParent.setInt(1, userId);
                        pstmtParent.setString(2, faker.address().fullAddress());
                        pstmtParent.setString(3, faker.job().title());
                        pstmtParent.executeUpdate();
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }
}
