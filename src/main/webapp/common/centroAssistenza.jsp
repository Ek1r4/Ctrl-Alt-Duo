<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="reframe.model.beans.Utente" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // ==========================================
    // MOCKUP DATI - LATO CLIENTE
    // ==========================================
    
    String query = request.getParameter("query");
    String filtro = request.getParameter("filtro");
    
    boolean ricercaEffettuata = (query != null && !query.trim().isEmpty()) || 
                                (filtro != null && !filtro.trim().isEmpty());
    
    List<Map<String, String>> praticheMock = new ArrayList<>();
    
    if(ricercaEffettuata) {
        Map<String, String> p1 = new HashMap<>();
        p1.put("rma", "RMA-001");
        p1.put("motivo", "Prodotto difettoso all'arrivo");
        p1.put("data", "24/06/2026");
        p1.put("stato", "Aperta");
        praticheMock.add(p1);

        Map<String, String> p2 = new HashMap<>();
        p2.put("rma", "RMA-002");
        p2.put("motivo", "Pacco smarrito dal corriere");
        p2.put("data", "20/06/2026");
        p2.put("stato", "In carico");
        praticheMock.add(p2);

        Map<String, String> p3 = new HashMap<>();
        p3.put("rma", "RMA-003");
        p3.put("motivo", "Richiesta di reso merce");
        p3.put("data", "15/05/2026");
        p3.put("stato", "Chiusa");
        praticheMock.add(p3);
    }
    
    request.setAttribute("pratiche", praticheMock);
    request.setAttribute("ricercaEffettuata", ricercaEffettuata);
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Centro Assistenza</title>
    <link href="https://cdn.jsdelivr.net/npm/remixicon@4.5.0/fonts/remixicon.css" rel="stylesheet" />
    <link rel="stylesheet" href="../css/global.css">
    <link rel="stylesheet" href="../css/variables.css">
    <link rel="stylesheet" href="../css/assistenza.css">
</head>
<body>
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <main class="assistenza-main">
        <div class="assistenza-content">
            
            <!-- Saluto Centrato -->
            <h1 class="assistenza-title">
                CIAO, <span class="text-accent">${not empty sessionScope.utente ? sessionScope.utente.nome : 'UTENTE'}</span><br>
                SIAMO LIETI DI AIUTARTI
            </h1>

            <!-- Barra di ricerca Cliente -->
            <form action="<%= request.getContextPath() %>/AssistenzaServlet" method="GET" class="assistenza-search-box">
            	<input type="hidden" name="action" value="cerca">
                <i class="ri-search-2-line search-icon"></i>
                <input type="text" name="query" placeholder="inserisci il titolo di un ticket..." value="${param.query}">
                 <i class="ri-eraser-line clear-icon" onclick="window.location.href = window.location.pathname;"></i>
            </form>

            <c:choose>
                <c:when test="${ricercaEffettuata}">
                    <!-- RISULTATI RICERCA (GRID CLIENTE) -->
                    <div class="pratiche-results-container">
                        
                        <!-- Intestazione colonne allineata al backoffice -->
                        <div class="results-subtitle" style="display: flex; justify-content: space-between; padding: 0 1.5rem;">
                            <span style="flex: 1;">I TUOI TICKET</span>
                            <span style="width: 150px; text-align: left;">DATA APERTURA</span>
                            <span style="width: 150px; text-align: right;">STATO</span>
                        </div>
                        
                        <div class="pratiche-grid">
                            <c:forEach var="pratica" items="${pratiche}">
                                <a href="?rma=${pratica.rma}" class="pratica-grid-row">
                                    <div class="pratica-titolo">${pratica.motivo}</div>
                                    
                                    <!-- Nuove classi generiche CSS -->
                                    <div class="pratica-col-center">${pratica.data}</div>
                                    <div class="pratica-col-right status-${pratica.stato.toLowerCase().replace(' ', '-')}">
                                        ${pratica.stato}
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Bottoni Ricerca Rapida (Esclusivi per il cliente) -->
                    <div class="quick-search-buttons">
    			<a href="<%= request.getContextPath() %>/AssistenzaServlet?action=cerca&categoria=Ordine" class="btn-outline">ORDINI</a>
    			<a href="<%= request.getContextPath() %>/AssistenzaServlet?action=cerca&categoria=Prodotto" class="btn-outline">PRODOTTI</a>
    			<a href="<%= request.getContextPath() %>/AssistenzaServlet?action=cerca&categoria=Account" class="btn-outline">ACCOUNT</a>
    			<a href="<%= request.getContextPath() %>/AssistenzaServlet?action=cerca&categoria=Pagamento" class="btn-outline">PAGAMENTI</a>
					</div>
                </c:otherwise>
            </c:choose>
            
        </div>

        <!-- CTA sul fondo per creare un nuovo ticket (Esclusiva per il cliente) -->
        <% if (session.getAttribute("utente") != null && ((Utente)session.getAttribute("utente")).getIsAdmin() == 0) { %>
    		<div class="assistenza-cta-container">
        		<a href="nuovoTicketStep1.jsp" class="btn btn-cta">NUOVO TICKET</a>
    		</div>
		<% } %>
    </main>

    <jsp:include page="../WEB-INF/components/ticket-overlay.jsp" />
    <jsp:include page="../WEB-INF/components/footer.jsp" />
    <script src="../js/assistenza.js"></script>
</body>
</html>