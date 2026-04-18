package com.example.school.web.controller;

import com.example.school.model.Role;
import com.example.school.model.Student;
import com.example.school.model.User;
import com.example.school.service.ServiceFactory;
import com.example.school.service.StudentService;
import com.example.school.web.handler.student.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "StudentServlet", urlPatterns = { "/student/*" })
public class StudentServlet extends HttpServlet {

    private StudentService studentService;
    private StudentDashboardHandler dashboardHandler;
    private StudentAcademicsHandler academicsHandler;

    @Override
    public void init() throws ServletException {
        studentService = ServiceFactory.getStudentService();
        dashboardHandler = new StudentDashboardHandler();
        academicsHandler = new StudentAcademicsHandler();
    }

    private boolean processAuthentication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        Boolean impersonating = (Boolean) session.getAttribute("impersonating");
        if (user.getRole() != Role.STUDENT && (impersonating == null || !impersonating)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return false;
        }

        Optional<Student> studentOpt = studentService.getStudentByUserId(user.getId());
        if (studentOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Student profile not found.");
            return false;
        }

        req.setAttribute("student", studentOpt.get());
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!processAuthentication(req, resp)) return;

        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/") || path.equals("/dashboard") || path.equals("/profile")) {
            dashboardHandler.handleGet(req, resp);
        } else if (path.equals("/courses") || path.equals("/grades") || path.equals("/attendance") || 
                   path.equals("/groups") || path.equals("/transcript")) {
            academicsHandler.handleGet(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!processAuthentication(req, resp)) return;

        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/profile")) {
            dashboardHandler.handlePost(req, resp);
        } else if (path.equals("/enroll") || path.equals("/unenroll")) {
            academicsHandler.handlePost(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
