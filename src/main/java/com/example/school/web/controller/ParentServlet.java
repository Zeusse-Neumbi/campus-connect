package com.example.school.web.controller;

import com.example.school.model.Parent;
import com.example.school.model.Role;
import com.example.school.model.User;
import com.example.school.service.ParentService;
import com.example.school.service.ServiceFactory;
import com.example.school.web.handler.parent.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ParentServlet", urlPatterns = { "/parent/*" })
public class ParentServlet extends HttpServlet {

    private ParentService parentService;
    private ParentDashboardHandler dashboardHandler;
    private ParentAcademicsHandler academicsHandler;

    @Override
    public void init() throws ServletException {
        parentService = ServiceFactory.getParentService();
        dashboardHandler = new ParentDashboardHandler();
        academicsHandler = new ParentAcademicsHandler();
    }

    private boolean processAuthentication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        Boolean impersonating = (Boolean) session.getAttribute("impersonating");
        if (user.getRole() != Role.PARENT && (impersonating == null || !impersonating)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return false;
        }

        Optional<Parent> parentOpt = parentService.getParentByUserId(user.getId());
        if (parentOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Parent profile not found");
            return false;
        }

        req.setAttribute("parent", parentOpt.get());
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!processAuthentication(req, resp)) return;

        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/") || path.equals("/dashboard") || path.equals("/profile")) {
            dashboardHandler.handleGet(req, resp);
        } else if (path.equals("/children") || path.equals("/child-grades") || path.equals("/child-attendance") || 
                   path.equals("/child-schedule") || path.equals("/child-transcript")) {
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
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
