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

        <div class="children-grid" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem;">
            <c:forEach var="child" items="${children}">
                <div class="glass-panel metric-card" style="padding: 2rem; display: flex; flex-direction: column; justify-content: space-between;">
                    <div>
                        <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1.5rem;">
                            <div class="avatar" style="background: linear-gradient(135deg, #10b981, #059669); font-size: 1.2rem; width: 50px; height: 50px;">
                                ${child.user.firstName.substring(0,1)}
                            </div>
                            <div>
                                <h3 style="margin: 0; font-size: 1.2rem; display: flex; align-items: center; gap: 0.5rem;">
                                    ${child.user.firstName} ${child.user.lastName}
                                </h3>
                                <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 500;">
                                    ${child.student.studentNumber} &bull; ${child.relationship}
                                </span>
                            </div>
                        </div>
                        
                        <div class="child-stats" style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 2rem; background: rgba(0,0,0,0.02); border-radius: 8px; padding: 1rem;">
                            <div class="child-stat" style="text-align: center;">
                                <div class="stat-value" style="font-size: 1.2rem; font-weight: 700; color: var(--primary-color);">${child.courseCount}</div>
                                <div class="stat-label" style="font-size: 0.75rem; color: var(--text-muted);">Courses</div>
                            </div>
                            <div class="child-stat" style="text-align: center;">
                                <div class="stat-value" style="font-size: 1.2rem; font-weight: 700; color: var(--warning-color); border-left: 1px solid rgba(0,0,0,0.05); border-right: 1px solid rgba(0,0,0,0.05);">${child.gpa}</div>
                                <div class="stat-label" style="font-size: 0.75rem; color: var(--text-muted);">Avg Grade</div>
                            </div>
                            <div class="child-stat" style="text-align: center;">
                                <div class="stat-value" style="font-size: 1.2rem; font-weight: 700; color: var(--success-color);">${child.attendanceRate}%</div>
                                <div class="stat-label" style="font-size: 0.75rem; color: var(--text-muted);">Attendance</div>
                            </div>
                        </div>
                    </div>
                    
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                        <a href="${pageContext.request.contextPath}/parent/child-schedule?studentId=${child.student.id}" class="btn btn-sm" style="justify-content: center; background: rgba(59, 130, 246, 0.1); color: #3b82f6; border: 1px solid rgba(59, 130, 246, 0.2);">📅 Schedule</a>
                        <a href="${pageContext.request.contextPath}/parent/child-grades?studentId=${child.student.id}" class="btn btn-sm btn-primary" style="justify-content: center;">📝 Grades</a>
                        <a href="${pageContext.request.contextPath}/parent/child-attendance?studentId=${child.student.id}" class="btn btn-sm" style="justify-content: center; background: var(--secondary-color); color: white;">✔️ Attendance</a>
                        <a href="${pageContext.request.contextPath}/parent/child-transcript?studentId=${child.student.id}" class="btn btn-sm" style="justify-content: center; background: #3b82f6; color: white;">💰 Transcript</a>
                    </div>
                </div>
            </c:forEach>
        </div>

        <c:if test="${empty children}">
            <div class="glass-panel" style="padding: 2rem; text-align: center;">
                <p style="color: var(--text-muted);">No children linked to your account. Please contact the administration.</p>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>
