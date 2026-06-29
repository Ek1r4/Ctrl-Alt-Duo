<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // ==========================================
    // MOCKUP DATI - Dinamico in base alla ricerca
    // ==========================================
    
    // Leggiamo i parametri inviati dal form
    String query = request.getParameter("query");
    String filtro = request.getParameter("filtro");
    
    // La ricerca è considerata effettuata SOLO se c'è del testo nella barra o se è stato cliccato un filtro
    boolean ricercaEffettuata = (query != null && !query.trim().isEmpty()) || 
                                (filtro != null && !filtro.trim().isEmpty());
    
    List<Map<String, String>> praticheMock = new ArrayList<>();
    
    // Riempiamo la lista SOLO se l'utente ha cercato qualcosa
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
    // ==========================================
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

            <!-- Barra di ricerca -->
            <form action="" method="GET" class="assistenza-search-box">
                <i class="ri-search-2-line search-icon"></i>
                <input type="text" name="query" placeholder="inserisci il titolo di un ticket..." value="${param.query}">
                 <i class="ri-eraser-line clear-icon" onclick="window.location.href = window.location.pathname;"></i>
            </form>

            <c:choose>
                <c:when test="${ricercaEffettuata}">
                    <!-- RISULTATI RICERCA (GRID) -->
                    <div class="pratiche-results-container">
                        <p class="results-subtitle">"Risultati per la tua ricerca"</p>
                        
                        <div class="pratiche-grid">
                            <!-- Header Grid (opzionale, lo ometto per un look più pulito come da tuo sketch, ma possiamo aggiungerlo) -->
                            
                            <c:forEach var="pratica" items="${pratiche}">
                                <!-- Passiamo l'RMA in GET per aprire in seguito l'overlay -->
                                <a href="?rma=${pratica.rma}" class="pratica-grid-row">
                                    <div class="pratica-titolo">${pratica.motivo}</div>
                                    <div class="pratica-data">${pratica.data}</div>
                                    <div class="pratica-stato status-${pratica.stato.toLowerCase().replace(' ', '-')}">
                                        ${pratica.stato}
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Bottoni Ricerca Rapida (Mostrati solo se non c'è una ricerca attiva) -->
                    <div class="quick-search-buttons">
                        <button class="btn-outline">ORDINI</button>
                        <button class="btn-outline">PRODOTTI</button>
                        <button class="btn-outline">ACCOUNT</button>
                        <button class="btn-outline">PAGAMENTI</button>
                    </div>
                </c:otherwise>
            </c:choose>
            
        </div>

        <!-- CTA sul fondo -->
        <div class="assistenza-cta-container">
            <a href="nuovoTicketStep1.jsp" class="btn btn-cta">NUOVO TICKET</a>
        </div>
    </main>
	<jsp:include page="../WEB-INF/components/ticket-overlay.jsp" />
    <jsp:include page="../WEB-INF/components/footer.jsp" />
    <script src="../js/assistenza.js"></script>
</body>
</html>