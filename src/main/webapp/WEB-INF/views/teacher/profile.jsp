<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>My Profile - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/teacher_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">My Profile</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem; max-width: 600px;">
            <c:if test="${param.success}">
                <div style="background: #d1fae5; color: #065f46; padding: 1rem; border-radius: 0.5rem; margin-bottom: 1rem;">
                    Profile updated successfully!
                </div>
            </c:if>
            <form action="${pageContext.request.contextPath}/teacher/profile" method="post">
                <div class="input-group">
                    <label>First Name</label>
                    <input type="text" name="firstName" class="input-field" value="${sessionScope.user.firstName}" readonly style="background: rgba(0,0,0,0.05); cursor: not-allowed;">
                </div>
                
                <div class="input-group">
                    <label>Last Name</label>
                    <input type="text" name="lastName" class="input-field" value="${sessionScope.user.lastName}" readonly style="background: rgba(0,0,0,0.05); cursor: not-allowed;">
                </div>

                <div class="input-group">
                    <label>Specialization</label>
                    <input type="text" class="input-field" value="Computer Science" readonly style="background: rgba(0,0,0,0.05); cursor: not-allowed;">
                </div>

                <div class="input-group">
                    <label>Phone Number</label>
                    <input type="text" name="phone" class="input-field" value="${sessionScope.user.phone}">
                </div>

                <div class="input-group">
                    <label>Email Address</label>
                    <input type="email" name="email" class="input-field" value="${sessionScope.user.email}">
                </div>
                
                <div class="input-group">
                    <label>New Password (Optional)</label>
                    <input type="password" name="password" class="input-field" placeholder="Leave empty to keep current">
                </div>
                
                <button type="submit" class="btn btn-primary">Update Profile</button>
            </form>
        </div>
    </div>
</div>

</body>
</html>
