<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Carrello" %>
<%@ page import="reframe.model.beans.CarrelloItem" %>
<%@ page import="reframe.model.beans.Utente" %>
<%
    // Controllo di sicurezza: se l'utente non è loggato o il carrello è vuoto, 
    // lo reindirizziamo senza fargli caricare l'interfaccia.
    Utente utente = (Utente) session.getAttribute("utente");
    Carrello carrello = (Carrello) session.getAttribute("carrello");
    
    if (utente == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (carrello == null || carrello.getItems().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/carrello.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout Sicuro - ReFrame</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/form.css">
    <style>
        /* CSS Specifico per il Checkout - Layout Responsivo e Minimale */
        .checkout-container { max-width: 1100px; margin: 3rem auto; padding: 0 1rem; display: grid; grid-template-columns: 1.5fr 1fr; gap: 4rem; }
        .section-title { font-size: 1.4rem; border-bottom: 2px solid #000; padding-bottom: 0.5rem; margin-bottom: 2rem; text-transform: uppercase; letter-spacing: 1px; }
        
        .order-summary { background: #fafafa; border: 1px solid #ddd; padding: 2rem; height: fit-content; position: sticky; top: 2rem; }
        .summary-item { display: flex; justify-content: space-between; margin-bottom: 1rem; padding-bottom: 1rem; border-bottom: 1px dashed #ccc; }
        .summary-total { font-size: 1.3rem; font-weight: bold; display: flex; justify-content: space-between; margin-top: 2rem; padding-top: 1rem; border-top: 2px solid #000; }
        
        .checkbox-group { display: flex; align-items: center; gap: 0.8rem; margin: 2rem 0; padding: 1rem; border: 1px solid #000; background: #fff; }
        .checkbox-group input { width: 20px; height: 20px; accent-color: #000; }
        
        .btn-checkout { background: #000; color: #fff; text-transform: uppercase; letter-spacing: 2px; width: 100%; padding: 1.2rem; border: none; cursor: pointer; transition: background 0.3s; font-weight: bold; font-size: 1rem;}
        .btn-checkout:hover { background: #333; }
        
        /* Requisito OBBLIGATORIO: Errori Inline */
        .error-inline { color: #d93025; font-size: 0.9rem; margin-bottom: 1rem; padding: 10px; background: #fce8e6; border-left: 4px solid #d93025; display: none; font-weight: bold; }
        
        /* Media Query per Responsività */
        @media (max-width: 768px) {
            .checkout-container { grid-template-columns: 1fr; gap: 2rem; }
            .order-summary { position: static; order: -1; } /* Su mobile il riepilogo va in alto */
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/components/header.jsp" />

    <main class="checkout-container">
        <section class="checkout-form-section">
			<form id="checkoutForm" action="${pageContext.request.contextPath}/Checkout" method="POST" onsubmit="return validaCheckout(event)">                
                <h2 class="section-title">1. Indirizzo di Spedizione</h2>
                <jsp:include page="/WEB-INF/components/shipping.jsp" />

                <h2 class="section-title" style="margin-top: 3rem;">2. Metodo di Pagamento</h2>
                <jsp:include page="/WEB-INF/components/payment.jsp" />


                <div id="formErrors" class="error-inline">
                    </div>

                <button type="submit" class="btn-checkout">Conferma Ordine e Paga</button>
            </form>
        </section>

        <aside class="order-summary">
            <h2 class="section-title">Riepilogo Ordine</h2>
            
            <% for (CarrelloItem item : carrello.getItems()) { %>
                <div class="summary-item">
                    <div>
                        <strong><%= item.getNome() %></strong><br>
                        <span style="color: #666; font-size: 0.9rem;">Quantità: <%= item.getQuantita() %></span>
                    </div>
                    <div style="font-weight: 500;">
                        € <%= String.format("%.2f", item.getPrezzoTotale()) %>
                    </div>
                </div>
            <% } %>

            <div class="summary-total">
                <span>Totale <small>(IVA incl.)</small></span>
                <span>€ <%= String.format("%.2f", carrello.getTotale()) %></span>
            </div>
        </aside>
    </main>

    <jsp:include page="/WEB-INF/components/footer.jsp" />

    <script>
        function validaCheckout(event) {
            let isValid = true;
            const errorDiv = document.getElementById('formErrors');
            errorDiv.style.display = 'none';
            let messaggioErrore = "";

            /* * REQUISITO CHECKLIST: Validazione JavaScript.
             * Poiché i campi esatti sono dentro i tuoi file shipping.jsp e payment.jsp, 
             * ti lascio la traccia esatta su come validarli. 
             */
             
            // Esempio: Se in payment.jsp hai un input con id="numeroCarta"
            /*
            const numeroCarta = document.getElementById('numeroCarta');
            const regexCarta = /^[0-9]{15,16}$/; // Solo numeri, 15 o 16 cifre
            
            if (numeroCarta && !regexCarta.test(numeroCarta.value)) {
                isValid = false;
                messaggioErrore = "Il numero della carta non è valido. Inserisci 15 o 16 cifre.";
                numeroCarta.focus(); // Requisito Opzionale Checklist: Focus sul campo attivo
            }
            */

            if (!isValid) {
                event.preventDefault(); // Blocca l'invio al server
                errorDiv.style.display = 'block';
                errorDiv.textContent = messaggioErrore;
                return false;
            }
            
            return true; // Se tutto è corretto, il form parte verso CheckoutServlet
        }
    </script>
</body>
</html>