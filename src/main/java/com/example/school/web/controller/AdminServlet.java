package com.example.school.web.controller;

import com.example.school.web.handler.admin.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    private AdminDashboardHandler dashboardHandler;
    private AdminAccountsHandler accountsHandler;
    private AdminAcademicsHandler academicsHandler;

    @Override
    public void init() throws ServletException {
        dashboardHandler = new AdminDashboardHandler();
        accountsHandler = new AdminAccountsHandler();
        academicsHandler = new AdminAcademicsHandler();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/") || path.equals("/dashboard")) {
            dashboardHandler.handleGet(req, resp);
        } else if (path.equals("/users") || path.equals("/students") || path.equals("/teachers") || 
                   path.equals("/parents") || path.equals("/student-parents") || path.equals("/profile") || 
                   path.equals("/impersonate") || path.equals("/stop-impersonate")) {
            accountsHandler.handleGet(req, resp);
        } else if (path.equals("/courses") || path.equals("/classrooms") || path.equals("/course-groups") || 
                   path.equals("/sessions")) {
            academicsHandler.handleGet(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/users") || path.equals("/students") || path.equals("/teachers") || 
            path.equals("/parents") || path.equals("/student-parents")) {
            accountsHandler.handlePost(req, resp);
        } else if (path.equals("/courses") || path.equals("/classrooms") || path.equals("/course-groups") || 
                   path.equals("/sessions")) {
            academicsHandler.handlePost(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
