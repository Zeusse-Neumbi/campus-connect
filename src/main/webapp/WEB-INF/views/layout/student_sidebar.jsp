<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar glass-panel">
    <div class="sidebar-logo">
        <span style="font-size: 2rem;">🎓</span> Student Zone
    </div>
    <ul class="nav-links">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="${pageContext.request.requestURI.endsWith('/dashboard.jsp') ? 'active' : ''}">
                Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/courses" class="${pageContext.request.requestURI.endsWith('/courses.jsp') ? 'active' : ''}">
                My Courses
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/grades" class="${pageContext.request.requestURI.endsWith('/grades.jsp') ? 'active' : ''}">
                Grades
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/attendance" class="${pageContext.request.requestURI.endsWith('/attendance.jsp') ? 'active' : ''}">
                Attendance
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/groups" class="${pageContext.request.requestURI.endsWith('/groups.jsp') ? 'active' : ''}">
                My Groups
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/transcript" class="${pageContext.request.requestURI.endsWith('/transcript.jsp') ? 'active' : ''}">
                Transcript
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/profile" class="${pageContext.request.requestURI.endsWith('/profile.jsp') ? 'active' : ''}">
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
