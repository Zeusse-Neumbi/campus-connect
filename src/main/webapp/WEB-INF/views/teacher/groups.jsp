<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Group Management - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/teacher-groups.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/teacher_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Group Management</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem; margin-bottom: 2rem;">
            <form method="get" action="${pageContext.request.contextPath}/teacher/groups">
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
                    <c:if test="${not empty selectedCourseId}">
                        <button type="button" class="btn btn-primary" onclick="document.getElementById('createGroupModal').classList.add('active')">+ Create Group</button>
                        <button type="button" class="btn btn-sm" style="background: var(--info-color, #3b82f6); color: white;" onclick="document.getElementById('autoCreateModal').classList.add('active')">⚡ Auto-Create Groups</button>
                    </c:if>
                </div>
            </form>
        </div>

        <%-- Error messages --%>
        <c:if test="${param.error == 'full'}">
            <div class="glass-panel" style="padding: 1rem 2rem; margin-bottom: 1rem; background: rgba(239,68,68,0.1); border: 1px solid rgba(239,68,68,0.3); color: #ef4444;">
                ⚠️ Cannot add student: this group has reached its capacity.
            </div>
        </c:if>
        <c:if test="${param.error == 'duplicate'}">
            <div class="glass-panel" style="padding: 1rem 2rem; margin-bottom: 1rem; background: rgba(245,158,11,0.1); border: 1px solid rgba(245,158,11,0.3); color: #f59e0b;">
                ⚠️ Cannot add student: this student is already in another group for this course.
            </div>
        </c:if>

        <c:if test="${not empty selectedCourseId}">
            <%-- Groups Grid --%>
            <c:if test="${not empty groups}">
                <div class="group-grid">
                    <c:forEach var="g" items="${groups}">
                        <c:set var="cnt" value="${groupStudentCounts[g.id]}" />
                        <c:set var="pct" value="${g.capacity > 0 ? (cnt != null ? cnt : 0) * 100 / g.capacity : 0}" />
                        <div class="group-card ${g.id == selectedGroupId ? 'selected' : ''}" onclick="window.location='${pageContext.request.contextPath}/teacher/groups?courseId=${selectedCourseId}&groupId=${g.id}';">
                            <h4>${g.groupName}</h4>
                            <div class="meta">
                                <span>${cnt != null ? cnt : 0} / ${g.capacity} students</span>
                                <div style="display: flex; gap: 0.25rem;" onclick="event.stopPropagation();">
                                    <form action="${pageContext.request.contextPath}/teacher/groups/random-fill" method="post" style="display:inline;">
                                        <input type="hidden" name="groupId" value="${g.id}">
                                        <input type="hidden" name="courseId" value="${selectedCourseId}">
                                        <button type="submit" class="btn btn-sm" style="background: var(--info-color, #3b82f6); color: white; font-size: 0.7rem; padding: 0.2rem 0.5rem;" title="Randomly fill this group with unassigned students">🎲 Fill</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/teacher/groups/delete" method="post" style="display:inline;">
                                        <input type="hidden" name="groupId" value="${g.id}">
                                        <input type="hidden" name="courseId" value="${selectedCourseId}">
                                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Delete this group?');">Delete</button>
                                    </form>
                                </div>
                            </div>
                            <div class="capacity-bar">
                                <div class="fill" style="width: ${pct}%; background: ${pct > 90 ? '#ef4444' : (pct > 60 ? '#fbbf24' : '#34d399')};"></div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
            <c:if test="${empty groups}">
                <div class="glass-panel" style="padding: 2rem; text-align: center; color: var(--text-muted);">
                    No groups for this course yet. Click "Create Group" or "Auto-Create Groups" to start.
                </div>
            </c:if>

            <%-- Selected group member management --%>
            <c:if test="${not empty selectedGroupId}">
                <div class="glass-panel" style="padding: 2rem; margin-top: 2rem;">
                    <h3>👥 Group Members</h3>
                    <div class="student-list">
                        <c:forEach var="sg" items="${studentsInGroup}">
                            <c:set var="sInfo" value="${studentInfoMap[sg.studentId]}" />
                            <div class="student-item">
                                <div>
                                    <span class="name">
                                        <c:choose>
                                            <c:when test="${sInfo != null}">
                                                ${sInfo.user.firstName} ${sInfo.user.lastName}
                                            </c:when>
                                            <c:otherwise>Student #${sg.studentId}</c:otherwise>
                                        </c:choose>
                                    </span>
                                    <c:if test="${sInfo != null}">
                                        <span class="id-badge">${sInfo.student.studentNumber}</span>
                                    </c:if>
                                </div>
                                <form action="${pageContext.request.contextPath}/teacher/groups/remove-student" method="post">
                                    <input type="hidden" name="groupId" value="${selectedGroupId}">
                                    <input type="hidden" name="studentId" value="${sg.studentId}">
                                    <input type="hidden" name="courseId" value="${selectedCourseId}">
                                    <button class="btn btn-sm btn-danger">Remove</button>
                                </form>
                            </div>
                        </c:forEach>
                        <c:if test="${empty studentsInGroup}">
                            <p style="color: var(--text-muted); text-align: center;">No students in this group yet.</p>
                        </c:if>
                    </div>

                    <%-- Add students (only show those not in any group) --%>
                    <h4 style="margin-top: 1.5rem;">➕ Add Student</h4>
                    <form action="${pageContext.request.contextPath}/teacher/groups/add-student" method="post">
                        <input type="hidden" name="groupId" value="${selectedGroupId}">
                        <input type="hidden" name="courseId" value="${selectedCourseId}">
                        <div class="add-student-select">
                            <select name="studentId" class="input-field" required>
                                <option value="">-- Select Student --</option>
                                <c:forEach var="sData" items="${enrolledStudents}">
                                    <c:if test="${!allAssignedStudentIds.contains(sData.studentId)}">
                                        <option value="${sData.studentId}">${sData.user.firstName} ${sData.user.lastName} (${sData.student.studentNumber})</option>
                                    </c:if>
                                </c:forEach>
                            </select>
                            <button type="submit" class="btn btn-primary">Add to Group</button>
                        </div>
                    </form>
                </div>
            </c:if>
        </c:if>

        <c:if test="${empty selectedCourseId}">
            <div class="glass-panel" style="padding: 2rem; text-align: center; color: var(--text-muted);">
                Select a course to manage groups.
            </div>
        </c:if>
    </div>
