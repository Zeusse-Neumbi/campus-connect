<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>User Profile - Admin View</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/profile.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">User Profile</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
            </div>
        </div>

        <div class="profile-header">
            <div class="profile-avatar">
                ${targetUser.firstName.substring(0,1)}
            </div>
            <div>
                <h2>${targetUser.firstName} ${targetUser.lastName}</h2>
                <p>${targetUser.email}</p>
                <span class="badge" style="background: rgba(255,255,255,0.2); color: white;">
                    <c:choose>
                        <c:when test="${targetUser.roleId == 1}">Administrator</c:when>
                        <c:when test="${targetUser.roleId == 2}">Teacher</c:when>
                        <c:when test="${targetUser.roleId == 3}">Student</c:when>
                    </c:choose>
                </span>
            </div>
        </div>

        <div class="info-grid">
            <div class="glass-panel" style="padding: 2rem;">
                <h3>Account Details</h3>
                <div class="form-group"><label>User ID</label><div class="form-control" style="background: rgba(0,0,0,0.02);">${targetUser.id}</div></div>
                <div class="form-group"><label>Role ID</label><div class="form-control" style="background: rgba(0,0,0,0.02);">${targetUser.roleId}</div></div>
            </div>

            <c:if test="${not empty targetStudent}">
                <div class="glass-panel" style="padding: 2rem;">
                    <h3>Student Information</h3>
                    <div class="form-group"><label>Student Number</label><div class="form-control">${targetStudent.studentNumber}</div></div>
                    <div class="form-group"><label>Date of Birth</label><div class="form-control">${targetStudent.dateOfBirth}</div></div>
                    <h4 class="mt-2">Enrolled Courses</h4>
                    <ul>
                        <c:forEach var="c" items="${studentCourses}"><li>${c.courseName} (${c.courseCode})</li></c:forEach>
                        <c:if test="${empty studentCourses}"><li>No courses enrolled.</li></c:if>
                    </ul>
                </div>
            </c:if>

            <c:if test="${not empty targetTeacher}">
                <div class="glass-panel" style="padding: 2rem;">
                    <h3>Teacher Information</h3>
                    <div class="form-group"><label>Employee ID</label><div class="form-control">${targetTeacher.employeeId}</div></div>
                    <div class="form-group"><label>Specialization</label><div class="form-control">${targetTeacher.specialization}</div></div>
                    <h4 class="mt-2">Assigned Courses</h4>
                    <ul>
                         <c:forEach var="c" items="${teacherCourses}"><li>${c.courseName} (${c.courseCode})</li></c:forEach>
                        <c:if test="${empty teacherCourses}"><li>No courses assigned.</li></c:if>
                    </ul>
                </div>
            </c:if>
        </div>
        
        <div class="mt-2">
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Back to Users</a>
        </div>
    </div>
</div>

</body>
</html>
