<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Parent Dashboard - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/parent-dashboard.css">
</head>
<body>

<c:if test="${sessionScope.impersonating}">
    <div style="background: #f97316; color: white; padding: 10px; text-align: center; position: sticky; top: 0; z-index: 9999;">
        <strong>⚠️ Viewing as: ${sessionScope.user.firstName} ${sessionScope.user.lastName}</strong>
        <a href="${pageContext.request.contextPath}/admin/stop-impersonate"
           class="btn btn-sm" style="margin-left: 20px; background: white; color: #f97316; border: none;">
           Stop Impersonating
        </a>
    </div>
</c:if>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/parent_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Parent Dashboard</div>
            <div class="user-profile">
                <span>Welcome, <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="metric-grid">
            <div class="glass-panel metric-card">
                <div class="metric-value" style="color: var(--primary-color);">${children.size()}</div>
                <div class="metric-label">Linked Children</div>
            </div>
        </div>

        <c:forEach var="child" items="${children}">
            <div class="glass-panel child-card" style="margin-bottom: 1.5rem; padding: 2rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap;">
                    <div>
                        <h4>👤 ${child.user.firstName} ${child.user.lastName}</h4>
                        <span style="color: var(--text-muted);">${child.student.studentNumber} · ${child.relationship}</span>
                    </div>
                    <div class="child-stats">
                        <div class="child-stat"><div class="stat-value" style="color: var(--primary-color);">${child.courseCount}</div><div class="stat-label">Courses</div></div>
                        <div class="child-stat"><div class="stat-value" style="color: var(--warning-color);">${child.gpa}</div><div class="stat-label">Avg Grade</div></div>
                        <div class="child-stat"><div class="stat-value" style="color: var(--success-color);">${child.attendanceRate}%</div><div class="stat-label">Attendance</div></div>
                    </div>
                </div>
                <div style="margin-top: 1rem; display: flex; gap: 0.5rem;">
                    <a href="${pageContext.request.contextPath}/parent/child-grades?studentId=${child.student.id}" class="btn btn-sm btn-primary">View Grades</a>
                    <a href="${pageContext.request.contextPath}/parent/child-attendance?studentId=${child.student.id}" class="btn btn-sm" style="background: var(--secondary-color); color: white;">View Attendance</a>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty children}">
            <div class="glass-panel" style="padding: 2rem; text-align: center;">
                <p style="color: var(--text-muted);">No children linked to your account. Please contact the administration.</p>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>
