<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>My Courses - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/student_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">My Courses</div>
            <div style="flex-grow: 1;"></div>
            <button class="btn btn-primary" onclick="openEnrollModal()">+ Enroll in Course</button>
            <div class="user-profile" style="margin-left: 20px;">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>Enrolled Courses</h3>
            <div class="table-container">
                <table>
                    <thead><tr><th>Code</th><th>Course Name</th><th>Teacher</th><th>Credits</th><th>Actions</th></tr></thead>
                    <tbody>
                        <c:forEach var="course" items="${enrolledCourses}">
                            <tr>
                                <td>${course.courseCode}</td>
                                <td>${course.courseName}</td>
                                <td>${teacherNames[course.id] != null ? teacherNames[course.id] : 'TBA'}</td>
                                <td>${course.credits}</td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/student/unenroll" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to unenroll?');">
                                        <input type="hidden" name="courseId" value="${course.id}">
                                        <button type="submit" class="btn btn-sm btn-danger" style="background-color: #ef4444; color: white; border: none;">Unenroll</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty enrolledCourses}">
                             <tr><td colspan="5" style="text-align:center;">You are not currently enrolled in any courses.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Enroll Modal -->
<div id="enrollModal" class="modal">
    <div class="modal-content" style="max-width: 700px;">
        <span class="close" onclick="closeEnrollModal()">&times;</span>
        <h2>Available Courses</h2>
        <div class="table-container">
            <table>
                <thead><tr><th>Code</th><th>Course Name</th><th>Teacher</th><th>Credits</th><th>Action</th></tr></thead>
                <tbody>
                    <c:forEach var="course" items="${availableCourses}">
                        <tr>
                            <td>${course.courseCode}</td>
                            <td>${course.courseName}</td>
                            <td>${teacherNames[course.id] != null ? teacherNames[course.id] : 'TBA'}</td>
                            <td>${course.credits}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/student/enroll" method="post" style="display:inline;">
                                    <input type="hidden" name="courseId" value="${course.id}">
                                    <button type="submit" class="btn btn-sm btn-primary">Enroll</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty availableCourses}">
                         <tr><td colspan="5" style="text-align:center;">No courses available for enrollment.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/student/courses.js"></script>

</body>
</html>
