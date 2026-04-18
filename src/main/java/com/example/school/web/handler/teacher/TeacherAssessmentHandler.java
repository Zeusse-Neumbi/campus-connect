package com.example.school.web.handler.teacher;

import com.example.school.model.*;
import com.example.school.service.ServiceFactory;
import com.example.school.service.TeacherService;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class TeacherAssessmentHandler implements ActionHandler {
    private final TeacherService teacherService = ServiceFactory.getTeacherService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Teacher teacher = (Teacher) req.getAttribute("teacher");
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/grades": showGradesManagement(req, resp, teacher); break;
            case "/attendance": showAttendanceManagement(req, resp, teacher); break;
            case "/ranking": showRanking(req, resp, teacher); break;
            default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/grades": handleGradeSubmission(req, resp); break;
            case "/attendance": handleAttendanceSubmission(req, resp); break;
            case "/exams/create": handleExamCreate(req, resp); break;
            case "/exams/delete": handleExamDelete(req, resp); break;
            default: resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showGradesManagement(HttpServletRequest req, HttpServletResponse resp, Teacher teacher) throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courses", courses);

        String courseIdParam = req.getParameter("courseId");
        String examIdParam = req.getParameter("examId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);

                List<Exam> exams = teacherService.getExamsForCourse(courseId);
                req.setAttribute("exams", exams);

                List<java.util.Map<String, Object>> students = teacherService.getCourseStudentsData(courseId, null);
                req.setAttribute("students", students);

                if (examIdParam != null && !examIdParam.isEmpty()) {
                    int examId = Integer.parseInt(examIdParam);
                    req.setAttribute("selectedExamId", examId);
                    req.setAttribute("resultsMap", teacherService.getExamResultsForStudents(examId, students));
                    req.setAttribute("remarksMap", teacherService.getExamRemarksForStudents(examId, students));

                    teacherService.getExamById(examId).ifPresent(e -> req.setAttribute("selectedExam", e));
                }
            } catch (NumberFormatException ignored) {}
        }
        req.getRequestDispatcher("/WEB-INF/views/teacher/grades_management.jsp").forward(req, resp);
    }

    private void showAttendanceManagement(HttpServletRequest req, HttpServletResponse resp, Teacher teacher) throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courses", courses);

        String courseIdParam = req.getParameter("courseId");
        String view = req.getParameter("view");
        if (view == null || view.isEmpty()) view = "overview";
        req.setAttribute("currentView", view);

        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);

                List<java.util.Map<String, Object>> allStudents = teacherService.getCourseStudentsData(courseId, null);
                req.setAttribute("students", allStudents);

                java.util.Map<Integer, java.util.Map<String, Object>> studentInfoMap = new java.util.HashMap<>();
                for (java.util.Map<String, Object> sData : allStudents) {
                    studentInfoMap.put((Integer) sData.get("studentId"), sData);
                }
                req.setAttribute("studentInfoMap", studentInfoMap);

                if ("overview".equals(view)) {
                    List<CourseSession> sessions = teacherService.getCourseSessions(courseId);
                    req.setAttribute("courseSessions", sessions);

                    java.util.Map<String, java.util.Map<String, String>> matrix = teacherService.getAttendanceMatrix(courseId);

                    java.util.List<java.util.Map<String, Object>> attendanceRows = new java.util.ArrayList<>();
                    for (java.util.Map<String, Object> sData : allStudents) {
                        Integer sid = (Integer) sData.get("studentId");
                        java.util.Map<String, Object> row = new java.util.HashMap<>(sData);

                        java.util.Map<String, String> studentStatuses = matrix.get(String.valueOf(sid));
                        java.util.List<String> statusList = new java.util.ArrayList<>();
                        for (CourseSession cs : sessions) {
                            String st = (studentStatuses != null) ? studentStatuses.get(String.valueOf(cs.getId())) : null;
                            statusList.add(st != null ? st : "NONE");
                        }
                        row.put("statuses", statusList);
                        row.put("rate", teacherService.getStudentAttendanceRate(sid, courseId));

                        attendanceRows.add(row);
                    }
                    req.setAttribute("attendanceRows", attendanceRows);

                    java.util.Map<Integer, Classroom> classroomMap = new java.util.HashMap<>();
                    for (CourseSession cs : sessions) {
                        if (!classroomMap.containsKey(cs.getClassroomId())) {
                            teacherService.getClassroom(cs.getClassroomId()).ifPresent(cr -> classroomMap.put(cr.getId(), cr));
                        }
                    }
                    req.setAttribute("classroomMap", classroomMap);

                } else if ("register".equals(view)) {
                    List<CourseSession> unattended = teacherService.getUnattendedSessions(courseId);
                    req.setAttribute("unattendedSessions", unattended);

                    List<CourseSession> allSessions = teacherService.getCourseSessions(courseId);
                    req.setAttribute("allSessions", allSessions);

                    String sessionIdParam = req.getParameter("sessionId");
                    if (sessionIdParam != null && !sessionIdParam.isEmpty()) {
                        int sessionId = Integer.parseInt(sessionIdParam);
                        req.setAttribute("selectedSessionId", sessionId);

                        for (java.util.Map<String, Object> map : allStudents) {
                            int studentId = (Integer) map.get("studentId");
                            teacherService.getAttendance(sessionId, studentId).ifPresent(att -> map.put("status", att.getStatus()));
                        }
                    }

                    java.util.Map<Integer, Classroom> classroomMap = new java.util.HashMap<>();
                    for (CourseSession cs : allSessions) {
                        if (!classroomMap.containsKey(cs.getClassroomId())) {
                            teacherService.getClassroom(cs.getClassroomId()).ifPresent(cr -> classroomMap.put(cr.getId(), cr));
                        }
                    }
                    req.setAttribute("classroomMap", classroomMap);
                }
            } catch (NumberFormatException ignored) {}
        }
        req.getRequestDispatcher("/WEB-INF/views/teacher/attendance_management.jsp").forward(req, resp);
    }

    private void showRanking(HttpServletRequest req, HttpServletResponse resp, Teacher teacher) throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courses", courses);

        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);

                java.util.List<java.util.Map<String, Object>> ranking = teacherService.getCourseRanking(courseId);
                List<Exam> exams = teacherService.getExamsForCourse(courseId);
                req.setAttribute("exams", exams);

                for (java.util.Map<String, Object> row : ranking) {
                    int studentId = (Integer) row.get("studentId");
                    java.util.Map<Integer, String> examScores = new java.util.LinkedHashMap<>();
                    for (Exam exam : exams) {
                        java.util.Optional<ExamResult> result = teacherService.getExamById(exam.getId())
                                .flatMap(e -> {
                                    java.util.Map<Integer, Integer> resultsMap = teacherService.getExamResultsForStudents(exam.getId(), java.util.List.of(row));
                                    Integer score = resultsMap.get(studentId);
                                    if (score != null) return java.util.Optional.of(new ExamResult(0, exam.getId(), studentId, score, null, null));
                                    return java.util.Optional.empty();
                                });
                        examScores.put(exam.getId(), result.map(r -> String.valueOf(r.getScore())).orElse("-"));
                    }
                    row.put("examScores", examScores);
                }
                req.setAttribute("ranking", ranking);
            } catch (NumberFormatException ignored) {}
        }
        req.getRequestDispatcher("/WEB-INF/views/teacher/ranking.jsp").forward(req, resp);
    }

    private void handleGradeSubmission(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int examId = Integer.parseInt(req.getParameter("examId"));
            int studentId = Integer.parseInt(req.getParameter("studentId"));
            int score = Integer.parseInt(req.getParameter("score"));
            String remark = req.getParameter("remark");
            teacherService.updateExamResult(examId, studentId, score, remark);

            String referrer = req.getHeader("referer");
            resp.sendRedirect(referrer != null ? referrer : req.getContextPath() + "/teacher/grades");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Exam Result Data");
        }
    }

    private void handleAttendanceSubmission(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionIdParam = req.getParameter("sessionId");
        String courseIdParam = req.getParameter("courseId");

        if (courseIdParam != null && sessionIdParam != null) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                int sessionId = Integer.parseInt(sessionIdParam);
                List<java.util.Map<String, Object>> students = teacherService.getCourseStudentsData(courseId, null);

                for (java.util.Map<String, Object> map : students) {
                    int studentId = (Integer) map.get("studentId");
                    String status = req.getParameter("status_" + studentId);
                    if (status != null) teacherService.updateAttendance(sessionId, studentId, status);
                }
            } catch (NumberFormatException ignored) {}
        }
        resp.sendRedirect(req.getContextPath() + "/teacher/attendance?courseId=" + courseIdParam + "&sessionId=" + sessionIdParam + "&view=register");
    }

    private void handleExamCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            Exam exam = new Exam(0, courseId, req.getParameter("examName"), req.getParameter("examType"),
                    req.getParameter("examDate"), Double.parseDouble(req.getParameter("maxScore")));
            teacherService.createExam(exam);
            resp.sendRedirect(req.getContextPath() + "/teacher/grades?courseId=" + courseId);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Exam Data");
        }
    }

    private void handleExamDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int examId = Integer.parseInt(req.getParameter("examId"));
            String courseId = req.getParameter("courseId");
            teacherService.deleteExam(examId);
            resp.sendRedirect(req.getContextPath() + "/teacher/grades?courseId=" + (courseId != null ? courseId : ""));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Exam Data");
        }
    }
}
