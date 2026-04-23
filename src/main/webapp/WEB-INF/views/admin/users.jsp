<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>User Management - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">User Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form action="${pageContext.request.contextPath}/admin/users" method="get" style="display:flex; gap:10px; align-items:center;">
                        <input type="text" name="q" class="form-control" placeholder="Search by name or email..." value="${searchQuery}" style="width: 300px;">
                        <button type="submit" class="btn btn-sm btn-primary">Search</button>
                        <c:if test="${not empty searchQuery}">
                            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                        </c:if>
                    </form>
                </div>
                <div class="toolbar-right">
                    <button class="btn btn-primary btn-sm" onclick="openModal('create')">+ Add New User</button>
                </div>
            </div>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>${u.id}</td>
                                <td>${u.firstName} ${u.lastName}</td>
                                <td>${u.email}</td>
                                <td>${u.phone != null ? u.phone : ''}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.roleId == 1}"><span class="badge badge-danger">Admin</span></c:when>
                                        <c:when test="${u.roleId == 2}"><span class="badge badge-warning">Teacher</span></c:when>
                                        <c:when test="${u.roleId == 3}"><span class="badge badge-success">Student</span></c:when>
                                        <c:when test="${u.roleId == 4}"><span class="badge badge-info">Parent</span></c:when>
                                        <c:otherwise><span class="badge">Unknown</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:set var="studentData" value="${studentMap[u.id]}" />
                                    <c:set var="teacherData" value="${teacherMap[u.id]}" />
                                    <c:set var="parentData" value="${parentMap[u.id]}" />
                                    <button class="btn btn-sm btn-primary" 
                                        onclick="openModal('update', ${u.id}, '${u.email}', '${u.firstName}', '${u.lastName}', ${u.roleId}, '${u.phone != null ? u.phone : ''}')"
                                        data-student-number="${studentData != null ? studentData.studentNumber : ''}"
                                        data-date-of-birth="${studentData != null ? studentData.dateOfBirth : ''}"
                                        data-employee-id="${teacherData != null ? teacherData.employeeId : ''}"
                                        data-specialization="${teacherData != null ? teacherData.specialization : ''}"
                                        data-parent-address="${parentData != null ? parentData.address : ''}"
                                        data-parent-occupation="${parentData != null ? parentData.occupation : ''}"
                                        id="editBtn-${u.id}">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteUser(${u.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty users}">
                             <tr>
                                <td colspan="5" style="text-align:center;">No users found.</td>
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

<%-- User Modal --%>
<div id="userModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Add New User</h2>
        <form id="userForm" action="${pageContext.request.contextPath}/admin/users" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="userId">
            
            <div class="form-group">
                <label>First Name</label>
                <input type="text" name="firstName" id="firstName" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Last Name</label>
                <input type="text" name="lastName" id="lastName" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" id="email" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Phone</label>
                <input type="text" name="phone" id="phone" class="form-control">
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" id="password" class="form-control" placeholder="Leave blank to keep current (update only)">
            </div>
            <div class="form-group">
                <label>Role</label>
                <select name="roleId" id="roleId" class="form-control" onchange="toggleRoleFields()">
                    <option value="1">Admin</option>
                    <option value="2">Teacher</option>
                    <option value="3">Student</option>
                    <option value="4">Parent</option>
                </select>
            </div>

            <%-- Student Fields --%>
            <div id="studentFields" style="display:none; border-top: 1px solid #ccc; padding-top: 10px; margin-top: 10px;">
                 <h4>Student Details</h4>
                 <div class="form-group">
                    <label>Student Number</label>
                    <input type="text" name="studentNumber" id="studentNumber" class="form-control">
                </div>
                <div class="form-group">
                    <label>Date of Birth (YYYY-MM-DD)</label>
                    <input type="date" name="dateOfBirth" id="dateOfBirth" class="form-control">
                </div>
            </div>

            <%-- Teacher Fields --%>
            <div id="teacherFields" style="display:none; border-top: 1px solid #ccc; padding-top: 10px; margin-top: 10px;">
                <h4>Teacher Details</h4>
                <div class="form-group">
                    <label>Employee ID</label>
                    <input type="text" name="employeeId" id="employeeId" class="form-control">
                </div>
                <div class="form-group">
                    <label>Specialization</label>
                    <input type="text" name="specialization" id="specialization" class="form-control">
                </div>
            </div>

            <%-- Parent Fields --%>
            <div id="parentFields" style="display:none; border-top: 1px solid #ccc; padding-top: 10px; margin-top: 10px;">
                <h4>Parent Details</h4>
                <div class="form-group">
                    <label>Address</label>
                    <input type="text" name="address" id="address" class="form-control">
                </div>
                <div class="form-group">
                    <label>Occupation</label>
                    <input type="text" name="occupation" id="occupation" class="form-control">
                </div>
            </div>

            <button type="submit" class="btn btn-primary" style="width:100%">Save User</button>
        </form>
    </div>
</div>

<%-- Delete Form --%>
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/users" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script src="${pageContext.request.contextPath}/assets/js/admin/users.js"></script>

</body>
</html>
