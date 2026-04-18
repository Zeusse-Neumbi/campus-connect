<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Attendance Management - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/teacher-attendance.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/teacher_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Attendance Management</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <%-- Course selector --%>
        <div class="glass-panel" style="padding: 1.5rem 2rem; margin-bottom: 1.5rem;">
            <form method="get" action="${pageContext.request.contextPath}/teacher/attendance">
                <input type="hidden" name="view" value="${currentView}">
                <div style="display: flex; gap: 1rem; align-items: end; flex-wrap: wrap;">
                    <div class="input-group" style="margin-bottom: 0; flex: 1; min-width: 220px;">
                        <label>Select Course</label>
                        <select name="courseId" class="input-field" onchange="this.form.submit()">
                            <option value="">-- Select a Course --</option>
                            <c:forEach var="c" items="${courses}">
                                <option value="${c.id}" ${c.id == selectedCourseId ? 'selected' : ''}>${c.courseCode} - ${c.courseName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </form>
        </div>

        <%-- Tab navigation --%>
        <c:if test="${not empty selectedCourseId}">
            <div class="attendance-tabs">
                <a href="${pageContext.request.contextPath}/teacher/attendance?courseId=${selectedCourseId}&view=overview"
                   class="attendance-tab ${currentView == 'overview' ? 'active' : ''}">📊 Attendance Overview</a>
                <a href="${pageContext.request.contextPath}/teacher/attendance?courseId=${selectedCourseId}&view=register"
                   class="attendance-tab ${currentView == 'register' ? 'active' : ''}">✍️ Register Attendance</a>
            </div>
        </c:if>

        <%-- ==================== OVERVIEW VIEW ==================== --%>
        <c:if test="${currentView == 'overview' && not empty selectedCourseId}">
            <div class="glass-panel" style="padding: 2rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                    <h3 style="margin: 0;">📊 Attendance Tracking per Session</h3>
                    <input type="text" id="attOverviewSearch" class="input-field" placeholder="🔍 Search students..." style="max-width: 250px;" oninput="filterTable('attOverviewSearch', 'overviewTable')">
                </div>

                <c:choose>
                    <c:when test="${not empty courseSessions && not empty attendanceRows}">
                        <div class="table-container" style="overflow-x: auto;">
                            <table id="overviewTable">
                                <thead>
                                    <tr>
                                        <th style="position: sticky; left: 0; background: rgba(255,255,255,0.95); z-index: 2;">Student</th>
                                        <c:forEach var="cs" items="${courseSessions}">
                                            <th style="text-align: center; font-size: 0.72rem; min-width: 45px; padding: 0.5rem 0.3rem;">
                                                ${cs.sessionDate.substring(5)}
                                            </th>
                                        </c:forEach>
                                        <th style="text-align: center;">Rate</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="row" items="${attendanceRows}">
                                        <tr>
                                            <td style="position: sticky; left: 0; background: rgba(255,255,255,0.95); z-index: 1; white-space: nowrap;">
                                                <strong>${row.user.firstName} ${row.user.lastName}</strong>
                                                <br><span style="color: var(--text-muted); font-size: 0.78rem;">${row.student.studentNumber}</span>
                                            </td>
                                            <c:forEach var="st" items="${row.statuses}">
                                                <td style="text-align: center; padding: 0.4rem;">
                                                    <c:choose>
                                                        <c:when test="${st eq 'Present' || st eq 'PRESENT' || st eq 'present'}"><span class="att-status present" title="Present"></span></c:when>
                                                        <c:when test="${st eq 'Absent' || st eq 'ABSENT' || st eq 'absent'}"><span class="att-status absent" title="Absent"></span></c:when>
                                                        <c:when test="${st eq 'Late' || st eq 'LATE' || st eq 'late'}"><span class="att-status late" title="Late"></span></c:when>
                                                        <c:when test="${st ne 'NONE'}"><span class="att-status present" title="${st}"></span></c:when>
                                                        <c:otherwise><span class="att-status none" title="Not recorded"></span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </c:forEach>
                                            <td style="text-align: center;">
                                                <c:choose>
                                                    <c:when test="${row.rate >= 80}">
                                                        <span class="att-rate good"><fmt:formatNumber value="${row.rate}" maxFractionDigits="0"/>%</span>
                                                    </c:when>
                                                    <c:when test="${row.rate >= 50}">
                                                        <span class="att-rate warn"><fmt:formatNumber value="${row.rate}" maxFractionDigits="0"/>%</span>
                                                    </c:when>
                                                    <c:when test="${row.rate >= 0}">
                                                        <span class="att-rate bad"><fmt:formatNumber value="${row.rate}" maxFractionDigits="0"/>%</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: var(--text-muted); font-size: 0.8rem;">—</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <%-- Legend --%>
                        <div class="att-legend">
                            <div class="att-legend-item"><span class="att-status present"></span> Present</div>
                            <div class="att-legend-item"><span class="att-status late"></span> Late</div>
                            <div class="att-legend-item"><span class="att-status absent"></span> Absent</div>
                            <div class="att-legend-item"><span class="att-status none"></span> Not recorded</div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p style="text-align: center; color: var(--text-muted); padding: 2rem 0;">
                            <c:choose>
                                <c:when test="${empty courseSessions}">No sessions found for this course.</c:when>
                                <c:otherwise>No students enrolled in this course.</c:otherwise>
                            </c:choose>
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <%-- ==================== REGISTER VIEW ==================== --%>
        <c:if test="${currentView == 'register' && not empty selectedCourseId}">
            <%-- Session selector --%>
            <div class="glass-panel" style="padding: 2rem; margin-bottom: 1.5rem;">
                <h3>✍️ Select a Session</h3>
                <form method="get" action="${pageContext.request.contextPath}/teacher/attendance">
                    <input type="hidden" name="courseId" value="${selectedCourseId}">
                    <input type="hidden" name="view" value="register">
                    <div class="input-group" style="margin-bottom: 0; max-width: 500px;">
                        <label>Session</label>
                        <select name="sessionId" class="input-field" onchange="this.form.submit()">
                            <option value="">-- Select a Session --</option>
                            <c:forEach var="cs" items="${allSessions}">
                                <c:set var="isUnattended" value="false" />
                                <c:forEach var="ua" items="${unattendedSessions}">
                                    <c:if test="${ua.id == cs.id}"><c:set var="isUnattended" value="true" /></c:if>
                                </c:forEach>
                                <option value="${cs.id}" ${cs.id == selectedSessionId ? 'selected' : ''}>
                                    ${cs.sessionDate} | ${cs.startTime}-${cs.endTime}
                                    <c:if test="${classroomMap[cs.classroomId] != null}"> | ${classroomMap[cs.classroomId].roomCode}</c:if>
                                    <c:if test="${isUnattended == 'true'}"> ⚠️ Incomplete</c:if>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </form>
            </div>

            <%-- Attendance form --%>
            <c:if test="${not empty selectedSessionId && not empty students}">
                <div class="glass-panel" style="padding: 2rem;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <h3 style="margin: 0;">Mark Attendance</h3>
                        <input type="text" id="attRegisterSearch" class="input-field" placeholder="🔍 Search students..." style="max-width: 250px;" oninput="filterTable('attRegisterSearch', 'registerTable')">
                    </div>
                    <form action="${pageContext.request.contextPath}/teacher/attendance" method="post">
                        <input type="hidden" name="courseId" value="${selectedCourseId}">
                        <input type="hidden" name="sessionId" value="${selectedSessionId}">
                        <div class="table-container">
                            <table id="registerTable">
                                <thead>
                                    <tr>
                                        <th>Student ID</th>
                                        <th>Name</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="sMap" items="${students}">
                                        <tr>
                                            <td>${sMap.student.studentNumber}</td>
                                            <td>${sMap.user.firstName} ${sMap.user.lastName}</td>
                                            <td>
                                                <div class="radio-group">
                                                    <label>
                                                        <input type="radio" name="status_${sMap.studentId}" value="Present" ${sMap.status == 'Present' ? 'checked' : ''} required>
                                                        <span>Present</span>
                                                    </label>
                                                    <label>
                                                        <input type="radio" name="status_${sMap.studentId}" value="Absent" ${sMap.status == 'Absent' ? 'checked' : ''}>
                                                        <span>Absent</span>
                                                    </label>
                                                    <label>
                                                        <input type="radio" name="status_${sMap.studentId}" value="Late" ${sMap.status == 'Late' ? 'checked' : ''}>
                                                        <span>Late</span>
                                                    </label>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty students}">
                                        <tr><td colspan="3" style="text-align: center; color: var(--text-muted);">No students enrolled.</td></tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                        <div style="text-align: center; margin-top: 1.5rem;">
                            <button type="submit" class="btn btn-primary">Save Attendance Record</button>
                        </div>
                    </form>
                </div>
            </c:if>

            <c:if test="${empty selectedSessionId}">
                <div class="glass-panel" style="padding: 2rem; text-align: center; color: var(--text-muted);">
                    Select a session above to record attendance.
                </div>
            </c:if>
        </c:if>

        <%-- Empty state --%>
        <c:if test="${empty selectedCourseId}">
            <div class="glass-panel" style="padding: 2rem; text-align: center; color: var(--text-muted);">
                Please select a course to manage attendance.
            </div>
        </c:if>
    </div>
</div>

<script>
function filterTable(inputId, tableId) {
    const q = document.getElementById(inputId).value.toLowerCase();
    const table = document.getElementById(tableId);
    if (!table) return;
    const rows = table.querySelectorAll('tbody tr');
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(q) ? '' : 'none';
    });
}
</script>
</body>
</html>
