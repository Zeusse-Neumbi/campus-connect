package com.example.school.util.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.db.DatabaseManager;
import net.datafaker.Faker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class EnrollmentAndExamSeeder {
    private static final Logger log = LoggerFactory.getLogger(EnrollmentAndExamSeeder.class);
    public static void seed(Faker faker, Random random) {
        String insertEnrollment = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
        String insertExam = "INSERT INTO exams (course_id, exam_name, exam_type, exam_date, max_score) VALUES (?, ?, ?, ?, ?)";
        String insertResult = "INSERT INTO exam_results (exam_id, student_id, score, remark) VALUES (?, ?, ?, ?)";
        String insertSession = "INSERT INTO course_sessions (course_id, classroom_id, session_date, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        String insertAttendance = "INSERT INTO attendance (course_session_id, student_id, status) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmtEnroll = conn.prepareStatement(insertEnrollment);
                PreparedStatement pstmtExam = conn.prepareStatement(insertExam, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement pstmtResult = conn.prepareStatement(insertResult);
                PreparedStatement pstmtSession = conn.prepareStatement(insertSession, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement pstmtAttendance = conn.prepareStatement(insertAttendance)) {

            conn.setAutoCommit(false);

            List<Integer> studentIds = SeederUtils.getIds("students");
            List<Integer> courseIds = SeederUtils.getIds("courses");
            List<Integer> classroomIds = SeederUtils.getIds("classrooms");

            if (studentIds.isEmpty() || courseIds.isEmpty() || classroomIds.isEmpty())
                return;

            // Base semester start timeline
            LocalDate semesterStart = LocalDate.now().minusMonths(2);

            // Realistic exams: 1 Quiz, 1 Midterm, 1 Final per course
            String[] examTypes = { "QUIZ", "MIDTERM", "FINAL" };
            double[] maxExamScores = { 20.0, 20.0, 20.0 };
            int[] examWeeksAfterStart = { 4, 8, 15 };

            Map<Integer, List<Integer>> courseExamMap = new HashMap<>();
            Map<Integer, List<Double>> courseExamMaxScores = new HashMap<>();
            
            for (int courseId : courseIds) {
                List<Integer> examIds = new ArrayList<>();
                List<Double> maxScores = new ArrayList<>();
                for (int j = 0; j < examTypes.length; j++) {
                    pstmtExam.setInt(1, courseId);
                    pstmtExam.setString(2, examTypes[j] + " - " + faker.educator().course());
                    pstmtExam.setString(3, examTypes[j]);
                    pstmtExam.setString(4, semesterStart.plusWeeks(examWeeksAfterStart[j]).toString());
                    pstmtExam.setDouble(5, maxExamScores[j]);
                    pstmtExam.executeUpdate();
                    try (java.sql.ResultSet rs = pstmtExam.getGeneratedKeys()) {
                        if (rs.next()) {
                            examIds.add(rs.getInt(1));
                            maxScores.add(maxExamScores[j]);
                        }
                    }
                }
                courseExamMap.put(courseId, examIds);
                courseExamMaxScores.put(courseId, maxScores);
            }

            // Create course sessions (1 to 3 per week per course for 15 weeks)
            String[][] timeSlots = {
                {"08:00", "10:00"}, {"10:15", "12:15"}, {"13:00", "15:00"}, {"15:15", "17:15"}
            };
            Map<Integer, Map<Integer, LocalDate>> courseSessionMap = new HashMap<>();
            
            Map<LocalDate, Map<String, Integer>> roomBookings = new HashMap<>();
            Map<LocalDate, Map<String, Integer>> teacherBookings = new HashMap<>();

            for (int courseId : courseIds) {
                int teacherId = -1;
                String selectTeacher = "SELECT teacher_id FROM courses WHERE id = " + courseId;
                try (Statement stmt = conn.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(selectTeacher)) {
                    if (rs.next()) teacherId = rs.getInt("teacher_id");
                }
                
                Map<Integer, LocalDate> sessionMap = new HashMap<>();
                
                for (int week = 0; week < 15; week++) {
                    int sessionsThisWeek = 1 + random.nextInt(3); // 1 to 3 sessions
                    Set<Integer> usedDays = new HashSet<>();
                    
                    for (int s = 0; s < sessionsThisWeek; s++) {
                        int offset;
                        LocalDate sessionDate;
                        do {
                            offset = random.nextInt(7); // 0 to 6 (7 days)
                            sessionDate = semesterStart.plusWeeks(week).plusDays(offset);
                        } while (usedDays.contains(offset) || sessionDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY);
                        usedDays.add(offset);
                        String[] slot = timeSlots[random.nextInt(timeSlots.length)];
                        String slotKey = slot[0] + "-" + slot[1];

                        int attempts = 0;
                        int classroomId = -1;
                        boolean booked = false;

                        while (attempts < 10) {
                            classroomId = classroomIds.get(random.nextInt(classroomIds.size()));
                            
                            // Check Room Booking
                            Map<String, Integer> dayRooms = roomBookings.computeIfAbsent(sessionDate, k -> new HashMap<>());
                            if (dayRooms.containsKey(slotKey + "-room-" + classroomId)) {
                                attempts++; continue;
                            }
                            
                            // Check Teacher Booking
                            Map<String, Integer> dayTeachers = teacherBookings.computeIfAbsent(sessionDate, k -> new HashMap<>());
                            if (teacherId != -1 && dayTeachers.containsKey(slotKey + "-teacher-" + teacherId)) {
                                attempts++; continue;
                            }
                            
                            // Valid
                            dayRooms.put(slotKey + "-room-" + classroomId, 1);
                            dayTeachers.put(slotKey + "-teacher-" + teacherId, 1);
                            booked = true;
                            break;
                        }

                        if (!booked) continue;

                        pstmtSession.setInt(1, courseId);
                        pstmtSession.setInt(2, classroomId);
                        pstmtSession.setString(3, sessionDate.toString());
                        pstmtSession.setString(4, slot[0]);
                        pstmtSession.setString(5, slot[1]);
                        pstmtSession.executeUpdate();
                        try (java.sql.ResultSet rs = pstmtSession.getGeneratedKeys()) {
                            if (rs.next()) {
                                sessionMap.put(rs.getInt(1), sessionDate);
                            }
                        }
                    }
                }
                courseSessionMap.put(courseId, sessionMap);
            }

            // Enroll each student in 4-6 courses and seed realistic exam results + attendance
            for (int studentId : studentIds) {
                int coursesCount = random.nextInt(3) + 4;
                Set<Integer> assignedCourses = new HashSet<>();
                while (assignedCourses.size() < coursesCount) {
                    int courseId = courseIds.get(random.nextInt(courseIds.size()));
                    if (assignedCourses.contains(courseId))
                        continue;
                    assignedCourses.add(courseId);

                    try {
                        pstmtEnroll.setInt(1, studentId);
                        pstmtEnroll.setInt(2, courseId);
                        // Enrollments happen shortly before semester starts
                        pstmtEnroll.setString(3, semesterStart.minusDays(random.nextInt(14)).toString()); 
                        pstmtEnroll.executeUpdate();

                        // Seed exam results with a gaussian curve for realistic grades
                        List<Integer> examIds = courseExamMap.get(courseId);
                        List<Double> maxScoresInfo = courseExamMaxScores.get(courseId);
                        
                        // Student's baseline ability in this specific course (50% to 95%)
                        double studentAbility = 0.50 + (random.nextDouble() * 0.45);

                        if (examIds != null) {
                            for (int i = 0; i < examIds.size(); i++) {
                                LocalDate examDate = semesterStart.plusWeeks(examWeeksAfterStart[i]);
                                if (examDate.isAfter(LocalDate.now())) {
                                    continue;
                                }

                                int examId = examIds.get(i);
                                double maxScore = maxScoresInfo.get(i);
                                
                                // Apply some variance per exam
                                double variance = random.nextGaussian() * 0.1;
                                double rawScoreRatio = Math.max(0.0, Math.min(1.0, studentAbility + variance));
                                int score = (int) Math.round(rawScoreRatio * maxScore);
                                
                                String remark;
                                if (rawScoreRatio >= 0.9) remark = "Excellent";
                                else if (rawScoreRatio >= 0.75) remark = "Good";
                                else if (rawScoreRatio >= 0.60) remark = "Average";
                                else if (rawScoreRatio >= 0.50) remark = "Pass";
                                else remark = "Fail";

                                pstmtResult.setInt(1, examId);
                                pstmtResult.setInt(2, studentId);
                                pstmtResult.setInt(3, score);
                                pstmtResult.setString(4, remark);
                                pstmtResult.executeUpdate();
                            }
                        }

                        // Seed attendance based on student reliability
                        double attendanceHabit = 0.7 + (random.nextDouble() * 0.3); // 70% to 100% reliable
                        Map<Integer, LocalDate> sessionMap = courseSessionMap.get(courseId);
                        if (sessionMap != null) {
                            for (Map.Entry<Integer, LocalDate> entry : sessionMap.entrySet()) {
                                if (entry.getValue().isAfter(LocalDate.now())) {
                                    continue;
                                }
                                int sessionId = entry.getKey();
                                
                                double r = random.nextDouble();
                                String status;
                                if (r < attendanceHabit) status = "PRESENT";
                                else if (r < attendanceHabit + 0.1) status = "LATE";
                                else status = "ABSENT";
                                
                                pstmtAttendance.setInt(1, sessionId);
                                pstmtAttendance.setInt(2, studentId);
                                pstmtAttendance.setString(3, status);
                                pstmtAttendance.executeUpdate();
                            }
                        }

                    } catch (SQLException e) {
                        log.error("An exception occurred: ", e);
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }
}
