<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>My Children - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/parent_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">My Children</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="table-container">
                <table>
                    <thead><tr><th>Student Number</th><th>Name</th><th>Relationship</th><th>Actions</th></tr></thead>
                    <tbody>
                        <c:forEach var="child" items="${children}">
                            <tr>
                                <td>${child.student.studentNumber}</td>
                                <td>${child.user.firstName} ${child.user.lastName}</td>
                                <td>${child.relationship}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/parent/child-grades?studentId=${child.student.id}" class="btn btn-sm btn-primary">Grades</a>
                                    <a href="${pageContext.request.contextPath}/parent/child-attendance?studentId=${child.student.id}" class="btn btn-sm" style="background: var(--secondary-color); color: white;">Attendance</a>
                                    <a href="${pageContext.request.contextPath}/parent/child-schedule?studentId=${child.student.id}" class="btn btn-sm" style="background: #10b981; color: white;">Schedule</a>
                                    <a href="${pageContext.request.contextPath}/parent/child-transcript?studentId=${child.student.id}" class="btn btn-sm" style="background: #6366f1; color: white;">Transcript</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty children}"><tr><td colspan="4" style="text-align:center;">No children linked to your account.</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
