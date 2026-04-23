package com.example.school.web.controller;

import static org.mockito.Mockito.*;

import com.example.school.model.Role;
import com.example.school.model.User;
import com.example.school.service.ServiceFactory;
import com.example.school.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoginServletTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;
    @Mock private UserService userService;

    @InjectMocks
    private LoginServlet loginServlet;

    private MockedStatic<ServiceFactory> mockedServiceFactory;

    @BeforeEach
    void setUp() throws Exception {
        mockedServiceFactory = mockStatic(ServiceFactory.class);
        mockedServiceFactory.when(ServiceFactory::getUserService).thenReturn(userService);
        loginServlet.init();
    }

    @AfterEach
    void tearDown() {
        mockedServiceFactory.close();
    }

    @Test
    void doGet_ShouldRedirectToDashboard_WhenSessionExists() throws Exception {
        User user = new User(1, "test@test.com", "pass", 1, "Admin", "User");
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);

        loginServlet.doGet(request, response);

        verify(response).sendRedirect("admin/dashboard");
    }

    @Test
    void doGet_ShouldForwardToLoginView_WhenNoSessionExists() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(dispatcher);

        loginServlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    void doPost_ShouldRedirectToAdminDashboard_WhenCredentialsAreValidAdmin() throws Exception {
        User user = new User(1, "admin@test.com", "pass", Role.ADMIN.getId(), "Admin", "User");
        
        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("password");
        when(userService.authenticate("admin@test.com", "password")).thenReturn(Optional.of(user));
        when(request.getSession()).thenReturn(session);

        loginServlet.doPost(request, response);

        verify(session).setAttribute("user", user);
        verify(response).sendRedirect("admin/dashboard");
    }

    @Test
    void doPost_ShouldForwardToLogin_WhenCredentialsAreInvalid() throws Exception {
        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("wrong");
        when(userService.authenticate("admin@test.com", "wrong")).thenReturn(Optional.empty());
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(dispatcher);

        loginServlet.doPost(request, response);

        verify(request).setAttribute("error", "Invalid email or password");
        verify(dispatcher).forward(request, response);
    }
}
