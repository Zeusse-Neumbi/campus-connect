<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Student-Parent Links - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Student-Parent Links</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form action="${pageContext.request.contextPath}/admin/student-parents" method="get" style="display:flex; gap:10px; align-items:center;">
                        <input type="text" name="q" class="form-control" placeholder="Search by student, parent or relationship..." value="${searchQuery}">
                        <button type="submit" class="btn btn-sm btn-primary">Search</button>
                        <c:if test="${not empty searchQuery}">
                            <a href="${pageContext.request.contextPath}/admin/student-parents" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                        </c:if>
                    </form>
                </div>
                <div class="toolbar-right">
                    <button class="btn btn-primary btn-sm" onclick="openModal()">+ Link Student & Parent</button>
                </div>
            </div>

            <div class="table-container">
                <table>
                    <thead><tr><th>Student</th><th>Student Number</th><th>Parent</th><th>Relationship</th><th>Actions</th></tr></thead>
                    <tbody>
                        <c:forEach var="link" items="${links}">
                            <c:set var="student" value="${studentMap[link.studentId]}" />
                            <c:set var="parent" value="${parentMap[link.parentId]}" />
                            <c:set var="sUser" value="${student != null ? userMap[student.userId] : null}" />
                            <c:set var="pUser" value="${parent != null ? userMap[parent.userId] : null}" />
                            <tr>
                                <td>${sUser != null ? sUser.firstName : '?'} ${sUser != null ? sUser.lastName : ''}</td>
                                <td>${student != null ? student.studentNumber : '-'}</td>
                                <td>${pUser != null ? pUser.firstName : '?'} ${pUser != null ? pUser.lastName : ''}</td>
                                <td><span class="badge badge-info">${link.relationship}</span></td>
                                <td><button class="btn btn-sm btn-danger" onclick="unlinkPair(${link.studentId}, ${link.parentId})">Remove</button></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty links}">
                            <tr><td colspan="5" style="text-align:center;">No links found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <tags:pagination currentPage="${currentPage}" totalPages="${totalPages}" queryString="&q=${searchQuery}" />
        </div>
    </div>
</div>

<!-- Link Modal -->
<div id="linkModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2>Link Student & Parent</h2>
        <form action="${pageContext.request.contextPath}/admin/student-parents" method="post">
            <input type="hidden" name="action" value="link">
            <div class="form-group">
                <label>Student</label>
                <select name="studentId" class="form-control" required>
                    <option value="">-- Select Student --</option>
                    <c:forEach var="s" items="${students}">
                        <c:set var="sUser" value="${userMap[s.userId]}" />
                        <option value="${s.id}">${sUser != null ? sUser.firstName : ''} ${sUser != null ? sUser.lastName : ''} (${s.studentNumber})</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>Parent</label>
                <select name="parentId" class="form-control" required>
                    <option value="">-- Select Parent --</option>
                    <c:forEach var="p" items="${parents}">
                        <c:set var="pUser" value="${userMap[p.userId]}" />
                        <option value="${p.id}">${pUser != null ? pUser.firstName : ''} ${pUser != null ? pUser.lastName : ''}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>Relationship</label>
                <select name="relationship" class="form-control" required>
                    <option value="Mother">Mother</option>
                    <option value="Father">Father</option>
                    <option value="Guardian">Guardian</option>
                    <option value="Step-parent">Step-parent</option>
                    <option value="Aunt">Aunt</option>
                    <option value="Uncle">Uncle</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Create Link</button>
        </form>
    </div>
</div>

<!-- Unlink Form -->
<form id="unlinkForm" action="${pageContext.request.contextPath}/admin/student-parents" method="post" style="display:none;">
    <input type="hidden" name="action" value="unlink">
    <input type="hidden" name="studentId" id="unlinkStudentId">
    <input type="hidden" name="parentId" id="unlinkParentId">
</form>

<script src="${pageContext.request.contextPath}/assets/js/admin/student-parents.js"></script>

</body>
</html>
