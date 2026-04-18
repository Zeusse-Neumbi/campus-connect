<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Student Dashboard - School Management</title>
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
    <jsp:include page="/WEB-INF/views/layout/student_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Student Dashboard</div>
            <div class="user-profile">
                <span>Welcome, <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="metric-grid">
            <div class="glass-panel metric-card">
                <span class="metric-label">Enrolled Courses</span>
                <span class="metric-value">${courseCount}</span>
            </div>
            <div class="glass-panel metric-card">
                <span class="metric-label">Overall Average</span>
                <span class="metric-value">${overallAvg}</span>
            </div>
            <div class="glass-panel metric-card">
                <span class="metric-label">Attendance Rate</span>
                <span class="metric-value">${attendanceRate}%</span>
            </div>
        </div>

        <%-- Weekly Timetable (Mon–Sun, 4 fixed time slots) --%>
        <div class="glass-panel" style="padding: 2rem; margin-top: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                <h3 style="margin: 0;">📅 Weekly Timetable</h3>
                <form action="${pageContext.request.contextPath}/student/dashboard" method="get" style="display: flex; gap: 10px; align-items: center;">
                    <span style="font-weight: bold; font-size: 0.85rem;">Week of:</span>
                    <input type="date" name="targetDate" class="form-control" value="${targetDate}" style="width: auto;">
                    <button type="submit" class="btn btn-sm btn-primary">Load</button>
                    <c:if test="${not empty targetDate}">
                        <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-sm btn-secondary">Reset</a>
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
    </div>
</div>

</body>
</html>
