package com.example.school.web.handler.admin;

import com.example.school.model.*;
import com.example.school.service.AdminService;
import com.example.school.service.ServiceFactory;
import com.example.school.util.ParseUtil;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminAccountsHandler implements ActionHandler {
    private final AdminService adminService = ServiceFactory.getAdminService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/users": showUsers(req, resp); break;
            case "/students": showStudents(req, resp); break;
            case "/teachers": showTeachers(req, resp); break;
            case "/parents": showParents(req, resp); break;
            case "/student-parents": showStudentParents(req, resp); break;
            case "/profile": showProfile(req, resp); break;
            case "/impersonate": startImpersonation(req, resp); break;
            case "/stop-impersonate": stopImpersonation(req, resp); break;
            default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String action = req.getParameter("action");

        switch (path) {
            case "/users": handleUserAction(req, resp, action); break;
            case "/students": handleStudentAction(req, resp, action); break;
            case "/teachers": handleTeacherAction(req, resp, action); break;
            case "/parents": handleParentAction(req, resp, action); break;
            case "/student-parents": handleStudentParentAction(req, resp, action); break;
            default: resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // --- GET METHODS ---

    private void showUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        int page = ParseUtil.parseOptionalInt(req.getParameter("page"), 1);
        int pageSize = 50;

        List<User> users = adminService.searchUsers(search, page, pageSize);
        int totalUsers = adminService.countUsers(search);
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        req.setAttribute("users", users);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);

        // PERFORMANCE FIX: Only load role entries for the users rendered on page
        java.util.Map<Integer, Student> studentMap = new java.util.HashMap<>();
        java.util.Map<Integer, Teacher> teacherMap = new java.util.HashMap<>();
        java.util.Map<Integer, Parent> parentMap = new java.util.HashMap<>();
        
        for (User u : users) {
             if (u.getRole() == Role.STUDENT) {
                  adminService.getStudentByUserId(u.getId()).ifPresent(s -> studentMap.put(u.getId(), s));
             } else if (u.getRole() == Role.TEACHER) {
                  adminService.getTeacherByUserId(u.getId()).ifPresent(t -> teacherMap.put(u.getId(), t));
             } else if (u.getRole() == Role.PARENT) {
                  adminService.getParentByUserId(u.getId()).ifPresent(p -> parentMap.put(u.getId(), p));
             }
        }
        
        req.setAttribute("studentMap", studentMap);
        req.setAttribute("teacherMap", teacherMap);
        req.setAttribute("parentMap", parentMap);

        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    private void showStudents(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        int page = ParseUtil.parseOptionalInt(req.getParameter("page"), 1);
        int pageSize = 50;

        List<Student> students = adminService.searchStudents(search, page, pageSize);
        int totalStudents = adminService.countStudents(search);
        int totalPages = (int) Math.ceil((double) totalStudents / pageSize);

        // PERFORMANCE FIX: Only load users for the students rendered on this page
        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (Student s : students) {
            adminService.getUserById(s.getUserId()).ifPresent(u -> userMap.put(u.getId(), u));
        }

        req.setAttribute("students", students);
        req.setAttribute("userMap", userMap);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/students.jsp").forward(req, resp);
    }

    private void showTeachers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        int page = ParseUtil.parseOptionalInt(req.getParameter("page"), 1);
        int pageSize = 50;

        List<Teacher> teachers = adminService.searchTeachers(search, page, pageSize);
        int totalTeachers = adminService.countTeachers(search);
        int totalPages = (int) Math.ceil((double) totalTeachers / pageSize);

        // PERFORMANCE FIX: Only load users for the rendered teachers
        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (Teacher t : teachers) {
            adminService.getUserById(t.getUserId()).ifPresent(u -> userMap.put(u.getId(), u));
        }

        req.setAttribute("teachers", teachers);
        req.setAttribute("userMap", userMap);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/teachers.jsp").forward(req, resp);
    }

    private void showParents(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        int page = ParseUtil.parseOptionalInt(req.getParameter("page"), 1);
        int pageSize = 50;

        List<Parent> parents = adminService.searchParents(search, page, pageSize);
        int totalParents = adminService.countParents(search);
        int totalPages = (int) Math.ceil((double) totalParents / pageSize);

        // PERFORMANCE FIX: Only load users for rendered parents
        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (Parent p : parents) {
             adminService.getUserById(p.getUserId()).ifPresent(u -> userMap.put(u.getId(), u));
        }

        req.setAttribute("parents", parents);
        req.setAttribute("userMap", userMap);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/parents.jsp").forward(req, resp);
    }

    private void showStudentParents(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("q");
        int page = ParseUtil.parseOptionalInt(req.getParameter("page"), 1);
        int pageSize = 50;

        List<java.util.Map<String, Object>> links = adminService.searchStudentParentLinks(search, page, pageSize);
        int totalLinks = adminService.countStudentParentLinks(search);
        int totalPages = (int) Math.ceil((double) totalLinks / pageSize);

        java.util.Map<Integer, Student> studentMap = new java.util.HashMap<>();
        java.util.Map<Integer, Parent> parentMap = new java.util.HashMap<>();
        java.util.Map<Integer, User> userMap = new java.util.HashMap<>();
        for (java.util.Map<String, Object> link : links) {
            int sid = (Integer) link.get("studentId");
            int pid = (Integer) link.get("parentId");
            adminService.getStudentById(sid).ifPresent(s -> {
                studentMap.put(sid, s);
                adminService.getUserById(s.getUserId()).ifPresent(u -> userMap.put(u.getId(), u));
            });
            adminService.getParentById(pid).ifPresent(p -> {
                parentMap.put(pid, p);
                adminService.getUserById(p.getUserId()).ifPresent(u -> userMap.put(u.getId(), u));
            });
        }

        req.setAttribute("studentMap", studentMap);
        req.setAttribute("parentMap", parentMap);
        req.setAttribute("userMap", userMap);
        req.setAttribute("links", links);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchQuery", search);
        req.getRequestDispatcher("/WEB-INF/views/admin/student_parents.jsp").forward(req, resp);
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = ParseUtil.parseOptionalInt(req.getParameter("userId"), 0);
        Optional<User> targetUserOpt = adminService.getUserById(userId);

        if (targetUserOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        User targetUser = targetUserOpt.get();
        req.setAttribute("targetUser", targetUser);

        if (targetUser.getRole() == Role.TEACHER) { 
            adminService.getTeacherByUserId(userId).ifPresent(t -> {
                req.setAttribute("targetTeacher", t);
                req.setAttribute("teacherCourses", adminService.getTeacherCourses(t.getId()));
            });
        } else if (targetUser.getRole() == Role.STUDENT) { 
            adminService.getStudentByUserId(userId).ifPresent(s -> {
                req.setAttribute("targetStudent", s);
                req.setAttribute("studentCourses", adminService.getStudentCourses(s.getId()));
            });
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/profile.jsp").forward(req, resp);
    }

    private void startImpersonation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User originalAdmin = (User) session.getAttribute("user");
        int targetUserId = ParseUtil.parseOptionalInt(req.getParameter("userId"), 0);
        Optional<User> targetUserOpt = adminService.getUserById(targetUserId);

        if (targetUserOpt.isPresent() && originalAdmin.getRole() == Role.ADMIN) {
            User targetUser = targetUserOpt.get();
            session.setAttribute("originalAdmin", originalAdmin);
            session.setAttribute("user", targetUser);
            session.setAttribute("impersonating", true);

            if (targetUser.getRole() != null) {
                switch (targetUser.getRole()) {
                    case STUDENT: resp.sendRedirect(req.getContextPath() + "/student/dashboard"); break;
                    case TEACHER: resp.sendRedirect(req.getContextPath() + "/teacher/dashboard"); break;
                    case PARENT: resp.sendRedirect(req.getContextPath() + "/parent/dashboard"); break;
                    default:
                        session.removeAttribute("originalAdmin");
                        session.removeAttribute("impersonating");
                        session.setAttribute("user", originalAdmin);
                        resp.sendRedirect(req.getContextPath() + "/admin/users");
                }
            } else {
                 resp.sendRedirect(req.getContextPath() + "/admin/users");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void stopImpersonation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User originalAdmin = (User) session.getAttribute("originalAdmin");
        if (originalAdmin != null) {
            session.setAttribute("user", originalAdmin);
            session.removeAttribute("originalAdmin");
            session.removeAttribute("impersonating");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }

    // --- POST METHODS ---
    
    private void handleUserAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("create".equals(action)) {
            int roleId = ParseUtil.parseOptionalInt(req.getParameter("roleId"), 0);
            Role role = Role.fromId(roleId);
            User newUser = new User(0, req.getParameter("email"), null, roleId, req.getParameter("firstName"), req.getParameter("lastName"));
            newUser.setPhone(req.getParameter("phone"));
            String password = req.getParameter("password");

            java.util.Map<String, String> roleData = new java.util.HashMap<>();
            if (role == Role.STUDENT) {
                roleData.put("studentNumber", req.getParameter("studentNumber"));
                roleData.put("dateOfBirth", req.getParameter("dateOfBirth"));
            } else if (role == Role.TEACHER) {
                roleData.put("employeeId", req.getParameter("employeeId"));
                roleData.put("specialization", req.getParameter("specialization"));
            } else if (role == Role.PARENT) {
                roleData.put("address", req.getParameter("address"));
                roleData.put("occupation", req.getParameter("occupation"));
            }
            adminService.createUserWithRole(newUser, password, roleData);
        } else if ("update".equals(action)) {
            int userId = ParseUtil.parseOptionalInt(req.getParameter("id"), 0);
            adminService.getUserById(userId).ifPresent(existingUser -> {
                User updatedUser = new User(userId, req.getParameter("email"), existingUser.getPassword(),
                        ParseUtil.parseOptionalInt(req.getParameter("roleId"), 0), req.getParameter("firstName"), req.getParameter("lastName"));
                updatedUser.setPhone(req.getParameter("phone"));
                adminService.updateUser(updatedUser, req.getParameter("password"));
            });
        } else if ("delete".equals(action)) {
            adminService.deleteUser(ParseUtil.parseOptionalInt(req.getParameter("id"), 0));
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private void handleStudentAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("create".equals(action)) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        } else if ("update".equals(action)) {
            Student student = new Student(ParseUtil.parseOptionalInt(req.getParameter("id"), 0),
                    ParseUtil.parseOptionalInt(req.getParameter("userId"), 0), req.getParameter("studentNumber"),
                    req.getParameter("dateOfBirth"), req.getParameter("gender"), req.getParameter("admissionDate"));
            adminService.updateStudent(student);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/students");
    }

    private void handleTeacherAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("create".equals(action)) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        } else if ("update".equals(action)) {
            Teacher teacher = new Teacher(ParseUtil.parseOptionalInt(req.getParameter("id"), 0),
                    ParseUtil.parseOptionalInt(req.getParameter("userId"), 0), req.getParameter("employeeId"),
                    req.getParameter("specialization"), req.getParameter("hireDate"));
            adminService.updateTeacher(teacher);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/teachers");
    }

    private void handleParentAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("update".equals(action)) {
            Parent parent = new Parent(ParseUtil.parseOptionalInt(req.getParameter("id"), 0), ParseUtil.parseOptionalInt(req.getParameter("userId"), 0),
                    req.getParameter("address"), req.getParameter("occupation"));
            adminService.updateParent(parent);
        } else if ("delete".equals(action)) {
            adminService.getParentById(ParseUtil.parseOptionalInt(req.getParameter("id"), 0))
                    .ifPresent(p -> adminService.deleteUser(p.getUserId()));
        }
        resp.sendRedirect(req.getContextPath() + "/admin/parents");
    }

    private void handleStudentParentAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        int studentId = ParseUtil.parseOptionalInt(req.getParameter("studentId"), 0);
        int parentId = ParseUtil.parseOptionalInt(req.getParameter("parentId"), 0);
        if ("link".equals(action)) {
            adminService.linkStudentToParent(studentId, parentId, req.getParameter("relationship"));
        } else if ("unlink".equals(action)) {
            adminService.unlinkStudentFromParent(studentId, parentId);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/student-parents");
    }
}
