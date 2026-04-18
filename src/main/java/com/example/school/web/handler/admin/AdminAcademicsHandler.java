package com.example.school.web.handler.admin;

import com.example.school.model.*;
import com.example.school.service.AdminService;
import com.example.school.service.ServiceFactory;
import com.example.school.util.ParseUtil;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

public class AdminAcademicsHandler implements ActionHandler {
    private final AdminService adminService = ServiceFactory.getAdminService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/courses": showCourses(req, resp); break;
            case "/classrooms": showClassrooms(req, resp); break;
            case "/course-groups": showCourseGroups(req, resp); break;
            case "/sessions": showCourseSessions(req, resp); break;
            default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String action = req.getParameter("action");

        switch (path) {
            case "/courses": handleCourseAction(req, resp, action); break;
            case "/classrooms": handleClassroomAction(req, resp, action); break;
            case "/course-groups": handleCourseGroupAction(req, resp, action); break;
            case "/sessions": handleSessionAction(req, resp, action); break;
            default: resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // --- GET DATA PROVIDERS ---

    private void showCourses(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        int page = ParseUtil.parseOptionalInt(req.getParameter("page"), 1);
        int pageSize = 50;

        List<Course> courses = adminService.searchCourses(search, page, pageSize);
        int totalCourses = adminService.countCourses(search);
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

        java.util.Map<Integer, Teacher> teacherMap = new java.util.HashMap<>();
        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (Course c : courses) {
             adminService.getTeacherById(c.getTeacherId()).ifPresent(t -> {
                 teacherMap.put(t.getId(), t);
                 adminService.getUserById(t.getUserId()).ifPresent(u -> userMap.put(u.getId(), u));
             });
        }
        
        req.setAttribute("teachers", adminService.getAllTeachers());
        req.setAttribute("teacherMap", teacherMap);
        req.setAttribute("userMap", userMap);
        req.setAttribute("courses", courses);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/courses.jsp").forward(req, resp);
    }

    private void showClassrooms(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        String[] buildings = req.getParameterValues("buildings");
        Integer minCapacity = null;
        if (req.getParameter("minCapacity") != null && !req.getParameter("minCapacity").isEmpty()) {
            minCapacity = ParseUtil.parseOptionalInt(req.getParameter("minCapacity"), 0);
        }
        
        int page = ParseUtil.parseOptionalInt(req.getParameter("page"), 1);
        int pageSize = 50;
        List<String> buildingList = buildings != null ? Arrays.asList(buildings) : null;
        
        List<Classroom> classrooms = adminService.searchClassrooms(search, buildingList, minCapacity, page, pageSize);
        int totalClassrooms = adminService.countClassrooms(search, buildingList, minCapacity);
        int totalPages = (int) Math.ceil((double) totalClassrooms / pageSize);

        req.setAttribute("classrooms", classrooms);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/classrooms.jsp").forward(req, resp);
    }

    private void showCourseGroups(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        int page = ParseUtil.parseOptionalInt(req.getParameter("page"), 1);
        int pageSize = 50;

        List<CourseGroup> courseGroups = adminService.searchCourseGroups(search, page, pageSize);
        int totalGroups = adminService.countCourseGroups(search);
        int totalPages = (int) Math.ceil((double) totalGroups / pageSize);

        java.util.Map<Integer, Course> courseMap = new java.util.HashMap<>();
        for (CourseGroup cg : courseGroups) {
             adminService.getAllCourses().stream()
                 .filter(c -> c.getId() == cg.getCourseId())
                 .findFirst().ifPresent(c -> courseMap.put(c.getId(), c));
        }

        req.setAttribute("courseGroups", courseGroups);
        req.setAttribute("courses", adminService.getAllCourses());
        req.setAttribute("courseMap", courseMap);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/course_groups.jsp").forward(req, resp);
    }

    private void showCourseSessions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String targetDateStr = req.getParameter("targetDate");
        List<CourseSession> sessions;
        java.time.LocalDate targetDate;

        try {
            if (targetDateStr != null && !targetDateStr.isEmpty()) {
                targetDate = java.time.LocalDate.parse(targetDateStr);
            } else {
                targetDate = java.time.LocalDate.now();
            }
        } catch (Exception e) {
            targetDate = java.time.LocalDate.now();
        }

        java.time.LocalDate startOfWeek = targetDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        java.time.LocalDate endOfWeek = startOfWeek.plusDays(6);
        sessions = adminService.getCourseSessionsByDateRange(startOfWeek.toString(), endOfWeek.toString());

        req.setAttribute("sessions", sessions);
        req.setAttribute("courses", adminService.getAllCourses());
        req.setAttribute("classrooms", adminService.getAllClassrooms());
        req.setAttribute("courseGroups", adminService.getAllCourseGroups());

        java.util.Map<Integer, Course> courseMap = new java.util.HashMap<>();
        java.util.Map<Integer, Classroom> classroomMap = new java.util.HashMap<>();
        java.util.Map<Integer, CourseGroup> groupMap = new java.util.HashMap<>();

        for (CourseSession s : sessions) {
             adminService.getAllCourses().stream().filter(c -> c.getId() == s.getCourseId()).findFirst().ifPresent(c -> courseMap.put(c.getId(), c));
             adminService.getClassroomById(s.getClassroomId()).ifPresent(cr -> classroomMap.put(cr.getId(), cr));
             if (s.getCourseGroupId() != null && s.getCourseGroupId() > 0) {
                 adminService.getAllCourseGroups().stream().filter(cg -> cg.getId() == s.getCourseGroupId()).findFirst().ifPresent(cg -> groupMap.put(cg.getId(), cg));
             }
        }

        req.setAttribute("courseMap", courseMap);
        req.setAttribute("classroomMap", classroomMap);
        req.setAttribute("groupMap", groupMap);
        req.setAttribute("targetDate", targetDateStr);

        List<String> weekDates = new java.util.ArrayList<>();
        List<String> weekDayNames = new java.util.ArrayList<>();
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            java.time.LocalDate d = startOfWeek.plusDays(i);
            weekDates.add(d.toString());
            weekDayNames.add(dayNames[i] + " " + d.getDayOfMonth() + "/" + d.getMonthValue());
        }
        req.setAttribute("weekDates", weekDates);
        req.setAttribute("weekDayNames", weekDayNames);
        req.setAttribute("todayDate", java.time.LocalDate.now().toString());

