package com.example.school.service;

import com.example.school.dao.*;
import com.example.school.model.*;
import com.example.school.util.PasswordUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class TeacherService {

    private final TeacherDao teacherDao;
    private final CourseDao courseDao;
    private final EnrollmentDao enrollmentDao;
    private final StudentDao studentDao;
    private final UserDao userDao;
    private final ExamDao examDao;
    private final ExamResultDao examResultDao;
    private final AttendanceDao attendanceDao;
    private final CourseSessionDao courseSessionDao;
    private final ClassroomDao classroomDao;
    private final CourseGroupDao courseGroupDao;
    private final StudentGroupDao studentGroupDao;

    public TeacherService(TeacherDao teacherDao, CourseDao courseDao, EnrollmentDao enrollmentDao,
            StudentDao studentDao, UserDao userDao, ExamDao examDao,
            ExamResultDao examResultDao, AttendanceDao attendanceDao,
            CourseSessionDao courseSessionDao, ClassroomDao classroomDao,
            CourseGroupDao courseGroupDao, StudentGroupDao studentGroupDao) {
        this.teacherDao = teacherDao;
        this.courseDao = courseDao;
        this.enrollmentDao = enrollmentDao;
        this.studentDao = studentDao;
        this.userDao = userDao;
        this.examDao = examDao;
        this.examResultDao = examResultDao;
        this.attendanceDao = attendanceDao;
        this.courseSessionDao = courseSessionDao;
        this.classroomDao = classroomDao;
        this.courseGroupDao = courseGroupDao;
        this.studentGroupDao = studentGroupDao;
    }

    public Optional<Teacher> getTeacherByUserId(int userId) {
        return teacherDao.findByUserId(userId);
    }

    public List<Course> getTeacherCourses(int teacherId) {
        return courseDao.findByTeacherId(teacherId);
    }

    public int getStudentCountForTeacher(int teacherId) {
        List<Course> courses = getTeacherCourses(teacherId);
        int totalStudents = 0;
        for (Course c : courses) {
            totalStudents += enrollmentDao.findByCourseId(c.getId()).size();
        }
        return totalStudents;
    }

    public Map<Integer, Integer> getStudentCountsPerCourse(List<Course> courses) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Course c : courses) {
            counts.put(c.getId(), enrollmentDao.findByCourseId(c.getId()).size());
        }
        return counts;
    }

    public List<Map<String, Object>> getCourseStudentsData(int courseId, String searchQuery) {
        List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);
        List<Map<String, Object>> studentsData = new ArrayList<>();

        for (Enrollment e : enrollments) {
            Optional<Student> sOpt = studentDao.findById(e.getStudentId());
            if (sOpt.isPresent()) {
                Student s = sOpt.get();
                Optional<User> uOpt = userDao.findById(s.getUserId());
                if (uOpt.isPresent()) {
                    User u = uOpt.get();

                    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                        String q = searchQuery.toLowerCase();
                        if (!s.getStudentNumber().toLowerCase().contains(q) &&
                                !u.getFirstName().toLowerCase().contains(q) &&
                                !u.getLastName().toLowerCase().contains(q)) {
                            continue;
                        }
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("student", s);
                    map.put("user", u);
                    map.put("enrollmentId", e.getId());
                    map.put("studentId", s.getId());
                    studentsData.add(map);
                }
            }
        }
        return studentsData;
    }

    public Map<Integer, Integer> getExamResultsForStudents(int examId, List<Map<String, Object>> studentsData) {
        Map<Integer, Integer> resultsMap = new HashMap<>();
        for (Map<String, Object> data : studentsData) {
            Integer studentId = (Integer) data.get("studentId");
            Optional<ExamResult> result = examResultDao.findByExamIdAndStudentId(examId, studentId);
            result.ifPresent(r -> resultsMap.put(studentId, r.getScore()));
        }
        return resultsMap;
    }

    /**
     * Returns a map of studentId -> remark for a given exam.
     */
    public Map<Integer, String> getExamRemarksForStudents(int examId, List<Map<String, Object>> studentsData) {
        Map<Integer, String> remarksMap = new HashMap<>();
        for (Map<String, Object> data : studentsData) {
            Integer studentId = (Integer) data.get("studentId");
            Optional<ExamResult> result = examResultDao.findByExamIdAndStudentId(examId, studentId);
            result.ifPresent(r -> {
                if (r.getRemark() != null) {
                    remarksMap.put(studentId, r.getRemark());
                }
            });
        }
        return remarksMap;
    }

    public void updateExamResult(int examId, int studentId, int score, String remark) {
        Optional<ExamResult> existing = examResultDao.findByExamIdAndStudentId(examId, studentId);
        if (existing.isPresent()) {
            ExamResult result = existing.get();
            result.setScore(score);
            result.setRemark(remark);
            examResultDao.update(result);
        } else {
            ExamResult result = new ExamResult(0, examId, studentId, score, null, remark);
            examResultDao.save(result);
        }
    }

    // Keep old signature for backward compat
    public void updateExamResult(int examId, int studentId, int score) {
        updateExamResult(examId, studentId, score, null);
    }

    public Optional<Attendance> getAttendance(int courseSessionId, int studentId) {
        return attendanceDao.findBySessionIdAndStudentId(courseSessionId, studentId);
    }

    public void updateAttendance(int courseSessionId, int studentId, String status) {
        Optional<Attendance> existing = attendanceDao.findBySessionIdAndStudentId(courseSessionId, studentId);
        if (existing.isPresent()) {
            Attendance attendance = existing.get();
            attendance.setStatus(status);
            attendanceDao.update(attendance);
        } else {
            Attendance attendance = new Attendance(0, courseSessionId, studentId, status, null);
            attendanceDao.save(attendance);
        }
    }

    public void updateProfile(User user, Teacher teacher, String email, String password) {
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        userDao.update(user);
        teacherDao.update(teacher);
    }

    public List<Exam> getExamsForCourse(int courseId) {
        return examDao.findByCourseId(courseId);
    }

    public void createExam(Exam exam) {
        examDao.save(exam);
    }

    public void deleteExam(int examId) {
        examDao.delete(examId);
    }

    public Optional<Exam> getExamById(int examId) {
        return examDao.findById(examId);
    }

    public List<CourseSession> getTodaySessions(int teacherId) {
        String today = java.time.LocalDate.now().toString();
        List<Course> courses = getTeacherCourses(teacherId);
        List<CourseSession> todaySessions = new ArrayList<>();
        for (Course c : courses) {
            todaySessions.addAll(courseSessionDao.findByCourseIdAndDate(c.getId(), today));
        }
        todaySessions.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
        return todaySessions;
    }

    /**
     * Returns all sessions for the teacher for the current week (Mon-Sun).
     */
    public List<CourseSession> getWeekSessions(int teacherId) {
        return getWeekSessions(teacherId, LocalDate.now());
    }

    public List<CourseSession> getWeekSessions(int teacherId, LocalDate targetDate) {
        LocalDate monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        List<Course> courses = getTeacherCourses(teacherId);
        Set<Integer> courseIds = new HashSet<>();
        for (Course c : courses) {
            courseIds.add(c.getId());
        }

        List<CourseSession> allWeekSessions = courseSessionDao.findByDateRange(monday.toString(), sunday.toString());
        List<CourseSession> teacherSessions = new ArrayList<>();
        for (CourseSession cs : allWeekSessions) {
            if (courseIds.contains(cs.getCourseId())) {
                teacherSessions.add(cs);
            }
        }
        teacherSessions.sort((a, b) -> {
            int dateComp = a.getSessionDate().compareTo(b.getSessionDate());
            return dateComp != 0 ? dateComp : a.getStartTime().compareTo(b.getStartTime());
        });
        return teacherSessions;
    }

    public Optional<Classroom> getClassroom(int classroomId) {
        return classroomDao.findById(classroomId);
    }

    public Optional<Course> getCourse(int courseId) {
        return courseDao.findById(courseId);
    }

    // ========== GROUP MANAGEMENT ==========

    public List<CourseGroup> getGroupsForCourse(int courseId) {
        return courseGroupDao.findByCourseId(courseId);
    }

    public Optional<CourseGroup> getGroupById(int groupId) {
        return courseGroupDao.findById(groupId);
    }

    public void createCourseGroup(CourseGroup group) {
        courseGroupDao.save(group);
    }

    public void updateCourseGroup(CourseGroup group) {
        courseGroupDao.update(group);
    }

    public void deleteCourseGroup(int groupId) {
        courseGroupDao.delete(groupId);
    }

    public List<StudentGroup> getStudentsInGroup(int courseGroupId) {
        return studentGroupDao.findByCourseGroupId(courseGroupId);
    }

    public void addStudentToGroup(int studentId, int courseGroupId) {
        StudentGroup sg = new StudentGroup();
        sg.setStudentId(studentId);
        sg.setCourseGroupId(courseGroupId);
        studentGroupDao.save(sg);
    }

    public void removeStudentFromGroup(int studentId, int courseGroupId) {
        studentGroupDao.delete(studentId, courseGroupId);
    }

    /**
     * Returns a map of courseGroupId -> count of students in that group.
     */
    public Map<Integer, Integer> getGroupStudentCounts(List<CourseGroup> groups) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (CourseGroup g : groups) {
            counts.put(g.getId(), studentGroupDao.findByCourseGroupId(g.getId()).size());
        }
        return counts;
    }

    // ========== ANALYTICS ==========

    /**
     * Returns the average exam score for a given course across all exams and students.
     */
    public double getAverageScoreForCourse(int courseId) {
        List<Exam> exams = examDao.findByCourseId(courseId);
        if (exams.isEmpty()) return -1;

        double totalScore = 0;
        int count = 0;
        for (Exam exam : exams) {
            List<ExamResult> results = examResultDao.findByExamId(exam.getId());
            for (ExamResult r : results) {
                totalScore += r.getScore();
                count++;
            }
        }
        return count > 0 ? totalScore / count : -1;
    }

    /**
     * Returns the attendance rate for a course as a percentage (0-100).
     * Only counts sessions that have already happened.
     */
    public double getAttendanceRateForCourse(int courseId) {
        List<CourseSession> sessions = courseSessionDao.findByCourseId(courseId);
        if (sessions.isEmpty()) return -1;

        List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);
        if (enrollments.isEmpty()) return -1;

        String today = LocalDate.now().toString();
        int totalExpected = 0;
        int totalPresent = 0;
        for (CourseSession session : sessions) {
            if (session.getSessionDate().compareTo(today) > 0) continue; // skip future
            for (Enrollment e : enrollments) {
                totalExpected++;
                Optional<Attendance> att = attendanceDao.findBySessionIdAndStudentId(session.getId(), e.getStudentId());
                if (att.isPresent() && "PRESENT".equalsIgnoreCase(att.get().getStatus())) {
                    totalPresent++;
                }
            }
        }
        return totalExpected > 0 ? (totalPresent * 100.0 / totalExpected) : -1;
    }
    // ========== ATTENDANCE OVERVIEW ==========

    /**
     * Returns all sessions for a given course, sorted by date asc.
     */
    public List<CourseSession> getCourseSessions(int courseId) {
        List<CourseSession> sessions = courseSessionDao.findByCourseId(courseId);
        sessions.sort((a, b) -> a.getSessionDate().compareTo(b.getSessionDate()));
        return sessions;
    }

    /**
     * Returns a map of studentId(String) -> (map of sessionId(String) -> status) for the attendance overview.
     * Uses String keys because JSTL EL coerces numeric keys to Long, breaking Integer-keyed lookups.
     */
    public Map<String, Map<String, String>> getAttendanceMatrix(int courseId) {
        List<CourseSession> sessions = getCourseSessions(courseId);
        List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);
        Map<String, Map<String, String>> matrix = new LinkedHashMap<>();

        for (Enrollment e : enrollments) {
            Map<String, String> row = new LinkedHashMap<>();
            for (CourseSession cs : sessions) {
                Optional<Attendance> att = attendanceDao.findBySessionIdAndStudentId(cs.getId(), e.getStudentId());
                row.put(String.valueOf(cs.getId()), att.map(Attendance::getStatus).orElse(null));
            }
            matrix.put(String.valueOf(e.getStudentId()), row);
        }
        return matrix;
    }

    /**
     * Returns the attendance rate for a specific student in a specific course.
     * Only counts sessions that have already happened.
     */
    public double getStudentAttendanceRate(int studentId, int courseId) {
        List<CourseSession> sessions = getCourseSessions(courseId);
        if (sessions.isEmpty()) return -1;

        String today = LocalDate.now().toString();
        int attended = 0;
        int pastSessionCount = 0;
        for (CourseSession cs : sessions) {
            if (cs.getSessionDate().compareTo(today) > 0) continue; // skip future
            pastSessionCount++;
            Optional<Attendance> att = attendanceDao.findBySessionIdAndStudentId(cs.getId(), studentId);
            if (att.isPresent() && "Present".equalsIgnoreCase(att.get().getStatus())) {
                attended++;
            }
        }
        return pastSessionCount > 0 ? (attended * 100.0 / pastSessionCount) : -1;
    }

    /**
     * Returns sessions for a course that have not yet had attendance fully recorded.
     * A session is considered "unattended" if at least one enrolled student has no record.
     */
    public List<CourseSession> getUnattendedSessions(int courseId) {
        List<CourseSession> sessions = getCourseSessions(courseId);
        List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);
        List<CourseSession> unattended = new ArrayList<>();

        for (CourseSession cs : sessions) {
            boolean incomplete = false;
            for (Enrollment e : enrollments) {
                Optional<Attendance> att = attendanceDao.findBySessionIdAndStudentId(cs.getId(), e.getStudentId());
                if (att.isEmpty()) {
                    incomplete = true;
                    break;
                }
            }
            if (incomplete) {
                unattended.add(cs);
            }
        }
        return unattended;
    }

    public List<Map<String, Object>> getCourseRanking(int courseId) {
        List<Map<String, Object>> students = getCourseStudentsData(courseId, null);
        for (Map<String, Object> data : students) {
            int studentId = (Integer) data.get("studentId");
            double avg = calculateCourseAverage(studentId, courseId);
            data.put("average", avg);
        }
        
        students.sort((a, b) -> {
            Double avgA = (Double) a.get("average");
            Double avgB = (Double) b.get("average");
            return avgB.compareTo(avgA); // descending
        });
        
        int rank = 1;
        for (Map<String, Object> data : students) {
            data.put("rank", rank++);
        }
        return students;
    }

    public double calculateCourseAverage(int studentId, int courseId) {
        List<ExamResult> results = examResultDao.findByStudentId(studentId);
        
        double midtermSum = 0; double midtermMax = 0;
        double finalSum = 0; double finalMax = 0;
        double otherGradeSum = 0; int otherCount = 0;
        
        for (ExamResult r : results) {
            Optional<Exam> examOpt = examDao.findById(r.getExamId());
            if (examOpt.isPresent() && examOpt.get().getCourseId() == courseId) {
                Exam exam = examOpt.get();
                String type = exam.getExamType() != null ? exam.getExamType() : "Other";
                
                if (type.equalsIgnoreCase("Midterm")) {
                    midtermSum += r.getScore();
                    midtermMax += exam.getMaxScore();
                } else if (type.equalsIgnoreCase("Final")) {
                    finalSum += r.getScore();
                    finalMax += exam.getMaxScore();
                } else {
                    if (exam.getMaxScore() > 0) {
                        otherGradeSum += ((double)r.getScore() / exam.getMaxScore());
                        otherCount++;
                    }
                }
            }
        }
        
        double totalWeight = 0;
        double weightedScoreSum = 0;
        if (midtermMax > 0) {
            totalWeight += 0.4;
            weightedScoreSum += (midtermSum / midtermMax) * 20 * 0.4;
        }
        if (finalMax > 0) {
            totalWeight += 0.4;
            weightedScoreSum += (finalSum / finalMax) * 20 * 0.4;
        }
        if (otherCount > 0) {
            totalWeight += 0.2;
            double avgPercent = otherGradeSum / otherCount;
            weightedScoreSum += avgPercent * 20 * 0.2;
        }
        
        if (totalWeight > 0) {
            return Math.round((weightedScoreSum / totalWeight) * 100.0) / 100.0;
        }
        return -1.0;
    }
}
