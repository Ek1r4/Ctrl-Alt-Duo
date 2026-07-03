<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>

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
            <form action="" method="GET" class="assistenza-search-box">
                <i class="ri-search-2-line search-icon"></i>
                <input type="text" name="query" placeholder="inserisci il titolo di un ticket..." value="">
                 <i class="ri-eraser-line clear-icon"></i>
            </form>

            <!-- Bottoni Ricerca Rapida Ripristinati e Potenziati -->
            <div class="quick-search-buttons">
                <button type="button" class="btn-outline quick-filter" data-filter="ordine">ORDINI</button>
                <button type="button" class="btn-outline quick-filter" data-filter="prodotto">PRODOTTI</button>
                <button type="button" class="btn-outline quick-filter" data-filter="account">ACCOUNT</button>
                <button type="button" class="btn-outline quick-filter" data-filter="pagamento">PAGAMENTI</button>
            </div>

            <!-- Griglia Risultati -->
            <div class="pratiche-results-container" style="display: none;">
                <div class="results-subtitle" style="display: flex; justify-content: space-between; padding: 0 1.5rem;">
                    <span style="flex: 1;">I TUOI TICKET</span>
                    <span style="width: 150px; text-align: left;">DATA APERTURA</span>
                    <span style="width: 150px; text-align: right;">STATO</span>
                </div>
                <!-- Griglia popolata dinamicamente da assistenza.js -->
                <div class="pratiche-grid"></div>
            </div>
        </div>

        <!-- CTA sul fondo per creare un nuovo ticket (Esclusiva per il cliente) -->
        <div class="assistenza-cta-container">
            <a href="nuovoTicketStep1.jsp" class="btn btn-cta">NUOVO TICKET</a>
        </div>
    </main>

    <jsp:include page="../WEB-INF/components/ticket-overlay.jsp" />
    <jsp:include page="../WEB-INF/components/footer.jsp" />
    <script src="../js/assistenza.js"></script>
</body>
</html>