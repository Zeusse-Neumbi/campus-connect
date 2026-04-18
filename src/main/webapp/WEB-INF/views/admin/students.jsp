<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Student Management - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Student Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form action="${pageContext.request.contextPath}/admin/students" method="get" style="display:flex; gap:10px; align-items:center;">
                        <input type="text" name="q" class="form-control" placeholder="Search by name or student number..." value="${searchQuery}">
                        <button type="submit" class="btn btn-sm btn-primary">Search</button>
                        <c:if test="${not empty searchQuery}">
                            <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
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
                            <th>Student ID</th>
                            <th>Name</th>
                            <th>Class/Year</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${students}">
                             <tr>
                                <td>${s.studentNumber}</td>
                                <td>${userMap[s.userId].firstName} ${userMap[s.userId].lastName}</td>
                                <td>Year 1</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/impersonate?userId=${s.userId}" class="btn btn-sm btn-primary">View Dashboard</a>
                                    <button class="btn btn-sm btn-primary" onclick="openModal('update', ${s.id}, ${s.userId}, '${s.studentNumber}', '${s.dateOfBirth}')">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteStudent(${s.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                         <c:if test="${empty students}">
                             <tr>
                                <td colspan="4" style="text-align:center;">No students found.</td>
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

<!-- Student Modal -->
<div id="studentModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Enroll Student</h2>
        <form id="studentForm" action="${pageContext.request.contextPath}/admin/students" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="studentId">
            <input type="hidden" name="userId" id="userId">
            
            <div class="form-group" id="userIdGroup" style="display:none;">
                <label>Linked User ID (Enter ID of existing User)</label>
                <input type="number" id="userIdDisplay" class="form-control" readonly>
                <small style="color: grey;">* Create a User first if not exists</small>
            </div>
            <div class="form-group">
                <label>Student Number</label>
                <input type="text" name="studentNumber" id="studentNumber" class="form-control" required>
            </div>

            <div class="form-group">
                <label>Date of Birth</label>
                <input type="date" name="dateOfBirth" id="dateOfBirth" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Student</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/students" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script src="${pageContext.request.contextPath}/assets/js/admin/students.js"></script>

</body>
</html>
