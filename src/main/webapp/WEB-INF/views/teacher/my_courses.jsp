<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>My Courses - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/teacher-courses.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/teacher_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">My Courses</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty courses}">
                <div class="course-grid">
                    <c:forEach var="course" items="${courses}">
                        <c:set var="avgScore" value="${avgScoreMap[course.id]}" />
                        <c:set var="attRate" value="${attendanceRateMap[course.id]}" />
                        <div class="course-card">
                            <div class="course-header">
                                <span class="course-code">${course.courseCode}</span>
                                <span class="credits-badge">${course.credits} Credits</span>
                            </div>
                            <h3>${course.courseName}</h3>
                            <div class="students-count">👥 ${studentCountMap[course.id]} students enrolled</div>

                            <%-- Analytics --%>
                            <div class="analytics-row">
                                <div class="analytics-item">
                                    <div class="label">Avg. Score</div>
                                    <div class="value" style="color: ${avgScore >= 0 ? (avgScore >= 10 ? '#34d399' : '#f87171') : '#64748b'};">
                                        <c:choose>
                                            <c:when test="${avgScore >= 0}">
                                                <fmt:formatNumber value="${avgScore}" maxFractionDigits="1" /> / 20
                                            </c:when>
                                            <c:otherwise>No data</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <c:if test="${avgScore >= 0}">
                                        <div class="progress-bar">
                                            <div class="fill" style="width: ${avgScore * 5}%; background: ${avgScore >= 10 ? 'linear-gradient(90deg, #34d399, #059669)' : 'linear-gradient(90deg, #f87171, #ef4444)'};"></div>
                                        </div>
                                    </c:if>
                                </div>
                                <div class="analytics-item">
                                    <div class="label">Attendance</div>
                                    <div class="value" style="color: ${attRate >= 0 ? (attRate >= 70 ? '#60a5fa' : '#fbbf24') : '#64748b'};">
                                        <c:choose>
                                            <c:when test="${attRate >= 0}">
                                                <fmt:formatNumber value="${attRate}" maxFractionDigits="0" />%
                                            </c:when>
                                            <c:otherwise>No data</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <c:if test="${attRate >= 0}">
                                        <div class="progress-bar">
                                            <div class="fill" style="width: ${attRate}%; background: ${attRate >= 70 ? 'linear-gradient(90deg, #60a5fa, #3b82f6)' : 'linear-gradient(90deg, #fbbf24, #f59e0b)'};"></div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="course-actions">
                                <a href="${pageContext.request.contextPath}/teacher/grades?courseId=${course.id}" class="btn btn-sm btn-primary">Grades</a>
                                <a href="${pageContext.request.contextPath}/teacher/attendance?courseId=${course.id}" class="btn btn-sm btn-primary">Attendance</a>
                                <a href="${pageContext.request.contextPath}/teacher/groups?courseId=${course.id}" class="btn btn-sm" style="background: rgba(139,92,246,0.2); color: #c4b5fd;">Groups</a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="glass-panel" style="padding: 2rem;">
                    <p style="text-align: center; color: #666;">No active courses found.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

</body>
</html>
