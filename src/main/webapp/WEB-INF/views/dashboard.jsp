<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Dashboard - School App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <%-- Include Header --%>
    <jsp:include page="/WEB-INF/common/header.jsp"/>

    <div class="main-container">
        <%-- Include Sidebar --%>
        <jsp:include page="/WEB-INF/common/sidebar.jsp"/>

        <div class="content">
            <h1>School Management System</h1>
            <p><strong>Présentation du projet :</strong> Un simple projet de fonctionnement d'un établissement scolaire contenant les modules Utilisateurs, Eleves, Profs, Matieres, Classes.</p>
            
            <h3>Principale :</h3>
            <p>Elle contiendra un menu vertical à gauche avec les titres des différents modules. Le click sur un élément du menu va ouvrir le composant sur la partie droite.</p>

            <img src="#" alt="Main dashboard area" style="border: 2px solid #ccc; width: 600px; height: 300px; background: #eee; display: flex; align-items: center; justify-content: center;">
        </div>
    </div>
</body>
</html>
