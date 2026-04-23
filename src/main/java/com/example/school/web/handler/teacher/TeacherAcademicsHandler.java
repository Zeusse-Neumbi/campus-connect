package com.example.school.web.handler.teacher;

import com.example.school.model.Course;
import com.example.school.model.CourseGroup;
import com.example.school.model.StudentGroup;
import com.example.school.model.Teacher;
import com.example.school.service.ServiceFactory;
import com.example.school.service.TeacherService;
import com.example.school.util.ParseUtil;
import com.example.school.web.handler.ActionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class TeacherAcademicsHandler implements ActionHandler {
    private final TeacherService teacherService = ServiceFactory.getTeacherService();

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Teacher teacher = (Teacher) req.getAttribute("teacher");
        String path = req.getPathInfo();
        if (path == null) path = "/";

        if ("/courses".equals(path)) {
            showCourses(req, resp, teacher);
        } else if ("/groups".equals(path)) {
            showGroups(req, resp, teacher);
        }
    }

    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/groups/create": handleGroupAction(req, resp, "create"); break;
            case "/groups/delete": handleGroupAction(req, resp, "delete"); break;
            case "/groups/add-student": handleGroupAction(req, resp, "addStudent"); break;
            case "/groups/remove-student": handleGroupAction(req, resp, "removeStudent"); break;
            case "/groups/random-fill": handleGroupAction(req, resp, "autoAssign"); break;
            case "/groups/auto-create": handleAutoCreateGroups(req, resp); break;
            default: resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showCourses(HttpServletRequest req, HttpServletResponse resp, Teacher teacher) throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courses", courses);
        req.setAttribute("studentCountMap", teacherService.getStudentCountsPerCourse(courses));

        java.util.Map<Integer, Double> avgScoreMap = new java.util.HashMap<>();
        java.util.Map<Integer, Double> attendanceRateMap = new java.util.HashMap<>();
        for (Course c : courses) {
            avgScoreMap.put(c.getId(), teacherService.getAverageScoreForCourse(c.getId()));
            attendanceRateMap.put(c.getId(), teacherService.getAttendanceRateForCourse(c.getId()));
        }
        req.setAttribute("avgScoreMap", avgScoreMap);
        req.setAttribute("attendanceRateMap", attendanceRateMap);

        req.getRequestDispatcher("/WEB-INF/views/teacher/my_courses.jsp").forward(req, resp);
    }

    private void showGroups(HttpServletRequest req, HttpServletResponse resp, Teacher teacher) throws ServletException, IOException {
        List<Course> courses = teacherService.getTeacherCourses(teacher.getId());
        req.setAttribute("courses", courses);

        String courseIdParam = req.getParameter("courseId");
        String groupIdParam = req.getParameter("groupId");

        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                req.setAttribute("selectedCourseId", courseId);

                List<CourseGroup> groups = teacherService.getGroupsForCourse(courseId);
                req.setAttribute("groups", groups);
                req.setAttribute("groupStudentCounts", teacherService.getGroupStudentCounts(groups));

                List<java.util.Map<String, Object>> enrolledStudents = teacherService.getCourseStudentsData(courseId, null);
                req.setAttribute("enrolledStudents", enrolledStudents);

                java.util.Set<Integer> allAssignedIds = new java.util.HashSet<>();
                for (CourseGroup cg : groups) {
                    for (StudentGroup sg : teacherService.getStudentsInGroup(cg.getId())) {
                        allAssignedIds.add(sg.getStudentId());
                    }
                }
                req.setAttribute("allAssignedStudentIds", allAssignedIds);

                if (groupIdParam != null && !groupIdParam.isEmpty()) {
                    int groupId = Integer.parseInt(groupIdParam);
                    req.setAttribute("selectedGroupId", groupId);

                    List<StudentGroup> assignedStudents = teacherService.getStudentsInGroup(groupId);
                    java.util.Set<Integer> assignedIds = new java.util.HashSet<>();
                    for (StudentGroup sg : assignedStudents) assignedIds.add(sg.getStudentId());
                    req.setAttribute("assignedStudentIds", assignedIds);

                    List<java.util.Map<String, Object>> currentAssigned = new java.util.ArrayList<>();
                    java.util.Map<Integer, java.util.Map<String, Object>> studentInfoMap = new java.util.HashMap<>();
                    for (java.util.Map<String, Object> s : enrolledStudents) {
                        int sid = (Integer) s.get("studentId");
                        if (assignedIds.contains(sid)) {
                            currentAssigned.add(s);
                            studentInfoMap.put(sid, s);
                        }
                    }
                    req.setAttribute("studentsInGroup", assignedStudents);
                    req.setAttribute("studentInfoMap", studentInfoMap);

                    groups.stream().filter(g -> g.getId() == groupId).findFirst().ifPresent(g -> req.setAttribute("selectedGroup", g));
                }
            } catch (NumberFormatException ignored) {}
        }
        req.getRequestDispatcher("/WEB-INF/views/teacher/groups.jsp").forward(req, resp);
    }

    private void handleGroupAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        if ("create".equals(action)) {
            int courseId = ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0);
            CourseGroup cg = new CourseGroup(0, courseId, req.getParameter("groupName"), ParseUtil.parseOptionalInt(req.getParameter("capacity"), 0));
            teacherService.createCourseGroup(cg);
            resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId);
            
        } else if ("delete".equals(action)) {
            int groupId = ParseUtil.parseOptionalInt(req.getParameter("groupId"), 0);
            int courseId = ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0);
            teacherService.deleteCourseGroup(groupId);
            resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId);
            
        } else if ("addStudent".equals(action)) {
            int groupId = ParseUtil.parseOptionalInt(req.getParameter("groupId"), 0);
            int studentId = ParseUtil.parseOptionalInt(req.getParameter("studentId"), 0);
            int courseId = ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0);
            java.util.Optional<CourseGroup> grpOpt = teacherService.getGroupById(groupId);
            if (grpOpt.isPresent()) {
                int capacity = grpOpt.get().getCapacity();
                int currentStudents = teacherService.getStudentsInGroup(groupId).size();
                if (currentStudents >= capacity) {
                    resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId + "&groupId=" + groupId + "&error=full");
                    return;
                }
            }
            teacherService.addStudentToGroup(studentId, groupId);
            resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId + "&groupId=" + groupId);
            
        } else if ("removeStudent".equals(action)) {
            int groupId = ParseUtil.parseOptionalInt(req.getParameter("groupId"), 0);
            int studentId = ParseUtil.parseOptionalInt(req.getParameter("studentId"), 0);
            int courseId = ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0);
            teacherService.removeStudentFromGroup(studentId, groupId);
            resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId + "&groupId=" + groupId);
            
        } else if ("autoAssign".equals(action)) {
            try {
                int groupId = ParseUtil.parseOptionalInt(req.getParameter("groupId"), 0);
                int courseId = ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0);

                java.util.Optional<CourseGroup> groupOpt = teacherService.getGroupById(groupId);
                if (groupOpt.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId);
                    return;
                }
                CourseGroup group = groupOpt.get();

                List<CourseGroup> courseGroups = teacherService.getGroupsForCourse(courseId);
                java.util.Set<Integer> assignedStudentIds = new java.util.HashSet<>();
                for (CourseGroup cg : courseGroups) {
                    for (StudentGroup sg : teacherService.getStudentsInGroup(cg.getId())) {
                        assignedStudentIds.add(sg.getStudentId());
                    }
                }

                List<java.util.Map<String, Object>> enrolled = teacherService.getCourseStudentsData(courseId, null);
                java.util.List<Integer> unassigned = new java.util.ArrayList<>();
                for (java.util.Map<String, Object> s : enrolled) {
                    int sid = (Integer) s.get("studentId");
                    if (!assignedStudentIds.contains(sid)) {
                        unassigned.add(sid);
                    }
                }

                java.util.Collections.shuffle(unassigned);
                int currentCount = teacherService.getStudentsInGroup(groupId).size();
                int slotsLeft = group.getCapacity() - currentCount;
                if (slotsLeft > 0) {
                    for (int i = 0; i < Math.min(slotsLeft, unassigned.size()); i++) {
                        teacherService.addStudentToGroup(unassigned.get(i), groupId);
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId + "&groupId=" + groupId);
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Data");
            }
        }
    }

    private void handleAutoCreateGroups(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int courseId = ParseUtil.parseOptionalInt(req.getParameter("courseId"), 0);
            int capacity = ParseUtil.parseOptionalInt(req.getParameter("capacity"), 0);

            List<java.util.Map<String, Object>> enrolled = teacherService.getCourseStudentsData(courseId, null);
            List<CourseGroup> existingGroups = teacherService.getGroupsForCourse(courseId);
            java.util.Set<Integer> assignedStudentIds = new java.util.HashSet<>();
            for (CourseGroup cg : existingGroups) {
                for (StudentGroup sg : teacherService.getStudentsInGroup(cg.getId())) {
                    assignedStudentIds.add(sg.getStudentId());
                }
            }

            java.util.List<Integer> unassigned = new java.util.ArrayList<>();
            for (java.util.Map<String, Object> s : enrolled) {
                int sid = (Integer) s.get("studentId");
                if (!assignedStudentIds.contains(sid)) {
                    unassigned.add(sid);
                }
            }

            if (unassigned.isEmpty() || capacity <= 0) {
                resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId);
                return;
            }

            java.util.Collections.shuffle(unassigned);
            int groupCount = (int) Math.ceil((double) unassigned.size() / capacity);
            int nextGroupNum = existingGroups.size() + 1;

            for (int g = 0; g < groupCount; g++) {
                String groupName = "Group " + (nextGroupNum + g);
                CourseGroup newGroup = new CourseGroup(0, courseId, groupName, capacity);
                teacherService.createCourseGroup(newGroup);

                List<CourseGroup> updated = teacherService.getGroupsForCourse(courseId);
                CourseGroup createdGroup = updated.get(updated.size() - 1);

                int start = g * capacity;
                int end = Math.min(start + capacity, unassigned.size());
                for (int i = start; i < end; i++) {
                    teacherService.addStudentToGroup(unassigned.get(i), createdGroup.getId());
                }
            }
            resp.sendRedirect(req.getContextPath() + "/teacher/groups?courseId=" + courseId);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Data");
        }
    }
}
