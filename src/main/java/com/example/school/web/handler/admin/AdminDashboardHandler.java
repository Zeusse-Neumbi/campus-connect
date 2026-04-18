package com.example.school.web.handler.admin;

import com.example.school.model.*;
import com.example.school.service.AdminService;
import com.example.school.service.ServiceFactory;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AdminDashboardHandler implements ActionHandler {
    private final AdminService adminService = ServiceFactory.getAdminService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        java.util.Map<String, Integer> stats = adminService.getDashboardStats();
        req.setAttribute("userCount", stats.get("userCount"));
        req.setAttribute("studentCount", stats.get("studentCount"));
        req.setAttribute("teacherCount", stats.get("teacherCount"));
        req.setAttribute("parentCount", stats.get("parentCount"));
        req.setAttribute("courseCount", stats.get("courseCount"));
        req.setAttribute("classroomCount", stats.get("classroomCount"));
        req.setAttribute("courseGroupCount", stats.get("courseGroupCount"));
        req.setAttribute("sessionCount", stats.get("sessionCount"));
        req.setAttribute("studentParentLinkCount", stats.get("studentParentLinkCount"));
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
