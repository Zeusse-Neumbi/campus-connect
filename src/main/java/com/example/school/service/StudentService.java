package com.example.school.service;

import com.example.school.dao.*;
import com.example.school.model.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class StudentService {
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final EnrollmentDao enrollmentDao;
    private final ExamDao examDao;
    private final ExamResultDao examResultDao;
    private final AttendanceDao attendanceDao;
    private final TeacherDao teacherDao;
    private final UserDao userDao;
    private final CourseSessionDao courseSessionDao;
    private final ClassroomDao classroomDao;
    private final CourseGroupDao courseGroupDao;
    private final StudentGroupDao studentGroupDao;

    public StudentService(StudentDao studentDao, CourseDao courseDao,
            EnrollmentDao enrollmentDao, ExamDao examDao, ExamResultDao examResultDao,
            AttendanceDao attendanceDao, TeacherDao teacherDao, UserDao userDao,
            CourseSessionDao courseSessionDao, ClassroomDao classroomDao,
            CourseGroupDao courseGroupDao, StudentGroupDao studentGroupDao) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.enrollmentDao = enrollmentDao;
        this.examDao = examDao;
        this.examResultDao = examResultDao;
        this.attendanceDao = attendanceDao;
        this.teacherDao = teacherDao;
        this.userDao = userDao;
        this.courseSessionDao = courseSessionDao;
        this.classroomDao = classroomDao;
        this.courseGroupDao = courseGroupDao;
        this.studentGroupDao = studentGroupDao;
    }

    public Optional<Student> getStudentByUserId(int userId) {
        return studentDao.findByUserId(userId);
    }

    public int getCourseCount(int studentId) {
        return courseDao.findByStudentId(studentId).size();
    }

    public double calculateGPA(int studentId) {
        List<ExamResult> results = examResultDao.findByStudentId(studentId);
        if (results.isEmpty()) return 0.0;

        double totalScore = 0;
        for (ExamResult r : results) {
            totalScore += r.getScore();
        }
        return totalScore / results.size();
    }

    public double calculateOverallAverageOver20(int studentId) {
        List<Map<String, Object>> matrix = getGradesMatrix(studentId);
        if (matrix.isEmpty()) return -1.0;
        
        double totalAverages = 0;
        int courseWithGradesCount = 0;
        
        for (Map<String, Object> map : matrix) {
            Double avg = (Double) map.get("average");
            if (avg != null && avg >= 0) {
                totalAverages += avg;
                courseWithGradesCount++;
            }
        }
        
        if (courseWithGradesCount == 0) return -1.0;
        
        double overall = totalAverages / courseWithGradesCount;
        return Math.round(overall * 100.0) / 100.0;
    }

    public double getAttendanceRate(int studentId) {
        return attendanceDao.getAttendanceRate(studentId);
    }

    public List<Course> getEnrolledCourses(int studentId) {
        return courseDao.findByStudentId(studentId);
    }

    public int getEnrolledStudentCount(int courseId) {
        return enrollmentDao.findByCourseId(courseId).size();
    }

    public List<Course> getAvailableCourses(int studentId) {
        List<Course> allCourses = courseDao.findAll();
        List<Enrollment> enrollments = enrollmentDao.findByStudentId(studentId);
        Set<Integer> enrolledIds = new HashSet<>();
        for (Enrollment e : enrollments)
            enrolledIds.add(e.getCourseId());

        List<Course> available = new ArrayList<>();
        for (Course c : allCourses) {
            if (!enrolledIds.contains(c.getId())) {
                available.add(c);
            }
        }
        return available;
    }

    public void enroll(int studentId, int courseId) {
        if (!enrollmentDao.isEnrolled(studentId, courseId)) {
            Enrollment enrollment = new Enrollment(0, studentId, courseId, java.time.LocalDate.now().toString());
            enrollmentDao.save(enrollment);
        }
    }

    public void unenroll(int studentId, int courseId) {
        enrollmentDao.delete(studentId, courseId);
    }

    public List<ExamResult> getExamResults(int studentId) {
        return examResultDao.findByStudentId(studentId);
    }

    public List<Attendance> getAttendance(int studentId) {
        return attendanceDao.findByStudentId(studentId);
    }

    public Course getCourse(int courseId) {
        return courseDao.findById(courseId).orElse(null);
    }

    public String getTeacherName(int courseId) {
        Optional<Course> courseOpt = courseDao.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            Optional<Teacher> teacherOpt = teacherDao.findById(course.getTeacherId());
            if (teacherOpt.isPresent()) {
                Optional<User> userOpt = userDao.findById(teacherOpt.get().getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    return user.getFirstName() + " " + user.getLastName();
                }
            }
        }
        return "Unknown";
    }

    public java.util.List<Enrollment> getEnrollments(int studentId) {
        return enrollmentDao.findByStudentId(studentId);
    }

    public Optional<Exam> getExam(int examId) {
        return examDao.findById(examId);
    }

    public List<Exam> getExamsForCourse(int courseId) {
        return examDao.findByCourseId(courseId);
    }

    public Optional<CourseSession> getCourseSession(int sessionId) {
        return courseSessionDao.findById(sessionId);
    }

    public List<CourseSession> getTodaySessions(int studentId) {
        String today = java.time.LocalDate.now().toString();
        List<Course> courses = getEnrolledCourses(studentId);
        List<CourseSession> todaySessions = new ArrayList<>();
        for (Course c : courses) {
            todaySessions.addAll(courseSessionDao.findByCourseIdAndDate(c.getId(), today));
        }
        todaySessions.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
        return todaySessions;
    }

    public List<CourseSession> getWeekSessions(int studentId) {
        return getWeekSessions(studentId, LocalDate.now());
    }

    public List<CourseSession> getWeekSessions(int studentId, LocalDate targetDate) {
        LocalDate monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        List<Course> courses = getEnrolledCourses(studentId);
        Set<Integer> courseIds = new HashSet<>();
        for (Course c : courses) {
            courseIds.add(c.getId());
        }

        List<CourseSession> allWeekSessions = courseSessionDao.findByDateRange(monday.toString(), sunday.toString());
        List<CourseSession> studentSessions = new ArrayList<>();
        for (CourseSession cs : allWeekSessions) {
            if (courseIds.contains(cs.getCourseId())) {
                studentSessions.add(cs);
            }
        }
        studentSessions.sort((a, b) -> {
            int dateComp = a.getSessionDate().compareTo(b.getSessionDate());
            return dateComp != 0 ? dateComp : a.getStartTime().compareTo(b.getStartTime());
        });
        return studentSessions;
    }

    public Optional<Classroom> getClassroom(int classroomId) {
        return classroomDao.findById(classroomId);
    }

    public List<Map<String, Object>> getGradesMatrix(int studentId) {
        List<Course> courses = getEnrolledCourses(studentId);
        List<Map<String, Object>> matrix = new ArrayList<>();

        for (Course course : courses) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("course", course);

            List<ExamResult> results = examResultDao.findByStudentId(studentId);
            Map<String, Map<String, Object>> examResultsForCourse = new HashMap<>();

            double midtermSum = 0; double midtermMax = 0;
            double finalSum = 0; double finalMax = 0;
            double otherGradeSum = 0; int otherCount = 0;

            for (ExamResult r : results) {
                Optional<Exam> examOpt = examDao.findById(r.getExamId());
                if (examOpt.isPresent() && examOpt.get().getCourseId() == course.getId()) {
                    Exam exam = examOpt.get();
                    String type = exam.getExamType() != null ? exam.getExamType() : "Other";
                    
                    if (!examResultsForCourse.containsKey(type)) {
                        Map<String, Object> examData = new HashMap<>();
                        examData.put("score", (double)r.getScore());
                        examData.put("maxScore", exam.getMaxScore());
                        examResultsForCourse.put(type, examData);
                    } else {
                        // Aggregate if multiple exams of same type
                        Map<String, Object> existing = examResultsForCourse.get(type);
                        double currentScore = ((Number) existing.get("score")).doubleValue();
                        double currentMax = ((Number) existing.get("maxScore")).doubleValue();
                        existing.put("score", currentScore + r.getScore());
                        existing.put("maxScore", currentMax + exam.getMaxScore());
                    }

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

            courseData.put("exams", examResultsForCourse);

            // Calculate Over 20 based on weights: Midterm 40%, Final 40%, Other 20%
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

            double averageOver20 = -1;
            if (totalWeight > 0) {
                averageOver20 = weightedScoreSum / totalWeight;
                // Round to 2 decimal places
                averageOver20 = Math.round(averageOver20 * 100.0) / 100.0;
            }

            courseData.put("average", averageOver20);
            matrix.add(courseData);
        }
        return matrix;
    }

    public List<Map<String, Object>> getAttendanceMatrix(int studentId) {
        List<Course> courses = getEnrolledCourses(studentId);
        List<Map<String, Object>> matrix = new ArrayList<>();

        for (Course course : courses) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("course", course);

            List<CourseSession> sessions = courseSessionDao.findByCourseId(course.getId());
            sessions.sort((a, b) -> a.getSessionDate().compareTo(b.getSessionDate()));
            courseData.put("sessions", sessions);

            Map<String, String> statusMap = new HashMap<>();
            int attended = 0;
            int pastSessionCount = 0;
            String today = LocalDate.now().toString();
            for (CourseSession cs : sessions) {
                Optional<Attendance> att = attendanceDao.findBySessionIdAndStudentId(cs.getId(), studentId);
                if (att.isPresent()) {
                    String status = att.get().getStatus();
                    statusMap.put(String.valueOf(cs.getId()), status);
                    if ("Present".equalsIgnoreCase(status)) {
                        attended++;
                    }
                } else {
                    statusMap.put(String.valueOf(cs.getId()), "NONE");
                }
                // Only count sessions that already happened for rate
                if (cs.getSessionDate().compareTo(today) <= 0) {
                    pastSessionCount++;
                }
            }
            courseData.put("statuses", statusMap);
            double rate = pastSessionCount <= 0 ? -1 : (attended * 100.0 / pastSessionCount);
            courseData.put("rate", rate);

            matrix.add(courseData);
        }
        return matrix;
    }

    public List<Map<String, Object>> getStudentGroupsData(int studentId) {
        List<StudentGroup> myGroups = studentGroupDao.findByStudentId(studentId);
        Set<Integer> myGroupIds = new HashSet<>();
        for (StudentGroup sg : myGroups) {
            myGroupIds.add(sg.getCourseGroupId());
        }

        // We also want groups of courses the student is enrolled in, even if not a member.
        List<Course> enrolledCourses = getEnrolledCourses(studentId);
        List<CourseGroup> allRelevantGroups = new ArrayList<>();
        for (Course c : enrolledCourses) {
            allRelevantGroups.addAll(courseGroupDao.findByCourseId(c.getId()));
        }

        List<Map<String, Object>> groupDataList = new ArrayList<>();

        for (CourseGroup group : allRelevantGroups) {
            Map<String, Object> map = new HashMap<>();
            map.put("group", group);
            Optional<Course> courseOpt = courseDao.findById(group.getCourseId());
            courseOpt.ifPresent(course -> map.put("course", course));

            map.put("isMember", myGroupIds.contains(group.getId()));

            List<StudentGroup> memberships = studentGroupDao.findByCourseGroupId(group.getId());
            List<Map<String, Object>> members = new ArrayList<>();

            for (StudentGroup membership : memberships) {
                Optional<Student> sOpt = studentDao.findById(membership.getStudentId());
                if (sOpt.isPresent()) {
                    Student s = sOpt.get();
                    Optional<User> uOpt = userDao.findById(s.getUserId());
                    if (uOpt.isPresent()) {
                        User u = uOpt.get();
                        Map<String, Object> memberData = new HashMap<>();
                        memberData.put("student", s);
                        memberData.put("user", u);
                        members.add(memberData);
                    }
                }
            }
            map.put("members", members);
            map.put("memberCount", members.size());

            groupDataList.add(map);
        }

        return groupDataList;
    }

    public List<Map<String, Object>> getStudentGroupsDataForCourse(int studentId, int courseId) {
        List<StudentGroup> myGroups = studentGroupDao.findByStudentId(studentId);
        Set<Integer> myGroupIds = new HashSet<>();
        for (StudentGroup sg : myGroups) {
            myGroupIds.add(sg.getCourseGroupId());
        }

        List<CourseGroup> courseGroups = courseGroupDao.findByCourseId(courseId);
        List<Map<String, Object>> groupDataList = new ArrayList<>();

        for (CourseGroup group : courseGroups) {
            Map<String, Object> map = new HashMap<>();
            map.put("group", group);
            Optional<Course> courseOpt = courseDao.findById(group.getCourseId());
            courseOpt.ifPresent(course -> map.put("course", course));

            map.put("isMember", myGroupIds.contains(group.getId()));

            List<StudentGroup> memberships = studentGroupDao.findByCourseGroupId(group.getId());
            List<Map<String, Object>> members = new ArrayList<>();

            for (StudentGroup membership : memberships) {
                Optional<Student> sOpt = studentDao.findById(membership.getStudentId());
                if (sOpt.isPresent()) {
                    Student s = sOpt.get();
                    Optional<User> uOpt = userDao.findById(s.getUserId());
                    if (uOpt.isPresent()) {
                        User u = uOpt.get();
                        Map<String, Object> memberData = new HashMap<>();
                        memberData.put("student", s);
                        memberData.put("user", u);
                        members.add(memberData);
                    }
                }
            }
            map.put("members", members);
            map.put("memberCount", members.size());

            groupDataList.add(map);
        }

        return groupDataList;
    }

    public int getCourseRank(int studentId, int courseId) {
        List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId);
        if (enrollments == null || enrollments.isEmpty()) return 1;

        List<Double> averages = new ArrayList<>();
        double studentAvg = -1;

        for (Enrollment e : enrollments) {
            double avg = calculateCourseAverage(e.getStudentId(), courseId);
            averages.add(avg);
            if (e.getStudentId() == studentId) {
                studentAvg = avg;
            }
        }

        if (studentAvg < 0) return enrollments.size();

        averages.sort(Collections.reverseOrder());
        
        int rank = 1;
        for (Double avg : averages) {
            if (avg > studentAvg) {
                rank++;
            } else if (avg.equals(studentAvg)) {
                break;
            }
        }
        return rank;
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
        return -1;
    }
}
