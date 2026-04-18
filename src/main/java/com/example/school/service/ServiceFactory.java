package com.example.school.service;

import com.example.school.dao.impl.*;

public class ServiceFactory {

    private static UserService userService;
    private static StudentService studentService;
    private static TeacherService teacherService;
    private static AdminService adminService;
    private static ParentService parentService;

    public static synchronized UserService getUserService() {
        if (userService == null) {
            userService = new UserService(new UserDaoSqliteImpl());
        }
        return userService;
    }

    public static synchronized StudentService getStudentService() {
        if (studentService == null) {
            studentService = new StudentService(
                    new StudentDaoSqliteImpl(),
                    new CourseDaoSqliteImpl(),
                    new EnrollmentDaoSqliteImpl(),
                    new ExamDaoSqliteImpl(),
                    new ExamResultDaoSqliteImpl(),
                    new AttendanceDaoSqliteImpl(),
                    new TeacherDaoSqliteImpl(),
                    new UserDaoSqliteImpl(),
                    new CourseSessionDaoSqliteImpl(),
                    new ClassroomDaoSqliteImpl(),
                    new CourseGroupDaoSqliteImpl(),
                    new StudentGroupDaoSqliteImpl());
        }
        return studentService;
    }

    public static synchronized TeacherService getTeacherService() {
        if (teacherService == null) {
            teacherService = new TeacherService(
                    new TeacherDaoSqliteImpl(),
                    new CourseDaoSqliteImpl(),
                    new EnrollmentDaoSqliteImpl(),
                    new StudentDaoSqliteImpl(),
                    new UserDaoSqliteImpl(),
                    new ExamDaoSqliteImpl(),
                    new ExamResultDaoSqliteImpl(),
                    new AttendanceDaoSqliteImpl(),
                    new CourseSessionDaoSqliteImpl(),
                    new ClassroomDaoSqliteImpl(),
                    new CourseGroupDaoSqliteImpl(),
                    new StudentGroupDaoSqliteImpl());
        }
        return teacherService;
    }

    public static synchronized AdminService getAdminService() {
        if (adminService == null) {
            adminService = new AdminService(
                    new UserDaoSqliteImpl(),
                    new StudentDaoSqliteImpl(),
                    new TeacherDaoSqliteImpl(),
                    new CourseDaoSqliteImpl(),
                    new ClassroomDaoSqliteImpl(),
                    new CourseGroupDaoSqliteImpl(),
                    new CourseSessionDaoSqliteImpl(),
                    new ParentDaoSqliteImpl(),
                    new StudentParentDaoSqliteImpl(),
                    new EnrollmentDaoSqliteImpl(),
                    new ExamDaoSqliteImpl(),
                    new ExamResultDaoSqliteImpl(),
                    new AttendanceDaoSqliteImpl());
        }
        return adminService;
    }

    public static synchronized ParentService getParentService() {
        if (parentService == null) {
            parentService = new ParentService(
                    new ParentDaoSqliteImpl(),
                    new StudentParentDaoSqliteImpl(),
                    new StudentDaoSqliteImpl(),
                    new UserDaoSqliteImpl(),
                    new CourseDaoSqliteImpl(),
                    new EnrollmentDaoSqliteImpl(),
                    new ExamDaoSqliteImpl(),
                    new ExamResultDaoSqliteImpl(),
                    new AttendanceDaoSqliteImpl(),
                    new CourseSessionDaoSqliteImpl());
        }
        return parentService;
    }
}
