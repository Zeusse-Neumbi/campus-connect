<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Course Management - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Course Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form action="${pageContext.request.contextPath}/admin/courses" method="get" style="display:flex; gap:10px; align-items:center;">
                        <input type="text" name="q" class="form-control" placeholder="Search by name, code or teacher..." value="${searchQuery}">
                        <button type="submit" class="btn btn-sm btn-primary">Search</button>
                        <c:if test="${not empty searchQuery}">
                            <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                        </c:if>
                    </form>
                </div>
                <div class="toolbar-right">
                    <button class="btn btn-primary btn-sm" onclick="openModal('create')">+ Create Course</button>
                </div>
            </div>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Name</th>
                            <th>Credits</th>
                            <th>Teacher</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${courses}">
                            <tr>
                                <td>${c.courseCode}</td>
                                <td>${c.courseName}</td>
                                <td>${c.credits}</td>
                                <td>
                                    <c:set var="teacherObj" value="${teacherMap[c.teacherId]}" />
                                    <c:if test="${not empty teacherObj}">
                                        ${userMap[teacherObj.userId].firstName} ${userMap[teacherObj.userId].lastName}
                                    </c:if>
                                    <c:if test="${empty teacherObj}">
                                        Unknown (ID: ${c.teacherId})
                                    </c:if>
                                </td>
                                <td>
                                    <button class="btn btn-sm btn-primary" onclick="openModal('update', ${c.id}, '${c.courseName}', '${c.courseCode}', ${c.teacherId}, ${c.credits})">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteCourse(${c.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty courses}">
                             <tr>
                                <td colspan="5" style="text-align:center;">No courses found.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- Pagination --%>
            <tags:pagination currentPage="${currentPage}" totalPages="${totalPages}" queryString="&q=${searchQuery}" />
        </div>
    </div>
</div>

<%-- Course Modal --%>
<div id="courseModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Create Course</h2>
        <form id="courseForm" action="${pageContext.request.contextPath}/admin/courses" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="courseId">
            
            <div class="form-group">
                <label>Course Name</label>
                <input type="text" name="courseName" id="courseName" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Course Code</label>
                <input type="text" name="courseCode" id="courseCode" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Credits</label>
                <input type="number" name="credits" id="credits" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Assigned Teacher</label>
                <select name="teacherId" id="teacherId" class="form-control" required>
                    <c:if test="${empty teachers}">
                        <option value="" disabled selected>No teachers available - create one first</option>
                    </c:if>
                    <c:forEach var="t" items="${teachers}">
                        <c:set var="tUser" value="${userMap[t.userId]}" />
                        <option value="${t.id}">${tUser.firstName} ${tUser.lastName} (${t.specialization})</option>
                    </c:forEach>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Course</button>
        </form>
    </div>
</div>

<%-- Delete Form --%>
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/courses" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script src="${pageContext.request.contextPath}/assets/js/admin/courses.js"></script>

</body>
</html>
