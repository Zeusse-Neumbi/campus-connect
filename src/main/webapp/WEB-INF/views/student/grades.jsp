<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>My Grades - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/student_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">My Grades</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="flex-between mb-2">
                <h3>Academic Performance</h3>
            </div>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th style="position: sticky; left: 0; background: rgba(255,255,255,0.95); z-index: 2;">Course</th>
                            <c:forEach var="type" items="${allExamTypes}">
                                <th style="text-align: center;">${type}</th>
                            </c:forEach>
                            <th style="text-align: center;">Average ( / 20)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="row" items="${gradesMatrix}">
                            <tr>
                                <td style="position: sticky; left: 0; background: rgba(255,255,255,0.95); z-index: 1;">
                                    <strong>${row.course.courseName}</strong>
                                    <br><span style="color: var(--text-muted); font-size: 0.8rem;">${row.course.courseCode}</span>
                                </td>
                                <c:forEach var="type" items="${allExamTypes}">
                                    <td style="text-align: center;">
                                        <c:set var="examData" value="${row.exams[type]}" />
                                        <c:choose>
                                            <c:when test="${not empty examData}">
                                                <strong>${examData.score}</strong> / ${examData.maxScore}
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted);">—</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:forEach>
                                <td style="text-align: center;">
                                    <c:choose>
                                        <c:when test="${row.average >= 0}">
                                            <c:choose>
                                                <c:when test="${row.average >= 10}">
                                                    <span class="badge badge-success">${row.average} / 20</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-danger">${row.average} / 20</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: var(--text-muted);">—</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty gradesMatrix}">
                            <tr><td colspan="${allExamTypes.size() + 2}" style="text-align:center;">No enrolled courses found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
