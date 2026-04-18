package com.example.school.web.handler.parent;

import com.example.school.model.*;
import com.example.school.service.ParentService;
import com.example.school.service.ServiceFactory;
import com.example.school.service.StudentService;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParentAcademicsHandler implements ActionHandler {

    private final ParentService parentService = ServiceFactory.getParentService();
    private final StudentService studentService = ServiceFactory.getStudentService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Parent parent = (Parent) req.getAttribute("parent");
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/children": showChildren(req, resp, parent); break;
            case "/child-grades": showChildGrades(req, resp, parent); break;
            case "/child-attendance": showChildAttendance(req, resp, parent); break;
            case "/child-schedule": showChildSchedule(req, resp, parent); break;
            case "/child-transcript": showChildTranscript(req, resp, parent); break;
            default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showChildren(HttpServletRequest req, HttpServletResponse resp, Parent parent) throws ServletException, IOException {
        List<Map<String, Object>> children = parentService.getLinkedStudents(parent.getId());
        req.setAttribute("children", children);
        req.getRequestDispatcher("/WEB-INF/views/parent/children.jsp").forward(req, resp);
    }

    private void showChildGrades(HttpServletRequest req, HttpServletResponse resp, Parent parent) throws ServletException, IOException {
        String studentIdStr = req.getParameter("studentId");
        if (studentIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/parent/children");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            if (!parentService.isLinkedToStudent(parent.getId(), studentId)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to view this student");
                return;
            }

            List<Map<String, Object>> gradesMatrix = studentService.getGradesMatrix(studentId);
            req.setAttribute("gradesMatrix", gradesMatrix);

            Set<String> allExamTypes = new java.util.LinkedHashSet<>();
            allExamTypes.add("Quiz");
            allExamTypes.add("Midterm");
            allExamTypes.add("Final");
            allExamTypes.add("Other");

            Set<String> actualTypes = new java.util.LinkedHashSet<>();
            for (Map<String, Object> map : gradesMatrix) {
                Map<String, Map<String, Object>> exams = (Map<String, Map<String, Object>>) map.get("exams");
                if (exams != null) actualTypes.addAll(exams.keySet());
            }
            allExamTypes.retainAll(actualTypes);
            allExamTypes.addAll(actualTypes);

            req.setAttribute("allExamTypes", allExamTypes);
            setChildInfo(req, studentId, parent);
            req.getRequestDispatcher("/WEB-INF/views/parent/child_grades.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/parent/children");
        }
    }

    private void showChildAttendance(HttpServletRequest req, HttpServletResponse resp, Parent parent) throws ServletException, IOException {
        String studentIdStr = req.getParameter("studentId");
        if (studentIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/parent/children");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            if (!parentService.isLinkedToStudent(parent.getId(), studentId)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to view this student");
                return;
            }

            List<Map<String, Object>> attendanceMatrix = studentService.getAttendanceMatrix(studentId);
            Set<String> allDates = new java.util.TreeSet<>();
            for (Map<String, Object> courseData : attendanceMatrix) {
                List<CourseSession> sessions = (List<CourseSession>) courseData.get("sessions");
                Map<String, String> statuses = (Map<String, String>) courseData.get("statuses");
                Map<String, String> ds = new java.util.HashMap<>();
                for (CourseSession cs : sessions) {
                    String dateKey = cs.getSessionDate().substring(5);
                    allDates.add(dateKey);
                    ds.put(dateKey, statuses.get(String.valueOf(cs.getId())));
                }
                courseData.put("dateStatuses", ds);
            }
            req.setAttribute("attendanceMatrix", attendanceMatrix);
            req.setAttribute("allSessionDates", allDates);

            setChildInfo(req, studentId, parent);
            req.getRequestDispatcher("/WEB-INF/views/parent/child_attendance.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/parent/children");
        }
    }

    private void showChildSchedule(HttpServletRequest req, HttpServletResponse resp, Parent parent) throws ServletException, IOException {
        String studentIdStr = req.getParameter("studentId");
        if (studentIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/parent/children");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            if (!parentService.isLinkedToStudent(parent.getId(), studentId)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to view this student");
                return;
            }

            java.time.LocalDate targetDate = java.time.LocalDate.now();
            String targetDateParam = req.getParameter("targetDate");
            if (targetDateParam != null && !targetDateParam.isEmpty()) {
                try { targetDate = java.time.LocalDate.parse(targetDateParam); } catch (Exception ignored) {}
            }
            req.setAttribute("targetDate", targetDateParam);

            List<CourseSession> todaySessions = studentService.getTodaySessions(studentId);
            req.setAttribute("todaySessions", todaySessions);

            List<CourseSession> weekSessions = studentService.getWeekSessions(studentId, targetDate);
            req.setAttribute("weekSessions", weekSessions);

            java.util.Map<Integer, Course> courseMap = new java.util.HashMap<>();
            List<Course> courses = studentService.getEnrolledCourses(studentId);
            for (Course c : courses) courseMap.put(c.getId(), c);
            req.setAttribute("courseMap", courseMap);

            java.util.Map<Integer, Classroom> classroomMap = new java.util.HashMap<>();
            List<CourseSession> allSessions = new java.util.ArrayList<>(todaySessions);
            allSessions.addAll(weekSessions);
            for (CourseSession cs : allSessions) {
                if (!classroomMap.containsKey(cs.getClassroomId())) {
                    studentService.getClassroom(cs.getClassroomId()).ifPresent(cr -> classroomMap.put(cr.getId(), cr));
                }
            }
            req.setAttribute("classroomMap", classroomMap);

            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate monday = targetDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            List<String> weekDates = new java.util.ArrayList<>();
            List<String> weekDayNames = new java.util.ArrayList<>();
            String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (int i = 0; i < 7; i++) {
                java.time.LocalDate d = monday.plusDays(i);
                weekDates.add(d.toString());
                weekDayNames.add(dayNames[i] + " " + d.getDayOfMonth() + "/" + d.getMonthValue());
            }
            req.setAttribute("weekDates", weekDates);
            req.setAttribute("weekDayNames", weekDayNames);
            req.setAttribute("todayDate", today.toString());

            setChildInfo(req, studentId, parent);
            req.getRequestDispatcher("/WEB-INF/views/parent/child_schedule.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/parent/children");
        }
    }

    private void showChildTranscript(HttpServletRequest req, HttpServletResponse resp, Parent parent) throws ServletException, IOException {
        String studentIdStr = req.getParameter("studentId");
        if (studentIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/parent/children");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            if (!parentService.isLinkedToStudent(parent.getId(), studentId)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to view this student");
                return;
            }

            Map<String, Object> stats = parentService.getStudentDashboardStats(studentId);
            req.setAttribute("globalAttendance", stats.get("attendanceRate"));

            List<Map<String, Object>> gradesMatrix = studentService.getGradesMatrix(studentId);
            List<Map<String, Object>> attendanceMatrix = studentService.getAttendanceMatrix(studentId);

            List<Map<String, Object>> transcriptCourses = new java.util.ArrayList<>();
            double globalGpaSum = 0;
            int gpaCourseCount = 0;
            
            for (Map<String, Object> gRow : gradesMatrix) {
                Course course = (Course) gRow.get("course");
                double average = (Double) gRow.get("average");
                if (average < 0) continue; 

                globalGpaSum += average;
                gpaCourseCount++;

                Map<String, Object> tRow = new java.util.HashMap<>();
                tRow.put("courseName", course.getCourseName());
                tRow.put("courseCode", course.getCourseCode());
                tRow.put("average", average);
                tRow.put("rank", studentService.getCourseRank(studentId, course.getId()));
                tRow.put("totalStudents", studentService.getEnrolledStudentCount(course.getId()));

                double attRate = -1;
                for (Map<String, Object> aRow : attendanceMatrix) {
                    Course aCourse = (Course) aRow.get("course");
                    if (aCourse.getId() == course.getId()) {
                        attRate = (Double) aRow.get("rate");
                        break;
                    }
                }
                tRow.put("attendanceRate", attRate > -1 ? String.format("%.0f", attRate) : "N/A");
                tRow.put("exams", gRow.get("exams"));
                transcriptCourses.add(tRow);
            }

            req.setAttribute("globalGpa", gpaCourseCount > 0 ? String.format(java.util.Locale.US, "%.2f", globalGpaSum / gpaCourseCount) : "N/A");
            req.setAttribute("transcriptCourses", transcriptCourses);

            setChildInfo(req, studentId, parent);
            req.getRequestDispatcher("/WEB-INF/views/parent/child_transcript.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/parent/children");
        }
    }

    private void setChildInfo(HttpServletRequest req, int studentId, Parent parent) {
        List<Map<String, Object>> children = parentService.getLinkedStudents(parent.getId());
        for (Map<String, Object> child : children) {
            Student s = (Student) child.get("student");
            if (s.getId() == studentId) {
                req.setAttribute("childUser", child.get("user"));
                req.setAttribute("childStudent", child.get("student"));
                break;
            }
        }
    }
}
