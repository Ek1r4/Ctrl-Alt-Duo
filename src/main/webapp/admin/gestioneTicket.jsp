<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="reframe.model.beans.Utente" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // Protezione della rotta
    Utente adminLoggato = (Utente) session.getAttribute("utente");
    if (adminLoggato == null || adminLoggato.getIsAdmin() == 0) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Ticket - Backoffice</title>
    <link href="https://cdn.jsdelivr.net/npm/remixicon@4.5.0/fonts/remixicon.css" rel="stylesheet" />
    <!-- I percorsi hanno ../ perché siamo dentro /admin/ -->
    <link rel="stylesheet" href="../css/global.css">
    <link rel="stylesheet" href="../css/variables.css">
    <link rel="stylesheet" href="../css/assistenza.css">
</head>
<body>
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <main class="assistenza-main">
        <div class="assistenza-content">
            
            <h1 class="assistenza-title">
                DASHBOARD <span class="text-accent">
                    <c:choose>
                        <c:when test="${sessionScope.utente.isAdmin == 2}">SUPERADMIN</c:when>
                        <c:otherwise>ADMIN</c:otherwise>
                    </c:choose>
                </span>
            </h1>

            <!-- Barra di ricerca "Intelligente" -->
            <form action="" method="GET" class="assistenza-search-box">
                <i class="ri-search-2-line search-icon"></i>
                <input type="text" name="query" placeholder="Cerca un ticket o un suo parametro..." value="${param.query}">
                <i class="ri-eraser-line clear-icon" onclick="window.location.href = window.location.pathname;"></i>
            </form>

            <!-- RISULTATI RICERCA (GRID SEMPRE VISIBILE) -->
            <div class="pratiche-results-container">
                
                <!-- Intestazione colonne allineata dinamicamente con CSS Grid -->
                <div class="pratica-grid-header">
                    <span>MOTIVO TICKET</span>
                    <span>UTENTE</span>
                    <span>
                        <c:choose>
                            <c:when test="${sessionScope.utente.isAdmin == 2}">IN CARICO A</c:when>
                            <c:otherwise>STATO</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                
                <div class="pratiche-grid"> </div>
            </div>
            
        </div>
    </main>

    <jsp:include page="../WEB-INF/components/ticket-overlay.jsp" />
    <jsp:include page="../WEB-INF/components/footer.jsp" />
    <script src="../js/assistenza.js"></script>
</body>
</html>