        req.getRequestDispatcher("/WEB-INF/views/admin/course_sessions.jsp").forward(req, resp);
    }

    // --- POST MUTATIONS ---

    private void handleCourseAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("create".equals(action)) {
            Course course = new Course(0, req.getParameter("courseCode"), req.getParameter("courseName"),
                    req.getParameter("description"), ParseUtil.parseOptionalInt(req.getParameter("credits"), 0),
                    ParseUtil.parseOptionalInt(req.getParameter("teacherId"), 0));
            adminService.createCourse(course);
        } else if ("update".equals(action)) {
            Course course = new Course(ParseUtil.parseOptionalInt(req.getParameter("id"), 0), req.getParameter("courseCode"),
                    req.getParameter("courseName"), req.getParameter("description"),
                    ParseUtil.parseOptionalInt(req.getParameter("credits"), 0), ParseUtil.parseOptionalInt(req.getParameter("teacherId"), 0));
            adminService.updateCourse(course);
        } else if ("delete".equals(action)) {
            adminService.deleteCourse(ParseUtil.parseOptionalInt(req.getParameter("id"), 0));
        }
        resp.sendRedirect(req.getContextPath() + "/admin/courses");
    }

    private void handleClassroomAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("create".equals(action)) {
            String[] buildings = req.getParameterValues("building");
            String buildingStr = buildings != null ? String.join(", ", buildings) : "";
            Classroom classroom = new Classroom(0, req.getParameter("roomCode"), buildingStr, ParseUtil.parseOptionalInt(req.getParameter("capacity"), 0));
            adminService.createClassroom(classroom);
        } else if ("update".equals(action)) {
            String[] buildings = req.getParameterValues("building");
            String buildingStr = buildings != null ? String.join(", ", buildings) : "";
            Classroom classroom = new Classroom(ParseUtil.parseOptionalInt(req.getParameter("id"), 0), req.getParameter("roomCode"),
                    buildingStr, ParseUtil.parseOptionalInt(req.getParameter("capacity"), 0));
            adminService.updateClassroom(classroom);
        } else if ("delete".equals(action)) {
            adminService.deleteClassroom(ParseUtil.parseOptionalInt(req.getParameter("id"), 0));
        }
        resp.sendRedirect(req.getContextPath() + "/admin/classrooms");
    }

    private void handleCourseGroupAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        int groupId = ParseUtil.parseOptionalInt(req.getParameter("groupId"), 0);
        if ("create".equals(action)) {
            CourseGroup group = new CourseGroup(0, ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0), req.getParameter("groupName"), ParseUtil.parseOptionalInt(req.getParameter("capacity"), 0));
            adminService.createCourseGroup(group);
        } else if ("update".equals(action)) {
            CourseGroup group = new CourseGroup(groupId, ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0), req.getParameter("groupName"), ParseUtil.parseOptionalInt(req.getParameter("capacity"), 0));
            adminService.updateCourseGroup(group);
        } else if ("delete".equals(action)) {
            adminService.deleteCourseGroup(groupId);
        } else if ("assignData".equals(action)) {
            adminService.deleteCourseGroup(ParseUtil.parseOptionalInt(req.getParameter("id"), 0));
        }
        resp.sendRedirect(req.getContextPath() + "/admin/course-groups");
    }

    private void handleSessionAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("create".equals(action) || "update".equals(action)) {
            String groupIdStr = req.getParameter("courseGroupId");
            Integer courseGroupId = (groupIdStr != null && !groupIdStr.isEmpty()) ? Integer.parseInt(groupIdStr) : null;
            CourseSession session = new CourseSession(
                    "update".equals(action) ? ParseUtil.parseOptionalInt(req.getParameter("id"), 0) : 0,
                    ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0),
                    courseGroupId, ParseUtil.parseOptionalInt(req.getParameter("classroomId"), 0),
                    req.getParameter("sessionDate"), req.getParameter("startTime"), req.getParameter("endTime"));
            
            if ("create".equals(action)) adminService.createCourseSession(session);
            else adminService.updateCourseSession(session);
        } else if ("delete".equals(action)) {
            adminService.deleteCourseSession(ParseUtil.parseOptionalInt(req.getParameter("id"), 0));
        }
        resp.sendRedirect(req.getContextPath() + "/admin/sessions");
    }
}
