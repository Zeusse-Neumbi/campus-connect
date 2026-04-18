<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>My Profile - School Management</title>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/parent_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">My Profile</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem; max-width: 600px;">
            <c:if test="${not empty success}">
                <div style="background: #dcfce7; color: #166534; padding: 10px; border-radius: 5px; margin-bottom: 15px;">${success}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div style="background: #fee2e2; color: #991b1b; padding: 10px; border-radius: 5px; margin-bottom: 15px;">${error}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/parent/profile" method="post">
                <div class="input-group"><label>First Name</label><input type="text" name="firstName" class="input-field" value="${sessionScope.user.firstName}" readonly style="background: rgba(0,0,0,0.05); cursor: not-allowed;"></div>
                <div class="input-group"><label>Last Name</label><input type="text" name="lastName" class="input-field" value="${sessionScope.user.lastName}" readonly style="background: rgba(0,0,0,0.05); cursor: not-allowed;"></div>
                <div class="input-group"><label>Email Address</label><input type="email" name="email" class="input-field" value="${sessionScope.user.email}"></div>
                <div class="input-group"><label>Phone Number</label><input type="text" name="phone" class="input-field" value="${sessionScope.user.phone}"></div>
                <div class="input-group"><label>Occupation</label><input type="text" name="occupation" class="input-field" value="${parent.occupation}"></div>
                <div class="input-group"><label>Address</label><input type="text" name="address" class="input-field" value="${parent.address}" readonly style="background: rgba(0,0,0,0.05); cursor: not-allowed;"></div>
                <div class="input-group"><label>New Password (Optional)</label><input type="password" name="password" class="input-field" placeholder="Leave empty to keep current"></div>
                <button type="submit" class="btn btn-primary" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9);">Update Profile</button>
            </form>
        </div>
    </div>
</div>

</body>
</html>
