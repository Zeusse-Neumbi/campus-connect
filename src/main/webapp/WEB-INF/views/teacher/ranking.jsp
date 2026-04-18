<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Course Rankings - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/teacher_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Course Rankings</div>
            <div style="flex-grow: 1;"></div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <form action="${pageContext.request.contextPath}/teacher/ranking" method="get" class="mb-4" style="display: flex; gap: 1rem; align-items: flex-end;">
                <div>
                    <label style="display:block; margin-bottom:0.5rem; color:var(--text-muted);">Select Course</label>
                    <select name="courseId" class="form-control" onchange="this.form.submit()" style="min-width: 250px;">
                        <option value="">-- Select a Course --</option>
                        <c:forEach var="course" items="${courses}">
                            <option value="${course.id}" ${selectedCourseId == course.id ? 'selected' : ''}>
                                ${course.courseCode} - ${course.courseName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <c:if test="${not empty selectedCourseId}">
                    <a href="${pageContext.request.contextPath}/teacher/ranking" class="btn" style="background: rgba(255,255,255,0.1);">Clear</a>
                </c:if>
            </form>

            <c:if test="${not empty ranking}">
                <div style="display: flex; justify-content: flex-end; margin-bottom: 1rem;">
                    <input type="text" id="rankingSearch" class="form-control" placeholder="🔍 Search students..."
                           style="max-width: 280px;" oninput="filterRanking()">
                </div>
                <div style="overflow-x: auto; background: rgba(255,255,255,0.02); border-radius: 8px;">
                    <table class="data-table" id="rankingTable">
                        <thead>
                            <tr>
                                <th>Rank</th>
                                <th>Student Number</th>
                                <th>Name</th>
                                <c:forEach var="exam" items="${exams}">
                                    <th style="text-align: center; font-size: 0.78rem; min-width: 70px;">
                                        ${exam.examName}<br>
                                        <span style="color: var(--text-muted); font-size: 0.7rem;">${exam.examType} /${exam.maxScore}</span>
                                    </th>
                                </c:forEach>
                                <th>Final Average</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="row" items="${ranking}">
                                <tr>
                                    <td>
                                        <span class="badge ${row.rank <= 3 ? 'badge-success' : 'badge-primary'}" style="font-size: 1rem; padding: 0.3rem 0.6rem;">#${row.rank}</span>
                                    </td>
                                    <td>${row.student.studentNumber}</td>
                                    <td style="font-weight: 500;">
                                        ${row.user.firstName} ${row.user.lastName}
                                    </td>
                                    <c:forEach var="exam" items="${exams}">
                                        <td style="text-align: center;">
                                            <c:set var="score" value="${row.examScores[exam.id]}" />
                                            <c:choose>
                                                <c:when test="${score != '-'}">
                                                    <strong>${score}</strong>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: var(--text-muted);">-</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:forEach>
                                    <td>
                                        <c:choose>
                                            <c:when test="${row.average >= 0}">
                                                <strong style="color: ${row.average >= 10 ? 'var(--success-color)' : 'var(--danger-color)'}; font-size: 1.1em;">
                                                    ${row.average} / 20
                                                </strong>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted);">N/A</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty ranking}">
                                <tr>
                                    <td colspan="100" style="text-align: center; color: var(--text-muted);">No students enrolled in this course.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script>
function filterRanking() {
    const q = document.getElementById('rankingSearch').value.toLowerCase();
    const rows = document.querySelectorAll('#rankingTable tbody tr');
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(q) ? '' : 'none';
    });
}
</script>

</body>
</html>
