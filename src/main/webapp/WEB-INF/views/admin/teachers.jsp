<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Teacher Management - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Teacher Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form action="${pageContext.request.contextPath}/admin/teachers" method="get" style="display:flex; gap:10px; align-items:center;">
                        <input type="text" name="q" class="form-control" placeholder="Search by name or specialization..." value="${searchQuery}">
                        <button type="submit" class="btn btn-sm btn-primary">Search</button>
                        <c:if test="${not empty searchQuery}">
                            <a href="${pageContext.request.contextPath}/admin/teachers" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                        </c:if>
                    </form>
                </div>
                <div class="toolbar-right">
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary btn-sm">+ Add via User Management</a>
                </div>
            </div>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Employee ID</th>
                            <th>Name</th>
                            <th>Specialization</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="t" items="${teachers}">
                            <tr>
                                <td>${t.employeeId}</td>
                                <td>${userMap[t.userId].firstName} ${userMap[t.userId].lastName}</td>
                                <td>${t.specialization}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/impersonate?userId=${t.userId}" class="btn btn-sm btn-primary">View Dashboard</a>
                                    <button class="btn btn-sm btn-primary" onclick="openModal('update', ${t.id}, ${t.userId}, '${t.employeeId}', '${t.specialization}')">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteTeacher(${t.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty teachers}">
                             <tr>
                                <td colspan="4" style="text-align:center;">No teachers found.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <tags:pagination currentPage="${currentPage}" totalPages="${totalPages}" queryString="&q=${searchQuery}" />
        </div>
    </div>
</div>

<!-- Teacher Modal -->
<div id="teacherModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Add Teacher</h2>
        <form id="teacherForm" action="${pageContext.request.contextPath}/admin/teachers" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="teacherId">
            <input type="hidden" name="userId" id="userId">
            <div class="form-group">
                <label>Employee ID</label>
                <input type="text" name="employeeId" id="employeeId" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Specialization</label>
                <input type="text" name="specialization" id="specialization" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Teacher</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/teachers" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script src="${pageContext.request.contextPath}/assets/js/admin/teachers.js"></script>

</body>
</html>
