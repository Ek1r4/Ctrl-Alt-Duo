<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // ==========================================
    // MOCKUP DATI - BACKOFFICE
    // ==========================================
    reframe.model.beans.Utente utenteLoggato = (reframe.model.beans.Utente) session.getAttribute("utente");
    int adminLevel = (utenteLoggato != null) ? utenteLoggato.getIsAdmin() : 1; 
    
    request.setAttribute("adminLevel", adminLevel);
    
    String query = request.getParameter("query");
    if(query == null) query = "";
    query = query.toLowerCase();

    List<Map<String, String>> praticheMock = new ArrayList<>();

    // Pratica 1: Aperta (Visibile SOLO al Superadmin)
    if(adminLevel == 2) {
        Map<String, String> p1 = new HashMap<>();
        p1.put("rma", "RMA-001");
        p1.put("motivo", "Prodotto difettoso all'arrivo, richiede sostituzione");
        p1.put("utente", "cliente_mario");
        p1.put("admin", "Da assegnare");
        p1.put("stato", "Aperta");
        praticheMock.add(p1);
    }

    // Pratica 2: In carico (Visibile a tutti)
    Map<String, String> p2 = new HashMap<>();
    p2.put("rma", "RMA-002");
    p2.put("motivo", "Pacco smarrito dal corriere durante la consegna");
    p2.put("utente", "luigi_99");
    p2.put("admin", "admin_Erika");
    p2.put("stato", "In carico");
    praticheMock.add(p2);

    // Pratica 3: Chiusa (Visibile a tutti)
    Map<String, String> p3 = new HashMap<>();
    p3.put("rma", "RMA-003");
    p3.put("motivo", "Richiesta di reso merce entro 14 giorni");
    p3.put("utente", "sara_fotografia");
    p3.put("admin", "admin_Marco");
    p3.put("stato", "Chiusa");
    praticheMock.add(p3);

    // Barra di Ricerca "Intelligente": filtra per Utente, Stato o Admin
    List<Map<String, String>> risultati = new ArrayList<>();
    for(Map<String, String> p : praticheMock) {
        if(query.isEmpty() || 
           p.get("utente").toLowerCase().contains(query) || 
           p.get("stato").toLowerCase().contains(query) || 
           p.get("admin").toLowerCase().contains(query)) {
            risultati.add(p);
        }
    }
    
    request.setAttribute("pratiche", risultati);
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
                        <c:when test="${adminLevel == 2}">SUPERADMIN</c:when>
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
                            <c:when test="${adminLevel == 2}">IN CARICO A</c:when>
                            <c:otherwise>STATO</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                
                <div class="pratiche-grid">
                    <c:if test="${empty pratiche}">
                        <div class="testo-tecnico" style="text-align:center; padding: 20px;">Nessun ticket trovato con questi filtri.</div>
                    </c:if>

                    <c:forEach var="pratica" items="${pratiche}">
                        <a href="?rma=${pratica.rma}" class="pratica-grid-row">
                            <div class="pratica-titolo">${pratica.motivo}</div>
                            
                            <!-- Colonna Utente -->
                            <div class="pratica-col-center">
                                <i class="ri-user-line" style="margin-right: 5px;"></i>${pratica.utente}
                            </div>
                            
                            <!-- Colonna Dinamica: Stato (Admin) o Admin Assegnato (Superadmin) -->
                            <c:choose>
                                <c:when test="${adminLevel == 2}">
                                    <div class="pratica-col-right admin-badge ${pratica.admin == 'Da assegnare' ? 'badge-urgent-assign' : ''}">
                                        <c:if test="${pratica.admin == 'Da assegnare'}">
                                            <i class="ri-error-warning-line" style="margin-right: 4px;"></i>
                                        </c:if>
                                        ${pratica.admin.toUpperCase()}
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="pratica-col-right status-${pratica.stato.toLowerCase().replace(' ', '-')}">
                                        ${pratica.stato}
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </c:forEach>
                </div>
            </div>
            
        </div>
    </main>

    <jsp:include page="../WEB-INF/components/ticket-overlay.jsp" />
    <jsp:include page="../WEB-INF/components/footer.jsp" />
    <script src="../js/assistenza.js"></script>
</body>
</html>