package com.example.school.web.handler.teacher;

import com.example.school.model.Classroom;
import com.example.school.model.Course;
import com.example.school.model.CourseSession;
import com.example.school.model.Teacher;
import com.example.school.service.ServiceFactory;
import com.example.school.service.TeacherService;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class TeacherDashboardHandler implements ActionHandler {
    private final TeacherService teacherService = ServiceFactory.getTeacherService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Teacher teacher = (Teacher) req.getAttribute("teacher");
        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/dashboard") || path.equals("/")) {
            showDashboard(req, resp, teacher);
        } else if (path.equals("/profile")) {
            showProfile(req, resp);
        }
    }

    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/profile".equals(path)) {
            // Profile update logic if needed
            resp.sendRedirect(req.getContextPath() + "/teacher/profile");
        }
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp, Teacher teacher) throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courseCount", courses.size());
        req.setAttribute("studentCount", teacherService.getStudentCountForTeacher(teacher.getId()));

        List<CourseSession> todaySessions = teacherService.getTodaySessions(teacher.getId());
        req.setAttribute("todaySessions", todaySessions);

        LocalDate targetDate = LocalDate.now();
        String targetDateParam = req.getParameter("targetDate");
        if (targetDateParam != null && !targetDateParam.isEmpty()) {
            try { targetDate = LocalDate.parse(targetDateParam); } catch (Exception ignored) {}
        }
        req.setAttribute("targetDate", targetDateParam);

        List<CourseSession> weekSessions = teacherService.getWeekSessions(teacher.getId(), targetDate);
        req.setAttribute("weekSessions", weekSessions);

        java.util.Map<Integer, Course> courseMap = new java.util.HashMap<>();
        for (Course c : courses) courseMap.put(c.getId(), c);
        req.setAttribute("courseMap", courseMap);

        java.util.Map<Integer, Classroom> classroomMap = new java.util.HashMap<>();
        java.util.List<CourseSession> allSessions = new java.util.ArrayList<>(todaySessions);
        allSessions.addAll(weekSessions);
        for (CourseSession cs : allSessions) {
            if (!classroomMap.containsKey(cs.getClassroomId())) {
                teacherService.getClassroom(cs.getClassroomId()).ifPresent(cr -> classroomMap.put(cr.getId(), cr));
            }
        }
        req.setAttribute("classroomMap", classroomMap);

        LocalDate today = LocalDate.now();
        LocalDate monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        java.util.List<String> weekDates = new java.util.ArrayList<>();
        java.util.List<String> weekDayNames = new java.util.ArrayList<>();
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            weekDates.add(d.toString());
            weekDayNames.add(dayNames[i] + " " + d.getDayOfMonth() + "/" + d.getMonthValue());
        }
        req.setAttribute("weekDates", weekDates);
        req.setAttribute("weekDayNames", weekDayNames);
        req.setAttribute("todayDate", today.toString());

        req.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(req, resp);
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/teacher/profile.jsp").forward(req, resp);
    }
}
