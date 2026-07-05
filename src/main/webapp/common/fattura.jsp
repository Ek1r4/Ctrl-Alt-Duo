<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Ordine" %>
<%@ page import="reframe.model.beans.DettaglioOrdine" %>

<%-- CONTROLLO VALIDITÀ DATI E INIZIALIZZAZIONE --%>
<%
    // Previene NullPointerException bloccando il rendering della pagina in caso di accesso diretto all'URL senza che l'ordine sia stato preventivamente caricato dalla Servlet nel RequestDispatcher.
    Ordine ordine = (Ordine) request.getAttribute("ordineFattura");
    if (ordine == null) {
        response.sendRedirect(request.getContextPath() + "/404.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <!-- STRUTTURA PAGINA E HEAD -->
    <meta charset="UTF-8">
    <title>Fattura <%= ordine.getIdOrdine() %> | ReFrame</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fattura.css">
</head>
<body>
    
    <div class="no-print">
        <jsp:include page="../WEB-INF/components/header.jsp" />
    </div>

    <!-- LAYOUT DOCUMENTO FATTURA -->
    <main class="invoice-box">
        
        <!-- INTESTAZIONE -->
        <div class="invoice-header">
            <div class="invoice-header-left">
                <h2>REFRAME</h2>
                <p>E-commerce Fotografia Analogica</p>
            </div>
            <div class="invoice-header-right">
                <h1>FATTURA</h1>
                <p>
                    <strong>N°:</strong> <%= ordine.getIdOrdine() %><br>
                    <strong>Data:</strong> <%= ordine.getDataOrdine() %>
                </p>
            </div>
        </div>

        <div class="invoice-recipient">
            <h3>Destinatario:</h3>
            <p><%= ordine.getIdUtente() %></p>
        </div>

        <!-- DETTAGLIO ARTICOLI -->
        <table class="invoice-table">
            <thead>
                <tr>
                    <th>Articolo</th>
                    <th>Prezzo Unitario</th>
                    <th>IVA</th>
                    <th>Q.tà</th>
                    <th>Totale</th>
                </tr>
            </thead>
            <tbody>
                <%-- Iterazione sui dettagli dell'ordine per la generazione dinamica delle righe del documento contabile, mantenendo l'isolamento dei calcoli parziali delegato al bean associato. --%>
                <% for (DettaglioOrdine d : ordine.getDettagli()) { %>
                    <tr>
                        <td><%= d.getNomeProdottoAcquisto() %></td>
                        <td>€ <%= String.format("%.2f", d.getPrezzoAcquisto()) %></td>
                        <td><%= d.getIvaAcquisto() %>%</td>
                        <td><%= d.getQuantitaAcquisto() %></td>
                        <td>€ <%= String.format("%.2f", d.getTotaleRiga()) %></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
        
        <!-- RIEPILOGO TOTALI -->
        <div class="invoice-subtotals">
            Subtotale Prodotti: € <%= String.format("%.2f", ordine.getTotale() - 5.00) %><br>
            Spedizione Standard: € 5.00
        </div>
        
        <div class="invoice-grand-total">
            TOTALE (IVA INCL.): € <%= String.format("%.2f", ordine.getTotale()) %>
        </div>
        
    </main>
    
    <script src="${pageContext.request.contextPath}/js/fattura.js"></script>
</body>
</html>