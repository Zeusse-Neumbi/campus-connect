<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Course Sessions - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/teacher-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/course-sessions.css">
    <style>
        .session-card:hover .session-hover-info { display: block; }
        .session-hover-info { display: none; position: absolute; background: white; border: 1px solid #ccc; padding: 10px; z-index: 1000; box-shadow: 0px 4px 6px rgba(0,0,0,0.1); border-radius: 4px; pointer-events: none;}
    </style>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Course Session Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form id="weekForm" action="${pageContext.request.contextPath}/admin/sessions" method="get" style="display:flex; gap:10px; align-items:center;">
                        <span style="font-weight:bold;">Select Week By Date:</span>
                        <input type="date" name="targetDate" class="form-control" value="${param.targetDate}" required>
                        <button type="submit" class="btn btn-sm btn-primary">Load Week</button>
                        <c:if test="${not empty param.targetDate}">
                            <a href="${pageContext.request.contextPath}/admin/sessions" class="btn btn-sm btn-secondary" style="line-height: 2;">Reset</a>
                        </c:if>
                    </form>
                </div>
                <div class="toolbar-right">
                    <button class="btn btn-primary btn-sm" onclick="openModal('create')">+ Schedule Session</button>
                </div>
            </div>

            <div class="timetable-grid" style="margin-top: 2rem;">
                <div class="timetable-header">Time</div>
                <c:forEach var="dayName" items="${weekDayNames}" varStatus="dayIdx">
                    <div class="timetable-header ${weekDates[dayIdx.index] == todayDate ? 'today-col' : ''}">${dayName}</div>
                </c:forEach>

                <c:set var="slots" value="08:00,10:00;10:15,12:15;13:00,15:00;15:15,17:15" />
                <c:forEach var="slot" items="${slots.split(';')}">
                    <c:set var="startTime" value="${slot.split(',')[0]}" />
                    <c:set var="endTime" value="${slot.split(',')[1]}" />

                    <div class="timetable-time">${startTime}<br>${endTime}</div>
                    
                    <c:forEach var="dayDate" items="${weekDates}">
                        <div class="timetable-cell ${dayDate == todayDate ? 'today-col' : ''}" 
                             style="cursor: pointer; overflow-y: auto; max-height: 180px; position:relative;"
                             onclick="openSlotModal('${dayDate}', '${startTime}', '${endTime}')">
                             
                            <c:set var="sessionCount" value="0" />
                            <c:forEach var="ws" items="${sessions}">
                                <c:if test="${ws.sessionDate == dayDate && ws.startTime == startTime}">
                                    <c:set var="sessionCount" value="${sessionCount + 1}" />
                                </c:if>
                            </c:forEach>
                            
                            <c:if test="${sessionCount > 0}">
                                <div style="display: flex; height: 100%; align-items: center; justify-content: center;">
                                    <span class="badge" style="background: var(--primary-color); color: white; padding: 0.5rem; text-align: center;">${sessionCount} Session(s)</span>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:forEach>
            </div>

        </div>
    </div>
</div>

<!-- Slot Details Modal -->
<div id="slotModal" class="modal">
    <div class="modal-content" style="max-width: 800px;">
        <span class="close" onclick="closeSlotModal()">&times;</span>
        <h2 id="slotModalTitle" style="margin-bottom: 1.5rem; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 0.5rem;">Sessions</h2>
        <div id="slotModalContent" style="margin: 1.5rem 0; max-height: 500px; overflow-y: auto; display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1rem; padding-right: 0.5rem;">
            <!-- JS injects sessions here -->
        </div>
        <button class="btn btn-primary" onclick="addNewSessionForSlot()">+ Add Session in this Slot</button>
    </div>
</div>

<!-- Session Modal -->
<div id="sessionModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Schedule New Session</h2>
        <form id="sessionForm" action="${pageContext.request.contextPath}/admin/sessions" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="sessionId">
            
            <div class="form-group">
                <label>Course</label>
                <select name="courseId" id="courseId" class="form-control" required>
                    <option value="">-- Select Course --</option>
                    <c:forEach var="c" items="${courses}">
                        <option value="${c.id}">${c.courseCode} - ${c.courseName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>Classroom</label>
                <select name="classroomId" id="classroomId" class="form-control" required>
                    <option value="">-- Select Classroom --</option>
                    <c:forEach var="cr" items="${classrooms}">
                        <option value="${cr.id}">${cr.roomCode} (${cr.building}, cap: ${cr.capacity})</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>Course Group (optional)</label>
                <select name="courseGroupId" id="courseGroupId" class="form-control">
                    <option value="">-- None --</option>
                    <c:forEach var="g" items="${courseGroups}">
                        <option value="${g.id}">${g.groupName} (Course ID: ${g.courseId})</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>Session Date</label>
                <input type="date" name="sessionDate" id="sessionDate" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Start Time</label>
                <input type="time" name="startTime" id="startTime" class="form-control" required>
            </div>
            <div class="form-group">
                <label>End Time</label>
                <input type="time" name="endTime" id="endTime" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Session</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/sessions" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<%-- Session data must be defined before the external JS loads --%>
<script>
    const sessionsData = [
        <c:forEach var="s" items="${sessions}" varStatus="status">
            <c:set var="course" value="${courseMap[s.courseId]}" />
            <c:set var="classroom" value="${classroomMap[s.classroomId]}" />
            <c:set var="group" value="${s.courseGroupId != null ? groupMap[s.courseGroupId] : null}" />
            {
                id: ${s.id},
                date: '${s.sessionDate}',
                start: '${s.startTime}',
                end: '${s.endTime}',
                courseCode: '${course != null ? course.courseCode : "N/A"}',
                courseName: '${course != null ? course.courseName.replace("'", "\\'") : "Unknown Course"}',
                roomCode: '${classroom != null ? classroom.roomCode : "N/A"}',
                building: '${classroom != null ? classroom.building.replace("'", "\\'") : ""}',
                group: '${group != null ? group.groupName.replace("'", "\\'") : "None"}'
            }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
</script>
<script src="${pageContext.request.contextPath}/assets/js/admin/course-sessions.js?v=${System.currentTimeMillis() + 1}"></script>

</body>
</html>
