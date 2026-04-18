<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Login - School Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/login.css">
</head>
<body class="login-body">

    <div class="login-glass-card glass-panel">
        <h2 style="color: var(--primary-color); margin-bottom: 0.5rem;">Welcome Back</h2>
        <p style="color: var(--text-muted); margin-bottom: 2rem;">Sign in to continue to your dashboard</p>

        <c:if test="${not empty error}">
            <div style="background: rgba(239, 68, 68, 0.1); color: var(--danger-color); padding: 10px; border-radius: 8px; margin-bottom: 20px; font-weight: 500;">
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="input-group">
                <label>Email Address</label>
                <input type="email" name="email" class="input-field" placeholder="Enter your email" required>
            </div>
            
            <div class="input-group">
                <label>Password</label>
                <input type="password" name="password" class="input-field" placeholder="Enter your password" required>
            </div>
            
            <button type="submit" class="btn btn-primary" style="width: 100%;">Sign In</button>
            
            <div style="margin-top: 20px;">
                <a href="#" style="font-size: 0.9rem;">Forgot Password?</a>
            </div>
        </form>
    </div>

</body>
</html>
