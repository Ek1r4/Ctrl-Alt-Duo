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
    <jsp:include page="/WEB-INF/components/header.jsp" />

	<% 
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.getItems().isEmpty()) { 
    %>
        <div class="empty-cart-container">
            <i class="fas fa-box-open empty-cart-icon"></i>
            <h2>IL TUO CARRELLO È VUOTO</h2>
            <div class="test-panel-container">
                <p class="test-panel-title">Pannello di Test (Simulatore Vetrina):</p>
                <div class="test-panel-buttons">
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="NEW-21">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Sony Alpha 7 IV</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="NEW-11">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Canon EOS R5</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="REF-10">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Minolta X-700</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="PRD49791">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Minolta X-700</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="NEW-81">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Minolta X-700</button>
                    </form>
                </div>
            </div>
            
        </div>
    <% } else { %>
    
    <div class="test-panel-container">
                <p class="test-panel-title">Pannello di Test (Simulatore Vetrina):</p>
                <div class="test-panel-buttons">
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="NEW-21">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Sony Alpha 7 IV</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="NEW-11">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Canon EOS R5</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="REF-10">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Minolta X-700</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="PRD49791">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Minolta X-700</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="id" value="NEW-81">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn btn-test">+ Minolta X-700</button>
                    </form>
                </div>
            </div>
    
        <div class="cart-page-layout">
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
                            <div class="custom-qty-wrapper">
                                <button type="button" class="qty-btn" onclick="gestisciClickMeno('<%= item.getIdProdotto() %>')">
                                    <i id="icon-minus-<%= item.getIdProdotto() %>" class="<%= item.getQuantita() == 1 ? "fas fa-trash-alt" : "fas fa-minus" %>"></i>
                                </button>
                                <input type="number" id="qty-<%= item.getIdProdotto() %>" value="<%= item.getQuantita() %>" class="qty-input-field" readonly>
                                <button type="button" class="qty-btn" onclick="modificaQuantita('<%= item.getIdProdotto() %>', 1)">
                                    <i class="fas fa-plus"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div>

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

    <jsp:include page="/WEB-INF/components/footer.jsp" />
    
    <script>const contestoReFrame = '<%= request.getContextPath() %>';</script>
    <script src="<%= request.getContextPath() %>/js/carrello.js"></script>
</body>
</html>