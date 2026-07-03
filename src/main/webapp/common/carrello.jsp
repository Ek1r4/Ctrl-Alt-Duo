<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Carrello" %>
<%@ page import="reframe.model.beans.CarrelloItem" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Il tuo Carrello - ReFrame</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/form.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/carrello.css"> 
</head>
<body>
    
    <!-- HEADER -->
    <jsp:include page="/WEB-INF/components/header.jsp" />

    <%-- GESTIONE STATO CARRELLO --%>
    <% 
        // Recupero dell'oggetto Carrello dal contesto di sessione: in assenza di un'istanza valida o in presenza di una lista elementi vuota, il blocco if garantisce il rendering esclusivo della view di fallback.
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.getItems().isEmpty()) { 
    %>
        <div class="empty-cart-container">
            <i class="fas fa-box-open empty-cart-icon"></i>
            <h2>IL TUO CARRELLO È VUOTO</h2>
        </div>
    <% } else { %>
        
        <!-- LAYOUT PAGINA CARRELLO -->
        <div class="cart-page-layout">
            
            <!-- LISTA ELEMENTI -->
            <div class="cart-items-column">
                <h1 class="cart-page-title">Carrello</h1>
                <% for (CarrelloItem item : carrello.getItems()) { %>
                    <div class="cart-item-box" id="riga-<%= item.getIdProdotto() %>">
                        <div class="cart-item-info">
                            <h3><%= item.getNome() %></h3>
                            <p>€ <%= String.format("%.2f", item.getPrezzo()) %></p>
                        </div>
                        <div class="cart-item-controls">
                            <span class="cart-item-price" id="totale-riga-<%= item.getIdProdotto() %>">
                                € <%= String.format("%.2f", item.getPrezzoTotale()) %>
                            </span>
                            
                            <%-- Gestione dinamica dei controlli di quantità: lo stato dell'icona decremento (minus/trash) si adatta in tempo reale se la quantità attuale raggiunge il limite minimo, predisponendo l'interfaccia alla cancellazione dell'item. --%>
                            <div class="custom-qty-wrapper">
                                <button type="button" class="qty-btn" onclick="gestisciClickMeno('<%= item.getIdProdotto() %>')">
                                    <i id="icon-minus-<%= item.getIdProdotto() %>" class="<%= item.getQuantita() == 1 ? "fas fa-trash-alt" : "fas fa-minus" %>"></i>
                                </button>
                                <input type="number" id="qty-<%= item.getIdProdotto() %>" value="<%= item.getQuantita() %>" data-stock="<%= item.getInStock() %>" class="qty-input-field" readonly>
                                <button type="button" class="qty-btn" onclick="modificaQuantita('<%= item.getIdProdotto() %>', 1)">
                                    <i class="fas fa-plus"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div> 

            <!-- RIEPILOGO COSTI (CHECKOUT) -->
            <div class="cart-receipt-column">
                <div class="receipt-box">
                    <div class="receipt-title">RICEVUTA</div>
                    <div class="receipt-divider"></div>
                    <div class="receipt-row">
                        <span>Subtotale:</span>
                        <span id="txt-subtotale">€ <%= String.format("%.2f", carrello.getSubtotaleProdotti()) %></span>
                    </div>
                    <div class="receipt-row">
                        <span>Spedizione:</span>
                        <span id="txt-spedizione">€ <%= String.format("%.2f", carrello.getCostoSpedizione()) %></span>
                    </div>
                    <div class="receipt-divider"></div>
                    <div class="receipt-total">
                        <span>TOTALE:</span>
                        <span id="txt-totale-complessivo">€ <%= String.format("%.2f", carrello.getTotaleComplessivo()) %></span>
                    </div>
                    <a href="${pageContext.request.contextPath}/common/checkout.jsp" class="checkout-link">
                        <button type="button" class="btn-cta">PROCEDI AL CHECKOUT</button>
                    </a>
                </div>
            </div>
        </div>
    <% } %>

    <!-- MODALI E FOOTER -->
    <jsp:include page="/WEB-INF/components/footer.jsp" />
    
    <div class="admin-modal-overlay" id="delete-confirm-modal">
        <div class="confirm-modal-box film-container">
            
            <h3>CONFERMA RIMOZIONE</h3>
            
            <p class="confirm-message" id="delete-confirm-message">
                Sei sicuro di voler rimuovere questo articolo<br>dal carrello?
            </p>
            
            <div class="confirm-actions">
                <button type="button" class="btn-cta cancel-btn" id="btn-cancel-delete">ANNULLA</button>
                <button type="button" class="btn-cta danger-btn" id="btn-confirm-delete">PROCEDI</button>
            </div>
            
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/carrello.js"></script>

</body>
</html>