<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Classrooms - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Classroom Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="page-toolbar">
                <div class="toolbar-left">
                    <form action="${pageContext.request.contextPath}/admin/classrooms" method="get" style="display:flex; gap:10px; align-items:center;">
                        <span style="font-weight:bold;">Filter Building:</span>
                        <select name="buildings" class="form-control">
                            <option value="Main Building" ${fn:contains(paramValues.buildings, 'Main Building') ? 'selected' : ''}>Main Building</option>
                            <option value="Engineering Hall" ${fn:contains(paramValues.buildings, 'Engineering Hall') ? 'selected' : ''}>Engineering Hall</option>
                            <option value="Science Block" ${fn:contains(paramValues.buildings, 'Science Block') ? 'selected' : ''}>Science Block</option>
                            <option value="Arts Wing" ${fn:contains(paramValues.buildings, 'Arts Wing') ? 'selected' : ''}>Arts Wing</option>
                        </select>
                        <span style="font-weight:bold; margin-left: 10px;">Min Capacity:</span>
                        <input type="number" name="minCapacity" class="form-control" style="width: 80px;" value="${param.minCapacity}">
                        <button type="submit" class="btn btn-sm btn-primary">Filter</button>
                        <c:if test="${not empty param.buildings or not empty param.minCapacity}">
                            <a href="${pageContext.request.contextPath}/admin/classrooms" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                        </c:if>
                    </form>
                </div>
                <div class="toolbar-right">
                    <button class="btn btn-primary btn-sm" onclick="openModal('create')">+ Add Classroom</button>
                </div>
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Room Code</th>
                            <th>Building</th>
                            <th>Capacity</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cr" items="${classrooms}">
                            <tr>
                                <td>${cr.id}</td>
                                <td>${cr.roomCode}</td>
                                <td>${cr.building}</td>
                                <td>${cr.capacity}</td>
                                <td>
                                    <button class="btn btn-sm btn-primary" onclick="openModal('update', ${cr.id}, '${cr.roomCode}', '${cr.building}', ${cr.capacity})">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteClassroom(${cr.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty classrooms}">
                            <tr><td colspan="5" style="text-align:center;">No classrooms found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <tags:pagination currentPage="${currentPage}" totalPages="${totalPages}" queryString="&q=${searchQuery}" />
        </div>
    </div>
</div>

<!-- Classroom Modal -->
<div id="classroomModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Add New Classroom</h2>
        <form id="classroomForm" action="${pageContext.request.contextPath}/admin/classrooms" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="classroomId">
            
            <div class="form-group">
                <label>Building</label>
                <select name="building" id="building" class="form-control" required onchange="generateRoomCode()">
                    <option value="">-- Select --</option>
                    <option value="Main Building">Main Building</option>
                    <option value="Science Block">Science Block</option>
                    <option value="Arts Center">Arts Center</option>
                </select>
            </div>
            <div style="display:flex; gap: 10px;">
                <div class="form-group" style="flex:1;">
                    <label>Floor (1-9)</label>
                    <input type="number" id="floor" min="1" max="9" class="form-control" oninput="generateRoomCode()" placeholder="e.g. 1" required>
                </div>
                <div class="form-group" style="flex:1;">
                    <label>Room No. (01-99)</label>
                    <input type="number" id="roomNo" min="1" max="99" class="form-control" oninput="generateRoomCode()" placeholder="e.g. 5" required>
                </div>
            </div>
            <div class="form-group">
                <label>Assigned Room Code</label>
                <input type="text" name="roomCode" id="roomCode" class="form-control" readonly required>
            </div>
            <div class="form-group">
                <label>Capacity</label>
                <input type="number" name="capacity" id="capacity" class="form-control" min="1" placeholder="30" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Classroom</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/classrooms" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script src="${pageContext.request.contextPath}/assets/js/admin/classrooms.js"></script>

</body>
</html>
