<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Teacher Dashboard - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/teacher-dashboard.css">
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
    <jsp:include page="/WEB-INF/views/layout/teacher_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Instructor Dashboard</div>
            <div class="user-profile">
                <span>Welcome, <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <%-- Quick Stats --%>
        <div class="quick-stats">
            <div class="stat-card">
                <div class="stat-value">${courseCount}</div>
                <div class="stat-label">Active Courses</div>
            </div>
            <div class="stat-card">
                <div class="stat-value" style="background: linear-gradient(135deg, #10b981, #059669); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">${studentCount}</div>
                <div class="stat-label">Total Students</div>
            </div>
            <div class="stat-card">
                <div class="stat-value" style="background: linear-gradient(135deg, #f59e0b, #d97706); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">${todaySessions.size()}</div>
                <div class="stat-label">Today's Sessions</div>
            </div>
        </div>

        <%-- Weekly Timetable (Mon–Sun, 4 fixed time slots) --%>
        <div class="glass-panel" style="padding: 2rem; margin-bottom: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                <h3 style="margin: 0;">📅 Weekly Timetable</h3>
                <form action="${pageContext.request.contextPath}/teacher/dashboard" method="get" style="display: flex; gap: 10px; align-items: center;">
                    <span style="font-weight: bold; font-size: 0.85rem;">Week of:</span>
                    <input type="date" name="targetDate" class="form-control" value="${targetDate}" style="width: auto;">
                    <button type="submit" class="btn btn-sm btn-primary">Load</button>
                    <c:if test="${not empty targetDate}">
                        <a href="${pageContext.request.contextPath}/teacher/dashboard" class="btn btn-sm btn-secondary">Reset</a>
                    </c:if>
                </form>
            </div>
            <div class="timetable-grid">
                <%-- Header row --%>
                <div class="timetable-header">Time</div>
                <c:forEach var="dayName" items="${weekDayNames}" varStatus="dayIdx">
                    <div class="timetable-header ${weekDates[dayIdx.index] == todayDate ? 'today-col' : ''}">${dayName}</div>
                </c:forEach>

                <%-- Slot 1: 08:00 – 10:00 --%>
                <div class="timetable-time">08:00<br>10:00</div>
                <c:forEach var="dayDate" items="${weekDates}">
                    <div class="timetable-cell ${dayDate == todayDate ? 'today-col' : ''}">
                        <c:forEach var="ws" items="${weekSessions}">
                            <c:if test="${ws.sessionDate == dayDate && ws.startTime == '08:00'}">
                                <c:set var="crs" value="${courseMap[ws.courseId]}" />
                                <c:set var="rm" value="${classroomMap[ws.classroomId]}" />
                                <div class="session-chip">
                                    <div class="chip-course">${crs != null ? crs.courseCode : '?'}</div>
                                    <div class="chip-time">${ws.startTime} - ${ws.endTime}</div>
                                    <div class="chip-room">${rm != null ? rm.roomCode : ''}</div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </c:forEach>

                <%-- Slot 2: 10:15 – 12:15 --%>
                <div class="timetable-time">10:15<br>12:15</div>
                <c:forEach var="dayDate" items="${weekDates}">
                    <div class="timetable-cell ${dayDate == todayDate ? 'today-col' : ''}">
                        <c:forEach var="ws" items="${weekSessions}">
                            <c:if test="${ws.sessionDate == dayDate && ws.startTime == '10:15'}">
                                <c:set var="crs" value="${courseMap[ws.courseId]}" />
                                <c:set var="rm" value="${classroomMap[ws.classroomId]}" />
                                <div class="session-chip">
                                    <div class="chip-course">${crs != null ? crs.courseCode : '?'}</div>
                                    <div class="chip-time">${ws.startTime} - ${ws.endTime}</div>
                                    <div class="chip-room">${rm != null ? rm.roomCode : ''}</div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </c:forEach>

                <%-- Slot 3: 13:00 – 15:00 --%>
                <div class="timetable-time">13:00<br>15:00</div>
                <c:forEach var="dayDate" items="${weekDates}">
                    <div class="timetable-cell ${dayDate == todayDate ? 'today-col' : ''}">
                        <c:forEach var="ws" items="${weekSessions}">
                            <c:if test="${ws.sessionDate == dayDate && ws.startTime == '13:00'}">
                                <c:set var="crs" value="${courseMap[ws.courseId]}" />
                                <c:set var="rm" value="${classroomMap[ws.classroomId]}" />
                                <div class="session-chip">
                                    <div class="chip-course">${crs != null ? crs.courseCode : '?'}</div>
                                    <div class="chip-time">${ws.startTime} - ${ws.endTime}</div>
                                    <div class="chip-room">${rm != null ? rm.roomCode : ''}</div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </c:forEach>

                <%-- Slot 4: 15:15 – 17:15 --%>
                <div class="timetable-time">15:15<br>17:15</div>
                <c:forEach var="dayDate" items="${weekDates}">
                    <div class="timetable-cell ${dayDate == todayDate ? 'today-col' : ''}">
                        <c:forEach var="ws" items="${weekSessions}">
                            <c:if test="${ws.sessionDate == dayDate && ws.startTime == '15:15'}">
                                <c:set var="crs" value="${courseMap[ws.courseId]}" />
                                <c:set var="rm" value="${classroomMap[ws.classroomId]}" />
                                <div class="session-chip">
                                    <div class="chip-course">${crs != null ? crs.courseCode : '?'}</div>
                                    <div class="chip-time">${ws.startTime} - ${ws.endTime}</div>
                                    <div class="chip-room">${rm != null ? rm.roomCode : ''}</div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </c:forEach>
            </div>
        </div>

        <%-- Today's Schedule (quick list) --%>
        <div class="glass-panel" style="padding: 2rem;">
            <h3>🗓️ Today's Schedule</h3>
            <div class="table-container">
                <table>
                    <thead><tr><th>Time</th><th>Course</th><th>Room</th><th>Action</th></tr></thead>
                    <tbody>
                        <c:forEach var="cs" items="${todaySessions}">
                            <c:set var="course" value="${courseMap[cs.courseId]}" />
                            <c:set var="classroom" value="${classroomMap[cs.classroomId]}" />
                            <tr>
                                <td>${cs.startTime} - ${cs.endTime}</td>
                                <td>${course != null ? course.courseCode : '?'} - ${course != null ? course.courseName : ''}</td>
                                <td>${classroom != null ? classroom.roomCode : '?'} ${classroom != null ? '('.concat(classroom.building).concat(')') : ''}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/teacher/attendance?courseId=${cs.courseId}&sessionId=${cs.id}&view=register" class="btn btn-sm btn-primary">
                                        Take Attendance
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty todaySessions}">
                            <tr><td colspan="4" style="text-align: center; color: var(--text-muted);">No sessions scheduled for today.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
