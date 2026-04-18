package com.example.school.web.controller;

import com.example.school.model.Role;
import com.example.school.model.User;
import com.example.school.service.ServiceFactory;
import com.example.school.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = ServiceFactory.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // If user is already logged in, redirect to their dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            redirectUser(resp, user.getRole());
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Optional<User> userOpt = userService.authenticate(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            redirectUser(resp, user.getRole());
            return;
        }

        // Login failed
        req.setAttribute("error", "Invalid email or password");
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    private void redirectUser(HttpServletResponse resp, Role role) throws IOException {
        if (role == null) {
            resp.sendRedirect("login?error=UnknownRole");
            return;
        }
        switch (role) {
            case ADMIN:
                resp.sendRedirect("admin/dashboard");
                break;
            case TEACHER:
                resp.sendRedirect("teacher/dashboard");
                break;
            case STUDENT:
                resp.sendRedirect("student/dashboard");
                break;
            case PARENT:
                resp.sendRedirect("parent/dashboard");
                break;
            default:
                resp.sendRedirect("login?error=UnknownRole");
        }
    }
}
