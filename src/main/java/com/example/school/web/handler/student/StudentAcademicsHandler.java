package com.example.school.web.handler.student;

import com.example.school.model.Course;
import com.example.school.model.CourseSession;
import com.example.school.model.Student;
import com.example.school.service.ServiceFactory;
import com.example.school.service.StudentService;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudentAcademicsHandler implements ActionHandler {

    private final StudentService studentService = ServiceFactory.getStudentService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Student student = (Student) req.getAttribute("student");
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/courses": showCourses(req, resp, student); break;
            case "/grades": showGrades(req, resp, student); break;
            case "/attendance": showAttendance(req, resp, student); break;
            case "/groups": showGroups(req, resp, student); break;
            case "/transcript": showTranscript(req, resp, student); break;
            default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Student student = (Student) req.getAttribute("student");
        String path = req.getPathInfo();
        
        if ("/enroll".equals(path)) {
            handleEnroll(req, resp, student);
        } else if ("/unenroll".equals(path)) {
            handleUnenroll(req, resp, student);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleEnroll(HttpServletRequest req, HttpServletResponse resp, Student student) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            studentService.enroll(student.getId(), courseId);
            resp.sendRedirect(req.getContextPath() + "/student/courses");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        }
    }

    private void handleUnenroll(HttpServletRequest req, HttpServletResponse resp, Student student) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            studentService.unenroll(student.getId(), courseId);
            resp.sendRedirect(req.getContextPath() + "/student/courses");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        }
    }

    private void showCourses(HttpServletRequest req, HttpServletResponse resp, Student student) throws ServletException, IOException {
        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());
        List<Course> availableCourses = studentService.getAvailableCourses(student.getId());

        Map<Integer, String> teacherNames = new HashMap<>();
        for (Course c : enrolledCourses) teacherNames.put(c.getId(), studentService.getTeacherName(c.getId()));
        for (Course c : availableCourses) teacherNames.put(c.getId(), studentService.getTeacherName(c.getId()));

        req.setAttribute("enrolledCourses", enrolledCourses);
        req.setAttribute("availableCourses", availableCourses);
        req.setAttribute("teacherNames", teacherNames);
        req.getRequestDispatcher("/WEB-INF/views/student/courses.jsp").forward(req, resp);
    }

    private void showGrades(HttpServletRequest req, HttpServletResponse resp, Student student) throws ServletException, IOException {
        List<Map<String, Object>> gradesMatrix = studentService.getGradesMatrix(student.getId());
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
        req.getRequestDispatcher("/WEB-INF/views/student/grades.jsp").forward(req, resp);
    }

    private void showAttendance(HttpServletRequest req, HttpServletResponse resp, Student student) throws ServletException, IOException {
        List<Map<String, Object>> attendanceMatrix = studentService.getAttendanceMatrix(student.getId());
        
        Set<String> allDates = new java.util.TreeSet<>();
        for (Map<String, Object> courseData : attendanceMatrix) {
            List<CourseSession> sessions = (List<CourseSession>) courseData.get("sessions");
            Map<String, String> statuses = (Map<String, String>) courseData.get("statuses");
            Map<String, String> dateStatuses = new HashMap<>();
            
            for (CourseSession cs : sessions) {
                String dateKey = cs.getSessionDate().substring(5);
                allDates.add(dateKey);
                dateStatuses.put(dateKey, statuses.get(String.valueOf(cs.getId())));
            }
            courseData.put("dateStatuses", dateStatuses);
        }
        req.setAttribute("attendanceMatrix", attendanceMatrix);
        req.setAttribute("allSessionDates", allDates);

        req.getRequestDispatcher("/WEB-INF/views/student/attendance.jsp").forward(req, resp);
    }

    private void showGroups(HttpServletRequest req, HttpServletResponse resp, Student student) throws ServletException, IOException {
        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());
        req.setAttribute("enrolledCourses", enrolledCourses);

        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);
                List<Map<String, Object>> groupsData = studentService.getStudentGroupsDataForCourse(student.getId(), courseId);
                req.setAttribute("groupsData", groupsData);
            } catch (NumberFormatException ignored) {}
        }
        req.getRequestDispatcher("/WEB-INF/views/student/groups.jsp").forward(req, resp);
    }
    
    private void showTranscript(HttpServletRequest req, HttpServletResponse resp, Student student) throws ServletException, IOException {
        double attendanceRate = studentService.getAttendanceRate(student.getId());
        req.setAttribute("globalAttendance", attendanceRate > -1 ? String.format("%.0f", attendanceRate) : "N/A");

        List<Map<String, Object>> gradesMatrix = studentService.getGradesMatrix(student.getId());
        List<Map<String, Object>> attendanceMatrix = studentService.getAttendanceMatrix(student.getId());

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
            tRow.put("rank", studentService.getCourseRank(student.getId(), course.getId()));
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

        req.getRequestDispatcher("/WEB-INF/views/student/transcript.jsp").forward(req, resp);
    }
}
