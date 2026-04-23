package com.example.school.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceFactoryTest {

    @Test
    void getUserService_ShouldReturnSingletonInstance() {
        UserService instance1 = ServiceFactory.getUserService();
        UserService instance2 = ServiceFactory.getUserService();

        assertNotNull(instance1);
        assertSame(instance1, instance2, "ServiceFactory should return the same singleton instance");
    }

    @Test
    void getStudentService_ShouldReturnSingletonInstance() {
        StudentService instance1 = ServiceFactory.getStudentService();
        StudentService instance2 = ServiceFactory.getStudentService();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    void getTeacherService_ShouldReturnSingletonInstance() {
        TeacherService instance1 = ServiceFactory.getTeacherService();
        TeacherService instance2 = ServiceFactory.getTeacherService();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    void getAdminService_ShouldReturnSingletonInstance() {
        AdminService instance1 = ServiceFactory.getAdminService();
        AdminService instance2 = ServiceFactory.getAdminService();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    void getParentService_ShouldReturnSingletonInstance() {
        ParentService instance1 = ServiceFactory.getParentService();
        ParentService instance2 = ServiceFactory.getParentService();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }
}