</div>

<%-- Create Group Modal --%>
<div id="createGroupModal" class="modal-overlay" onclick="if(event.target===this) this.classList.remove('active')">
    <div class="modal-content">
        <h3>Create New Group</h3>
        <form action="${pageContext.request.contextPath}/teacher/groups/create" method="post">
            <input type="hidden" name="courseId" value="${selectedCourseId}">
            <div class="input-group"><label>Group Name</label><input type="text" name="groupName" class="input-field" placeholder="e.g. Group A" required></div>
            <div class="input-group"><label>Capacity</label><input type="number" name="capacity" class="input-field" min="1" max="100" value="25" required></div>
            <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                <button type="submit" class="btn btn-primary" style="flex: 1;">Create</button>
                <button type="button" class="btn btn-danger" style="flex: 1;" onclick="document.getElementById('createGroupModal').classList.remove('active')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<%-- Auto-Create Groups Modal --%>
<div id="autoCreateModal" class="modal-overlay" onclick="if(event.target===this) this.classList.remove('active')">
    <div class="modal-content">
        <h3>⚡ Auto-Create Groups</h3>
        <p style="color: var(--text-muted); margin-bottom: 1rem;">
            Automatically creates groups and distributes all unassigned students randomly.
        </p>
        <form action="${pageContext.request.contextPath}/teacher/groups/auto-create" method="post">
            <input type="hidden" name="courseId" value="${selectedCourseId}">
            <div class="input-group">
                <label>Students per group (capacity)</label>
                <input type="number" name="capacity" class="input-field" min="1" max="100" value="25" required>
            </div>
            <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                <button type="submit" class="btn btn-primary" style="flex: 1;">Create & Fill</button>
                <button type="button" class="btn btn-danger" style="flex: 1;" onclick="document.getElementById('autoCreateModal').classList.remove('active')">Cancel</button>
            </div>
        </form>
    </div>
</div>

</body>
</html>
