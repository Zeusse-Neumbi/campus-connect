<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>My Groups - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/student_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">My Groups</div>
            <div class="user-profile">
                <span>Welcome, <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>👥 Student Groups</h3>
            <p>Select a course to view its groups and members.</p>

            <%-- Course Selector --%>
            <form action="${pageContext.request.contextPath}/student/groups" method="get" style="display: flex; gap: 10px; align-items: center; margin-top: 1rem; margin-bottom: 1.5rem;">
                <select name="courseId" class="form-control" style="max-width: 400px;" onchange="this.form.submit()">
                    <option value="">-- Select a Course --</option>
                    <c:forEach var="c" items="${enrolledCourses}">
                        <option value="${c.id}" ${selectedCourseId == c.id ? 'selected' : ''}>${c.courseCode} - ${c.courseName}</option>
                    </c:forEach>
                </select>
            </form>

            <c:choose>
                <c:when test="${empty selectedCourseId}">
                    <div style="text-align: center; color: var(--text-muted); padding: 2rem;">
                        Please select a course above to view its groups.
                    </div>
                </c:when>
                <c:when test="${empty groupsData}">
                    <div style="text-align: center; color: var(--text-muted); padding: 2rem;">
                        No groups found for this course.
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="margin-top: 0.5rem;">
                        <c:forEach var="item" items="${groupsData}">
                            <div class="glass-panel" style="padding: 1.5rem; margin-bottom: 1.5rem; background: rgba(255,255,255,0.5);">
                                <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid rgba(0,0,0,0.1); padding-bottom: 1rem; margin-bottom: 1rem;">
                                    <div>
                                        <h4 style="margin: 0; color: var(--primary-color);">${item.group.groupName}</h4>
                                        <span style="font-size: 0.85rem; color: var(--text-muted);">${item.course.courseName} (${item.course.courseCode})</span>
                                    </div>
                                    <div style="text-align: right;">
                                        <div style="font-size: 0.9rem; font-weight: bold;">
                                            ${item.memberCount} / ${item.group.capacity} Members
                                        </div>
                                        <c:choose>
                                            <c:when test="${item.isMember}">
                                                <span class="badge badge-success" style="margin-top: 5px; display: inline-block;">You are a member</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge" style="background: var(--text-muted); color: white; margin-top: 5px; display: inline-block;">Not a member</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                
                                <div>
                                    <strong>Group Members:</strong>
                                    <c:choose>
                                        <c:when test="${not empty item.members}">
                                            <div style="display: flex; flex-wrap: wrap; gap: 0.5rem; margin-top: 10px;">
                                                <c:forEach var="m" items="${item.members}">
                                                    <div class="badge" style="background: ${m.student.id == student.id ? 'var(--primary-light)' : '#e2e8f0'}; color: ${m.student.id == student.id ? 'var(--primary-color)' : 'var(--text-dark)'}; border: 1px solid ${m.student.id == student.id ? 'var(--primary-color)' : '#cbd5e1'};">
                                                        ${m.user.firstName} ${m.user.lastName}
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div style="color: var(--text-muted); margin-top: 5px; font-size: 0.9rem;">No students assigned to this group yet.</div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

</body>
</html>
