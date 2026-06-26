<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Centro Assistenza</title>
    <link
	  href="https://cdn.jsdelivr.net/npm/remixicon@4.5.0/fonts/remixicon.css"
	  rel="stylesheet"
	/>
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
            <div class="assistenza-search-box">
                <i class="ri-search-2-line search-icon"></i>
                <input type="text" placeholder="inserisci il titolo di un ticket...">
                <i class="ri-eraser-line clear-icon"></i>
            </div>

            <!-- Bottoni Ricerca Rapida (4) -->
            <div class="quick-search-buttons">
                <button class="btn-outline">ORDINI</button>
                <button class="btn-outline">PRODOTTI</button>
                <button class="btn-outline">ACCOUNT</button>
                <button class="btn-outline">PAGAMENTI</button>
            </div>
            
        </div>

        <!-- CTA sul fondo (Applica le tue classi globali, es. btn btn-primary) -->
        <div class="assistenza-cta-container">
            <a href="nuovoTicketStep1.jsp" class="btn btn-cta">NUOVO TICKET</a>
        </div>
    </main>

    <jsp:include page="../WEB-INF/components/footer.jsp" />
</body>
</html>