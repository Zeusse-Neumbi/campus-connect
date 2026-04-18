<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar glass-panel">
    <div class="sidebar-logo">
        <span style="font-size: 2rem;">🛡️</span> Admin Panel
    </div>
    <ul class="nav-links">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="${pageContext.request.requestURI.endsWith('/dashboard.jsp') ? 'active' : ''}">
                Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/users" class="${pageContext.request.requestURI.endsWith('/users.jsp') ? 'active' : ''}">
                User Management
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/teachers" class="${pageContext.request.requestURI.endsWith('/teachers.jsp') ? 'active' : ''}">
                Teachers
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/students" class="${pageContext.request.requestURI.endsWith('/students.jsp') ? 'active' : ''}">
                Students
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/parents" class="${pageContext.request.requestURI.endsWith('/parents.jsp') ? 'active' : ''}">
                Parents
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/student-parents" class="${pageContext.request.requestURI.endsWith('/student_parents.jsp') ? 'active' : ''}">
                Student-Parent Links
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/courses" class="${pageContext.request.requestURI.endsWith('/courses.jsp') ? 'active' : ''}">
                Courses
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/classrooms" class="${pageContext.request.requestURI.endsWith('/classrooms.jsp') ? 'active' : ''}">
                Classrooms
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/course-groups" class="${pageContext.request.requestURI.endsWith('/course_groups.jsp') ? 'active' : ''}">
                Course Groups
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/sessions" class="${pageContext.request.requestURI.endsWith('/course_sessions.jsp') ? 'active' : ''}">
                Sessions
            </a>
        </li>
        <li class="nav-item" style="margin-top: auto;">
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--danger-color);">
                Logout
            </a>
        </li>
    </ul>
</div>
