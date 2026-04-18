<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Course Groups - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Course Group Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form action="${pageContext.request.contextPath}/admin/course-groups" method="get" style="display:flex; gap:10px; align-items:center;">
                        <input type="text" name="q" class="form-control" placeholder="Search by group or course name..." value="${searchQuery}">
                        <button type="submit" class="btn btn-sm btn-primary">Search</button>
                        <c:if test="${not empty searchQuery}">
                            <a href="${pageContext.request.contextPath}/admin/course-groups" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                        </c:if>
                    </form>
                </div>
                <div class="toolbar-right">
                    <button class="btn btn-primary btn-sm" onclick="openModal('create')">+ Add Group</button>
                </div>
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Course</th>
                            <th>Group Name</th>
                            <th>Capacity</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="g" items="${courseGroups}">
                            <c:set var="course" value="${courseMap[g.courseId]}" />
                            <tr>
                                <td>${g.id}</td>
                                <td>${course != null ? course.courseCode : 'Unknown'} - ${course != null ? course.courseName : ''}</td>
                                <td>${g.groupName}</td>
                                <td>${g.capacity}</td>
                                <td>
                                    <button class="btn btn-sm btn-primary" onclick="openModal('update', ${g.id}, ${g.courseId}, '${g.groupName}', ${g.capacity})">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteGroup(${g.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty courseGroups}">
                            <tr><td colspan="5" style="text-align:center;">No course groups found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <tags:pagination currentPage="${currentPage}" totalPages="${totalPages}" queryString="&q=${searchQuery}" />
        </div>
    </div>
</div>

<!-- Course Group Modal -->
<div id="groupModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Add New Course Group</h2>
        <form id="groupForm" action="${pageContext.request.contextPath}/admin/course-groups" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="groupId">
            
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
                <label>Group Name</label>
                <input type="text" name="groupName" id="groupName" class="form-control" placeholder="e.g. Group A" required>
            </div>
            <div class="form-group">
                <label>Capacity</label>
                <input type="number" name="capacity" id="capacity" class="form-control" min="1" placeholder="25" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Group</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/course-groups" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script src="${pageContext.request.contextPath}/assets/js/admin/course-groups.js"></script>

</body>
</html>
