<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="reframe.model.beans.Utente" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- CONTROLLO AUTORIZZAZIONI --%>
<%
    // Verifica l'esistenza della sessione e il livello di privilegio. 
    // In assenza del token di sessione o del ruolo adeguato, forza il redirect.
    Utente adminLoggato = (Utente) session.getAttribute("utente");
    if (adminLoggato == null || adminLoggato.getIsAdmin() == 0) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <!-- STRUTTURA PAGINA E HEAD -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Ticket - Backoffice</title>
    
    <link href="https://cdn.jsdelivr.net/npm/remixicon@4.5.0/fonts/remixicon.css" rel="stylesheet" />
    <link rel="stylesheet" href="../css/global.css">
    <link rel="stylesheet" href="../css/variables.css">
    <link rel="stylesheet" href="../css/assistenza.css">
</head>
<body>
    
    <!-- HEADER -->
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <!-- CONTENUTO PRINCIPALE E DASHBOARD -->
    <main class="assistenza-main">
        <div class="assistenza-content">
            
            <h1 class="assistenza-title">
                DASHBOARD <span class="text-accent">
                    <%-- Rendering condizionale JSTL per adattare l'interfaccia utente al livello di privilegio rilevato nel token di sessione --%>
                    <c:choose>
                        <c:when test="${sessionScope.utente.isAdmin == 2}">SUPERADMIN</c:when>
                        <c:otherwise>ADMIN</c:otherwise>
                    </c:choose>
                </span>
            </h1>

            <form action="" method="GET" class="assistenza-search-box">
                <i class="ri-search-2-line search-icon"></i>
                <input type="text" name="query" placeholder="Cerca un ticket o un suo parametro..." value="${param.query}">
                <i class="ri-eraser-line clear-icon" onclick="window.location.href = window.location.pathname;"></i>
            </form>

            <div class="pratiche-results-container">
                
                <div class="pratica-grid-header">
                    <span>MOTIVO TICKET</span>
                    <span>UTENTE</span>
                    <span>
                        <%-- Adattamento dinamico delle colonne della griglia dati per differenziare la vista del Superadmin (gestione assegnazioni) dalla vista Admin standard (gestione stati) --%>
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

    <!-- COMPONENTI ESTERNI E SCRIPT -->
    <jsp:include page="../WEB-INF/components/ticket-overlay.jsp" />
    <jsp:include page="../WEB-INF/components/footer.jsp" />
    <script src="../js/assistenza.js"></script>
</body>
</html>