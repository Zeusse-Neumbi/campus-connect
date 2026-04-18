<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar glass-panel">
    <div class="sidebar-logo">
        <span style="font-size: 2rem;">👨‍👩‍👧</span> Parent Portal
    </div>
    <ul class="nav-links">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/parent/dashboard" class="${pageContext.request.requestURI.endsWith('/dashboard.jsp') ? 'active' : ''}">
                Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/parent/children" class="${pageContext.request.requestURI.endsWith('/children.jsp') ? 'active' : ''}">
                My Children
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/parent/profile" class="${pageContext.request.requestURI.endsWith('/profile.jsp') ? 'active' : ''}">
                Profile
            </a>
        </li>
        <li class="nav-item" style="margin-top: auto;">
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--danger-color);">
                Logout
            </a>
        </li>
    </ul>
</div>
