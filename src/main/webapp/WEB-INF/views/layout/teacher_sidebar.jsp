<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar glass-panel">
    <div class="sidebar-logo">
        <span style="font-size: 2rem;">👩‍🏫</span> Teacher Portal
    </div>
    <ul class="nav-links">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/dashboard" class="${pageContext.request.requestURI.endsWith('/dashboard.jsp') ? 'active' : ''}">
                Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/courses" class="${pageContext.request.requestURI.endsWith('/my_courses.jsp') ? 'active' : ''}">
                My Courses
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/grades" class="${pageContext.request.requestURI.endsWith('/grades_management.jsp') ? 'active' : ''}">
                Grade Mgmt
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/ranking" class="${pageContext.request.requestURI.endsWith('/ranking.jsp') ? 'active' : ''}">
                Ranking
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/attendance" class="${pageContext.request.requestURI.endsWith('/attendance_management.jsp') || pageContext.request.requestURI.endsWith('/attendance.jsp') ? 'active' : ''}">
                Attendance Mgmt
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/groups" class="${pageContext.request.requestURI.endsWith('/groups.jsp') ? 'active' : ''}">
                Group Mgmt
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/profile" class="${pageContext.request.requestURI.endsWith('/profile.jsp') ? 'active' : ''}">
                Profile
            </a>
        </li>
        <li class="nav-item" style="margin-top: auto;">
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--danger-color);">
                Logout
            </a>
        </li>
    </ul>
</div>
