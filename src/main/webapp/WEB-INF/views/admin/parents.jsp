<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Parents - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Parent Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form action="${pageContext.request.contextPath}/admin/parents" method="get" style="display:flex; gap:10px; align-items:center;">
                        <input type="text" name="q" class="form-control" placeholder="Search by name, email, occupation..." value="${searchQuery}">
                        <button type="submit" class="btn btn-sm btn-primary">Search</button>
                        <c:if test="${not empty searchQuery}">
                            <a href="${pageContext.request.contextPath}/admin/parents" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                        </c:if>
                    </form>
                </div>
                <div class="toolbar-right">
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary btn-sm">+ Add via User Management</a>
                </div>
            </div>

            <div class="table-container">
                <table>
                    <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Address</th><th>Occupation</th><th>Actions</th></tr></thead>
                    <tbody>
                        <c:forEach var="parent" items="${parents}">
                            <c:set var="user" value="${userMap[parent.userId]}" />
                            <tr>
                                <td>${parent.id}</td>
                                <td>${user != null ? user.firstName : '?'} ${user != null ? user.lastName : ''}</td>
                                <td>${user != null ? user.email : '-'}</td>
                                <td>${parent.address != null ? parent.address : '-'}</td>
                                <td>${parent.occupation != null ? parent.occupation : '-'}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/impersonate?userId=${parent.userId}" class="btn btn-sm btn-primary">View Portal</a>
                                    <button class="btn btn-sm btn-primary" onclick="openModal(${parent.id}, ${parent.userId}, '${parent.address}', '${parent.occupation}')">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteParent(${parent.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty parents}">
                            <tr><td colspan="6" style="text-align:center;">No parents found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <tags:pagination currentPage="${currentPage}" totalPages="${totalPages}" queryString="&q=${searchQuery}" />
        </div>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/parents" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<!-- Edit Parent Modal -->
<div id="parentModal" class="modal">
    <div class="modal-content" style="max-width: 500px;">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Edit Parent Profile</h2>
        <form id="parentForm" action="${pageContext.request.contextPath}/admin/parents" method="post">
            <input type="hidden" name="action" id="formAction" value="update">
            <input type="hidden" name="id" id="parentId">
            <input type="hidden" name="userId" id="userId">
            <div class="form-group"><label>Address</label><input type="text" name="address" id="address" class="form-control" required></div>
            <div class="form-group"><label>Occupation</label><input type="text" name="occupation" id="occupation" class="form-control"></div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Changes</button>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/admin/parents.js"></script>

</body>
</html>
