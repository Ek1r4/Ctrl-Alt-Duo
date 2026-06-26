<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.*" %>
<%@ page import="reframe.model.dao.*" %>
<%@ page import="java.util.List" %>
<%
    Utente utente = (Utente) session.getAttribute("utente");
    Carrello carrello = (Carrello) session.getAttribute("carrello");
    
    if (utente == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (carrello == null || carrello.getItems().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/common/carrello.jsp");
        return;
    }

    SpedizioneDAO spedDAO = new SpedizioneDAO();
    PagamentoDAO pagDAO = new PagamentoDAO();
    List<Spedizione> spedizioni = null;
    List<Pagamento> pagamenti = null;
    try {
        spedizioni = spedDAO.doRetrieveByUtente(utente.getUsername());
        pagamenti = pagDAO.doRetrieveByUtente(utente.getUsername());
    } catch (Exception e) { e.printStackTrace(); }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout Sicuro - ReFrame</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/form.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/checkout.css"> 
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/carrello.css"> 
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="/WEB-INF/components/header.jsp" />

    <form id="checkoutForm" action="${pageContext.request.contextPath}/Checkout" method="POST">
        <div class="checkout-page-layout">
            
            <div class="checkout-form-column">
                
                <h2 class="section-title" style="margin-top:0;">1. Spedizione</h2>
                
                <% boolean firstSped = true;
                   if (spedizioni != null) {
                   for (Spedizione ind : spedizioni) { %>
                    <label style="display:block;">
                        <input type="radio" name="idSpedizione" class="hidden-radio sped-radio" value="<%= ind.getIdSpedizione() %>" <%= firstSped ? "checked" : "" %> required>
                        <div class="selectable-box">
                            <div>
                                <p><strong><%= ind.getVia() %> <%= ind.getCivico() %></strong></p>
                                <p class="sub-text"><%= ind.getCitta() %> (<%= ind.getProvincia().toUpperCase() %>), <%= ind.getCap() %></p>
                            </div>
                        </div>
                    </label>
                <% firstSped = false; } } %>
                
                <label style="display:block;">
                    <input type="radio" name="idSpedizione" class="hidden-radio sped-radio" value="nuovo" id="radioNewSped" <%= firstSped ? "checked" : "" %> required>
                    <div class="selectable-box">
                        <div><p><strong><i class="fas fa-plus"></i> Aggiungi Nuovo Indirizzo</strong></p></div>
                    </div>
                </label>
                
                <div id="formNuovaSpedizione" class="new-entry-form form-grid <%= firstSped ? "" : "hidden" %>">
                    <fieldset class="custom-input"><legend>Via</legend><input type="text" name="via" class="req-sped"></fieldset>
                    <fieldset class="custom-input"><legend>Civico</legend><input type="text" name="civico" class="req-sped"></fieldset>
                    <fieldset class="custom-input"><legend>Città</legend><input type="text" name="citta" class="req-sped"></fieldset>
                    <fieldset class="custom-input"><legend>Provincia</legend><input type="text" name="provincia" maxlength="2" class="req-sped"></fieldset>
                    <fieldset class="custom-input"><legend>CAP</legend><input type="text" name="cap" maxlength="5" class="req-sped"></fieldset>
                    <fieldset class="custom-input"><legend>Paese</legend><input type="text" name="paese" class="req-sped"></fieldset>
                    <fieldset class="custom-input full-width"><legend>Note per il corriere</legend><input type="text" name="note"></fieldset>
                </div>

                <h2 class="section-title">2. Pagamento</h2>
                
                <% boolean firstPag = true;
                   if (pagamenti != null) {
                   for (Pagamento pag : pagamenti) { 
                       String carta = pag.getNumeroCarta();
                       String mask = "****" + (carta.length() >= 4 ? carta.substring(carta.length() - 4) : carta);
                %>
                    <label style="display:block;">
                        <input type="radio" name="idPagamento" class="hidden-radio pag-radio" value="<%= pag.getIdPagamento() %>" <%= firstPag ? "checked" : "" %> required>
                        <div class="selectable-box">
                            <div>
                                <p><strong><%= pag.getCircuito() %> <%= mask %></strong></p>
                                <p class="sub-text">Intestatario: <%= pag.getNomeIntestatario() %> &bull; Scad: <%= pag.getDataScadenza() %></p>
                            </div>
                        </div>
                    </label>
                <% firstPag = false; } } %>

                <label style="display:block;">
                    <input type="radio" name="idPagamento" class="hidden-radio pag-radio" value="nuovo" id="radioNewPag" <%= firstPag ? "checked" : "" %> required>
                    <div class="selectable-box">
                        <div><p><strong><i class="fas fa-plus"></i> Aggiungi Nuova Carta</strong></p></div>
                    </div>
                </label>
                
                <div id="formNuovoPagamento" class="new-entry-form form-grid <%= firstPag ? "" : "hidden" %>">
                    <fieldset class="custom-input full-width"><legend>Nome Intestatario</legend><input type="text" name="nomeIntestatario" class="req-pag"></fieldset>
                    <fieldset class="custom-input full-width"><legend>Circuito</legend>
                        <select name="circuito" class="req-pag">
                            <option value="" disabled selected>Seleziona il circuito...</option>
                            <option value="Visa">Visa</option>
                            <option value="Mastercard">Mastercard</option>
                            <option value="American Express">American Express</option>
                        </select>
                    </fieldset>
                    <fieldset class="custom-input full-width"><legend>Numero Carta</legend><input type="text" name="numeroCarta" maxlength="16" class="req-pag"></fieldset>
                    <fieldset class="custom-input"><legend>Scadenza</legend><input type="text" name="dataScadenza" placeholder="MM/AA" maxlength="5" class="req-pag"></fieldset>
                    <fieldset class="custom-input"><legend>CVV</legend><input type="text" name="cvv" maxlength="4" class="req-pag"></fieldset>
                </div>
            </div>

            <div class="checkout-receipt-column">
                <div class="receipt-box">
                    <div class="receipt-title">RICEVUTA</div>
                    <div class="receipt-divider"></div>
                    <div class="receipt-row">
                        <span>Subtotale:</span>
                        <span>€ <%= String.format("%.2f", carrello.getSubtotaleProdotti()) %></span>
                    </div>
                    <div class="receipt-row">
                        <span>Spedizione:</span>
                        <span>€ <%= String.format("%.2f", carrello.getCostoSpedizione()) %></span>
                    </div>
                    <div class="receipt-divider"></div>
                    <div class="receipt-total">
                        <span>TOTALE:</span>
                        <span>€ <%= String.format("%.2f", carrello.getTotaleComplessivo()) %></span>
                    </div>
                    <button type="submit" class="btn-cta btn-checkout">CONFERMA E PAGA</button>
                </div>
            </div>
        </div>
    </form>

    <jsp:include page="/WEB-INF/components/footer.jsp" />
    <script>
    function aggiornaRequisitiForm() {
        const isSpedNuovo = document.getElementById('radioNewSped').checked;
        document.getElementById('formNuovaSpedizione').classList.toggle('hidden', !isSpedNuovo);
        document.querySelectorAll('.req-sped').forEach(input => input.required = isSpedNuovo);

        const isPagNuovo = document.getElementById('radioNewPag').checked;
        document.getElementById('formNuovoPagamento').classList.toggle('hidden', !isPagNuovo);
        document.querySelectorAll('.req-pag').forEach(input => input.required = isPagNuovo);
    }

    document.querySelectorAll('.sped-radio, .pag-radio').forEach(radio => {
        radio.addEventListener('change', aggiornaRequisitiForm);
    });

    // Esecuzione all'avvio
    aggiornaRequisitiForm();
    </script>
</body>
</html>