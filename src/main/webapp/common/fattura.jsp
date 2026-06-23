<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Ordine" %>
<%@ page import="reframe.model.beans.DettaglioOrdine" %>
<%
    Ordine ordine = (Ordine) request.getAttribute("ordineFattura");
    if (ordine == null) {
        response.sendRedirect(request.getContextPath() + "/404.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Fattura <%= ordine.getIdOrdine() %> | ReFrame</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <style>
        .invoice-box { max-width: 800px; margin: 2rem auto; padding: 2rem; border: 2px solid var(--antracite-scuro); background: var(--panna-carta); font-family: var(--font-tecnico); }
        .invoice-header { display: flex; justify-content: space-between; border-bottom: 2px solid var(--antracite-scuro); padding-bottom: 1rem; margin-bottom: 2rem; }
        .invoice-table { width: 100%; border-collapse: collapse; margin-top: 1rem; border: 1px solid var(--antracite-scuro); }
        .invoice-table th, .invoice-table td { border: 1px solid var(--grigio-taupe); padding: 0.8rem; text-align: left; }
        .invoice-table th { background: rgba(0,0,0,0.05); }
        
        /* IL MIRACOLO DELLA CHECKLIST: Si attiva solo quando si stampa/salva in PDF */
        @media print {
            .no-print, header, footer, .btn-print { display: none !important; }
            body { background: #fff; margin: 0; padding: 0; }
            .invoice-box {  max-width: 100%; }
        }
    </style>
</head>
<body>
    <div class="no-print">
        <jsp:include page="../WEB-INF/components/header.jsp" />
    </div>


    <main class="invoice-box">
        <div class="invoice-header">
            <div>
                <h2 style="margin:0;">REFRAME</h2>
                <p>E-commerce Fotografia Analogica</p>
            </div>
            <div style="text-align: right;">
                <h1 style="margin:0;">FATTURA</h1>
                <p><strong>N°:</strong> <%= ordine.getIdOrdine() %><br>
                   <strong>Data:</strong> <%= ordine.getDataOrdine() %></p>
            </div>
        </div>

        <div style="margin-bottom: 2rem; display: flex; align-items: baseline; justify-content: space-between;">
            <h3 style="margin: 0;">Destinatario:</h3>
            <p style="margin: 0;"><%= ordine.getIdUtente() %></p>
        </div>

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

        <div style="text-align: right; margin-top: 2rem; font-size: 1.2rem; font-weight: bold;">
            TOTALE (IVA INCL.): € <%= String.format("%.2f", ordine.getTotale()) %>
        </div>
    </main>
    
    
    <script>
        // Triggera automaticamente la stampa/salvataggio appena la pagina è caricata nell'iframe
        window.onload = function() {
            window.print();
        };
    </script>
</body>
</html>