<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Admin Dashboard - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/dashboard.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        
        <div class="header glass-panel">
            <div class="page-title dashboard-title">Dashboard Overview</div>
            <div class="user-profile">
                <span class="admin-name">Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar dashboard-avatar">${sessionScope.user.firstName.substring(0,1).toUpperCase()}</div>
            </div>
        </div>

        <div class="welcome-banner">
            <h1>Hello, ${sessionScope.user.firstName}! 👋</h1>
            <p>Welcome back to your administration portal. Everything is running smoothly today. Here is what's happening across your institution.</p>
            <div class="banner-decoration">🏫</div>
        </div>

        <div class="dashboard-grid">
            <a href="${pageContext.request.contextPath}/admin/users" class="stat-card delay-1" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-indigo" data-count="${userCount}">0</span>
                    <span class="stat-label">Total Users</span>
                </div>
                <div class="stat-icon bg-indigo">👥</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/students" class="stat-card delay-2" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-emerald" data-count="${studentCount}">0</span>
                    <span class="stat-label">Students Active</span>
                </div>
                <div class="stat-icon bg-emerald">🎓</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/teachers" class="stat-card delay-3" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-pink" data-count="${teacherCount}">0</span>
                    <span class="stat-label">Staff & Teachers</span>
                </div>
                <div class="stat-icon bg-pink">👩‍🏫</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/parents" class="stat-card delay-4" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-cyan" data-count="${parentCount}">0</span>
                    <span class="stat-label">Parents</span>
                </div>
                <div class="stat-icon bg-cyan">👨‍👩‍👧</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/courses" class="stat-card delay-5" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-amber" data-count="${courseCount}">0</span>
                    <span class="stat-label">Total Courses</span>
                </div>
                <div class="stat-icon bg-amber">📚</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/classrooms" class="stat-card delay-6" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-violet" data-count="${classroomCount}">0</span>
                    <span class="stat-label">Classrooms</span>
                </div>
                <div class="stat-icon bg-violet">🚪</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/course-groups" class="stat-card delay-7" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-emerald" data-count="${courseGroupCount}">0</span>
                    <span class="stat-label">Course Groups</span>
                </div>
                <div class="stat-icon bg-emerald">📋</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/sessions" class="stat-card delay-8" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-indigo" data-count="${sessionCount}">0</span>
                    <span class="stat-label">Scheduled Sessions</span>
                </div>
                <div class="stat-icon bg-indigo">🕒</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/student-parents" class="stat-card delay-9" style="text-decoration:none; color:inherit;">
                <div class="stat-info">
                    <span class="stat-value text-pink" data-count="${studentParentLinkCount}">0</span>
                    <span class="stat-label">Family Links</span>
                </div>
                <div class="stat-icon bg-pink">🔗</div>
            </a>
        </div>

        <div class="two-col-layout">
            <div class="panel delay-7">
                <div class="panel-header">
                    <h3 class="panel-title">⚡ Quick Management</h3>
                </div>
                <div class="quick-actions">
                    <a href="${pageContext.request.contextPath}/admin/users" class="action-btn">
                        <span class="action-icon">👩‍💻</span>
                        <span>User Portal</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/courses" class="action-btn">
                        <span class="action-icon">📖</span>
                        <span>Manage Curriculum</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/sessions" class="action-btn">
                        <span class="action-icon">📅</span>
                        <span>Session Planner</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/student-parents" class="action-btn">
                        <span class="action-icon">🔗</span>
                        <span>Family Links</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/classrooms" class="action-btn">
                        <span class="action-icon">🏢</span>
                        <span>Classrooms</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/parents" class="action-btn">
                        <span class="action-icon">👨‍👩‍👧</span>
                        <span>Parents</span>
                    </a>
                </div>
            </div>

            <div class="panel delay-8">
                <div class="panel-header">
                    <h3 class="panel-title">🟢 System Health</h3>
                </div>
                <ul class="status-list">
                    <li class="status-item">
                        <div class="status-indicator indicator-emerald"></div>
                        <div class="status-text">Database Cluster</div>
                        <div class="status-time">Online</div>
                    </li>
                    <li class="status-item">
                        <div class="status-indicator indicator-emerald"></div>
                        <div class="status-text">Authentication APIs</div>
                        <div class="status-time">99.9% Uptime</div>
                    </li>
                    <li class="status-item">
                        <div class="status-indicator indicator-cyan"></div>
                        <div class="status-text">Automated Backups</div>
                        <div class="status-time">Synced 2h ago</div>
                    </li>
                    <li class="status-item">
                        <div class="status-indicator indicator-amber"></div>
                        <div class="status-text">Server Storage</div>
                        <div class="status-time">68% Used</div>
                    </li>
                </ul>
            </div>
        </div>

    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/admin/dashboard.js"></script>

</body>
</html>
