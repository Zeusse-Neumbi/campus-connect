package com.example.school.web.controller;

import com.example.school.model.Role;
import com.example.school.model.Teacher;
import com.example.school.model.User;
import com.example.school.service.ServiceFactory;
import com.example.school.service.TeacherService;
import com.example.school.web.handler.teacher.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "TeacherServlet", urlPatterns = { "/teacher/*" })
public class TeacherServlet extends HttpServlet {

    private TeacherService teacherService;
    private TeacherDashboardHandler dashboardHandler;
    private TeacherAcademicsHandler academicsHandler;
    private TeacherAssessmentHandler assessmentHandler;

    @Override
    public void init() throws ServletException {
        teacherService = ServiceFactory.getTeacherService();
        dashboardHandler = new TeacherDashboardHandler();
        academicsHandler = new TeacherAcademicsHandler();
        assessmentHandler = new TeacherAssessmentHandler();
    }

    private boolean processAuthentication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        Boolean impersonating = (Boolean) session.getAttribute("impersonating");
        if (user.getRole() != Role.TEACHER && (impersonating == null || !impersonating)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return false;
        }

        Optional<Teacher> teacherOpt = teacherService.getTeacherByUserId(user.getId());
        if (teacherOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Teacher profile not found.");
            return false;
        }

        req.setAttribute("teacher", teacherOpt.get());
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!processAuthentication(req, resp)) return;

        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/") || path.equals("/dashboard") || path.equals("/profile")) {
            dashboardHandler.handleGet(req, resp);
        } else if (path.equals("/courses") || path.startsWith("/groups")) {
            academicsHandler.handleGet(req, resp);
        } else if (path.equals("/grades") || path.equals("/attendance") || path.equals("/ranking") || path.startsWith("/exams")) {
            assessmentHandler.handleGet(req, resp);
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
        } else if (path.startsWith("/groups")) {
            academicsHandler.handlePost(req, resp);
        } else if (path.equals("/grades") || path.equals("/attendance") || path.startsWith("/exams")) {
            assessmentHandler.handlePost(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
