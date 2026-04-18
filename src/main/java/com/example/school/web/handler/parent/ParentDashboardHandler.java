package com.example.school.web.handler.parent;

import com.example.school.model.Parent;
import com.example.school.model.Student;
import com.example.school.model.User;
import com.example.school.service.ParentService;
import com.example.school.service.ServiceFactory;
import com.example.school.service.UserService;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParentDashboardHandler implements ActionHandler {

    private final ParentService parentService = ServiceFactory.getParentService();
    private final UserService userService = ServiceFactory.getUserService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Parent parent = (Parent) req.getAttribute("parent");
        User user = (User) req.getSession().getAttribute("user");
        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/dashboard") || path.equals("/")) {
            showDashboard(req, resp, parent);
        } else if (path.equals("/profile")) {
            showProfile(req, resp, parent, user);
        }
    }

    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Parent parent = (Parent) req.getAttribute("parent");
        User user = (User) req.getSession().getAttribute("user");
        String path = req.getPathInfo();

        if ("/profile".equals(path)) {
            handleProfileUpdate(req, resp, parent, user);
        }
    }

    private void handleProfileUpdate(HttpServletRequest req, HttpServletResponse resp, Parent parent, User sessionUser) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");
        String occupation = req.getParameter("occupation");

        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Email cannot be empty");
            showProfile(req, resp, parent, sessionUser);
            return;
        }

        Optional<User> userOpt = userService.findById(parent.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (phone != null) user.setPhone(phone);
            userService.updateProfile(user, email, password);

            if (occupation != null) parent.setOccupation(occupation);
            parentService.updateParent(parent);

            req.getSession().setAttribute("user", user);
            req.setAttribute("success", "Profile updated successfully");
            showProfile(req, resp, parent, user);
        } else {
            req.setAttribute("error", "User not found");
            showProfile(req, resp, parent, sessionUser);
        }
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp, Parent parent) throws ServletException, IOException {
        List<Map<String, Object>> children = parentService.getLinkedStudents(parent.getId());
        for (Map<String, Object> child : children) {
            Student student = (Student) child.get("student");
            Map<String, Object> stats = parentService.getStudentDashboardStats(student.getId());
            child.put("courseCount", stats.get("courseCount"));
            child.put("gpa", stats.get("gpa"));
            child.put("attendanceRate", stats.get("attendanceRate"));
        }
        req.setAttribute("children", children);
        req.getRequestDispatcher("/WEB-INF/views/parent/dashboard.jsp").forward(req, resp);
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp, Parent parent, User user) throws ServletException, IOException {
        req.setAttribute("parent", parent);
        req.setAttribute("parentUser", user);
        req.getRequestDispatcher("/WEB-INF/views/parent/profile.jsp").forward(req, resp);
    }
}
