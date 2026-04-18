package com.example.school.web.handler.student;

import com.example.school.model.Classroom;
import com.example.school.model.Course;
import com.example.school.model.CourseSession;
import com.example.school.model.Student;
import com.example.school.model.User;
import com.example.school.service.ServiceFactory;
import com.example.school.service.StudentService;
import com.example.school.service.UserService;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentDashboardHandler implements ActionHandler {

    private final StudentService studentService = ServiceFactory.getStudentService();
    private final UserService userService = ServiceFactory.getUserService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Student student = (Student) req.getAttribute("student");
        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/dashboard") || path.equals("/")) {
            showDashboard(req, resp, student);
        } else if (path.equals("/profile")) {
            showProfile(req, resp);
        }
    }

    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Student student = (Student) req.getAttribute("student");
        String path = req.getPathInfo();
        
        if ("/profile".equals(path)) {
            handleProfileUpdate(req, resp, student);
        }
    }

    private void handleProfileUpdate(HttpServletRequest req, HttpServletResponse resp, Student student) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");

        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Email cannot be empty");
            showProfile(req, resp);
            return;
        }

        Optional<User> userOpt = userService.findById(student.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (phone != null) user.setPhone(phone);
            userService.updateProfile(user, email, password);

            req.getSession().setAttribute("user", user);
            req.setAttribute("success", "Profile updated successfully");
        } else {
            req.setAttribute("error", "User not found");
        }
        showProfile(req, resp);
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp, Student student) throws ServletException, IOException {
        req.setAttribute("courseCount", studentService.getCourseCount(student.getId()));

        double gpa = studentService.calculateGPA(student.getId());
        double overallAvg = studentService.calculateOverallAverageOver20(student.getId());
        req.setAttribute("gpa", String.format("%.2f", gpa));
        req.setAttribute("overallAvg", overallAvg >= 0 ? String.format("%.2f", overallAvg) : "N/A");

        double attendanceRate = studentService.getAttendanceRate(student.getId());
        req.setAttribute("attendanceRate", String.format("%.0f", attendanceRate));

        LocalDate targetDate = LocalDate.now();
        String targetDateParam = req.getParameter("targetDate");
        if (targetDateParam != null && !targetDateParam.isEmpty()) {
            try { targetDate = LocalDate.parse(targetDateParam); } catch (Exception ignored) {}
        }
        req.setAttribute("targetDate", targetDateParam);

        List<CourseSession> todaySessions = studentService.getTodaySessions(student.getId());
        req.setAttribute("todaySessions", todaySessions);

        List<CourseSession> weekSessions = studentService.getWeekSessions(student.getId(), targetDate);
        req.setAttribute("weekSessions", weekSessions);

        Map<Integer, Course> courseMap = new HashMap<>();
        List<Course> courses = studentService.getEnrolledCourses(student.getId());
        for (Course c : courses) courseMap.put(c.getId(), c);
        req.setAttribute("courseMap", courseMap);

        Map<Integer, Classroom> classroomMap = new HashMap<>();
        List<CourseSession> allSessions = new java.util.ArrayList<>(todaySessions);
        allSessions.addAll(weekSessions);
        for (CourseSession cs : allSessions) {
            if (!classroomMap.containsKey(cs.getClassroomId())) {
                studentService.getClassroom(cs.getClassroomId()).ifPresent(cr -> classroomMap.put(cr.getId(), cr));
            }
        }
        req.setAttribute("classroomMap", classroomMap);

        LocalDate today = LocalDate.now();
        LocalDate monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<String> weekDates = new java.util.ArrayList<>();
        List<String> weekDayNames = new java.util.ArrayList<>();
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            weekDates.add(d.toString());
            weekDayNames.add(dayNames[i] + " " + d.getDayOfMonth() + "/" + d.getMonthValue());
        }
        req.setAttribute("weekDates", weekDates);
        req.setAttribute("weekDayNames", weekDayNames);
        req.setAttribute("todayDate", today.toString());

        req.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(req, resp);
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);
    }
}
