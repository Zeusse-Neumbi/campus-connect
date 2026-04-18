package com.example.school.service;

import com.example.school.dao.*;
import com.example.school.model.*;
import java.util.*;

public class ParentService {

    private final ParentDao parentDao;
    private final StudentParentDao studentParentDao;
    private final StudentDao studentDao;
    private final UserDao userDao;
    private final CourseDao courseDao;
    private final EnrollmentDao enrollmentDao;
    private final ExamDao examDao;
    private final ExamResultDao examResultDao;
    private final AttendanceDao attendanceDao;
    private final CourseSessionDao courseSessionDao;

    public ParentService(ParentDao parentDao, StudentParentDao studentParentDao,
            StudentDao studentDao, UserDao userDao, CourseDao courseDao,
            EnrollmentDao enrollmentDao, ExamDao examDao, ExamResultDao examResultDao,
            AttendanceDao attendanceDao, CourseSessionDao courseSessionDao) {
        this.parentDao = parentDao;
        this.studentParentDao = studentParentDao;
        this.studentDao = studentDao;
        this.userDao = userDao;
        this.courseDao = courseDao;
        this.enrollmentDao = enrollmentDao;
        this.examDao = examDao;
        this.examResultDao = examResultDao;
        this.attendanceDao = attendanceDao;
        this.courseSessionDao = courseSessionDao;
    }

    public Optional<Parent> getParentByUserId(int userId) {
        return parentDao.findByUserId(userId);
    }

    /**
     * Returns linked students for a parent. Each map has: student, user, relationship.
     */
    public List<Map<String, Object>> getLinkedStudents(int parentId) {
        List<Map<String, Object>> links = studentParentDao.findStudentsByParentId(parentId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> link : links) {
            int studentId = (int) link.get("studentId");
            String relationship = (String) link.get("relationship");

            Optional<Student> studentOpt = studentDao.findById(studentId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                Optional<User> userOpt = userDao.findById(student.getUserId());
                if (userOpt.isPresent()) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("student", student);
                    entry.put("user", userOpt.get());
                    entry.put("relationship", relationship);
                    result.add(entry);
                }
            }
        }
        return result;
    }

    public void updateParent(Parent parent) {
        parentDao.update(parent);
    }

    /**
     * Verify that a given student is linked to this parent.
     */
    public boolean isLinkedToStudent(int parentId, int studentId) {
        List<Map<String, Object>> links = studentParentDao.findStudentsByParentId(parentId);
        return links.stream().anyMatch(l -> (int) l.get("studentId") == studentId);
    }

    public List<Course> getStudentCourses(int studentId) {
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(studentId);
        List<Course> courses = new ArrayList<>();
        for (Enrollment e : enrollments) {
            Optional<Course> c = courseDao.findById(e.getCourseId());
            c.ifPresent(courses::add);
        }
        return courses;
    }

    /**
     * Returns exam results for a student with exam and course lookup maps.
     * Map keys: examResults, examMap, courseMap
     */
    public Map<String, Object> getStudentGradesData(int studentId) {
        List<ExamResult> examResults = examResultDao.findByStudentId(studentId);
        Map<Integer, Exam> examMap = new HashMap<>();
        Map<Integer, Course> courseMap = new HashMap<>();

        for (ExamResult er : examResults) {
            if (!examMap.containsKey(er.getExamId())) {
                examDao.findById(er.getExamId()).ifPresent(exam -> {
                    examMap.put(exam.getId(), exam);
                    if (!courseMap.containsKey(exam.getCourseId())) {
                        courseDao.findById(exam.getCourseId()).ifPresent(c -> courseMap.put(c.getId(), c));
                    }
                });
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("examResults", examResults);
        data.put("examMap", examMap);
        data.put("courseMap", courseMap);
        return data;
    }

    /**
     * Returns attendance records for a student with session and course maps.
     * Map keys: attendanceList, sessionMap, courseMap
     */
    public Map<String, Object> getStudentAttendanceData(int studentId) {
        List<Attendance> attendanceList = attendanceDao.findByStudentId(studentId);
        Map<Integer, CourseSession> sessionMap = new HashMap<>();
        Map<Integer, Course> courseMap = new HashMap<>();

        for (Attendance att : attendanceList) {
            int sessionId = att.getCourseSessionId();
            if (!sessionMap.containsKey(sessionId)) {
                courseSessionDao.findById(sessionId).ifPresent(cs -> {
                    sessionMap.put(sessionId, cs);
                    if (!courseMap.containsKey(cs.getCourseId())) {
                        courseDao.findById(cs.getCourseId()).ifPresent(c -> courseMap.put(c.getId(), c));
                    }
                });
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("attendanceList", attendanceList);
        data.put("sessionMap", sessionMap);
        data.put("courseMap", courseMap);
        return data;
    }

    /**
     * Returns dashboard stats for one student.
     * Map keys: courseCount, gpa, attendanceRate
     */
    public Map<String, Object> getStudentDashboardStats(int studentId) {
        Map<String, Object> stats = new HashMap<>();
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(studentId);
        stats.put("courseCount", enrollments.size());

        // GPA
        List<ExamResult> results = examResultDao.findByStudentId(studentId);
        double gpa = 0.0;
        if (!results.isEmpty()) {
            double total = 0;
            for (ExamResult r : results) {
                total += r.getScore();
            }
            gpa = total / results.size();
        }
        stats.put("gpa", String.format("%.1f", gpa));

        // Attendance rate
        double rate = attendanceDao.getAttendanceRate(studentId);
        stats.put("attendanceRate", String.format("%.0f", rate));

        return stats;
    }
}
