<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Ordine" %>

<%-- CONTROLLO VALIDITÀ DATI E REDIRECT --%>
<%
    // Previene NullPointerException e blocca l'accesso diretto alla view: verifica la presenza dell'oggetto Ordine instradato dalla Servlet post-checkout. In assenza, forza il redirect alla home.
    Ordine ordine = (Ordine) request.getAttribute("ordineEffettuato");
    if (ordine == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <!-- STRUTTURA PAGINA E HEAD -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grazie per l'acquisto | ReFrame</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkout.css">
</head>
<body>
    
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <!-- LAYOUT CONFERMA ORDINE -->
    <div class="auth-wrapper grazie-wrapper">
        <div class="film-container grazie-container">
            
            <div class="camera-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="rgb(61, 116, 111)" stroke-linecap="round" stroke-linejoin="round" class="grazie-icon-svg">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
            </div>
    
            <h1 class="form-title grazie-title">ACQUISTO COMPLETATO</h1>
            <p class="grazie-subtitle">Soggetto perfettamente a fuoco.</p>
            
            <div class="ordine-recap">
                <p><strong>N° Ordine:</strong> <%= ordine.getIdOrdine() %></p>
                <p><strong>Data:</strong> <%= ordine.getDataOrdine() %></p>
                <p><strong>Totale Pagato:</strong> € <%= String.format("%.2f", ordine.getTotale()) %></p>
                <p><strong>Stato:</strong> In Lavorazione</p>
            </div>

            <p class="denied-text">
                Grazie per aver scelto ReFrame. Abbiamo inviato una mail di conferma con tutti i dettagli.
            </p>

            <%-- Inclusione dinamica del componente fattura con passaggio del parametro identificativo dell'ordine appena evaso --%>
            <jsp:include page="../WEB-INF/components/btn-fattura.jsp">
                <jsp:param name="idOrdine" value="<%= ordine.getIdOrdine() %>" />
            </jsp:include>
            
            <br>

            <a href="${pageContext.request.contextPath}/ProdottoServlet?search=" class="btn-home">Torna alla Vetrina</a>
        </div>
    </div>

    <jsp:include page="../WEB-INF/components/footer.jsp" />
    
</body>
</html>