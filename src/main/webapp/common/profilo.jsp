<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Utente" %>
<%@ page import="reframe.model.beans.Spedizione" %>
<%@ page import="reframe.model.beans.Pagamento" %>
<%@ page import="reframe.model.beans.Ordine" %>
<%@ page import="reframe.model.beans.DettaglioOrdine" %>
<%@ page import="java.util.List" %>

<%
    Utente utenteLoggato = (Utente) session.getAttribute("utente");
    if (utenteLoggato == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    List<Spedizione> listaSpedizioni = (List<Spedizione>) request.getAttribute("listaSpedizioni");
    List<Pagamento> listaPagamenti = (List<Pagamento>) request.getAttribute("listaPagamenti");
    List<Ordine> listaOrdini = (List<Ordine>) request.getAttribute("listaOrdini");
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ReFrame - Profilo di @<%= utenteLoggato.getUsername() %></title>
    
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/variables.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/user-area.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/form.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <%@ include file="/WEB-INF/components/header.jsp" %>
    
    <div class="profile-page-container">
        
        <div class="profile-column scrollable-column">
            
            <div class="profile-card">
                <div class="card-header">
                    <h2><i class="fas fa-truck"></i> SHIPPING</h2>
                    <button id="btnAddSpedizione" class="btn-add" title="Aggiungi nuovo indirizzo"><i class="fas fa-plus"></i></button>
                </div>
                
                <div id="formSpedizioneContainer" class="hidden form-container">
                    <form action="<%= request.getContextPath() %>/ProfiloServlet" method="POST">
                        <input type="hidden" name="action" value="salvaSpedizione">
                        <div class="form-grid">
                            <fieldset class="custom-input"><legend>Via</legend><input type="text" name="via" required></fieldset>
                            <fieldset class="custom-input"><legend>Civico</legend><input type="text" name="civico" required></fieldset>
                            <fieldset class="custom-input"><legend>Città</legend><input type="text" name="citta" required></fieldset>
                            <fieldset class="custom-input"><legend>Provincia</legend><input type="text" name="provincia" maxlength="2" required></fieldset>
                            <fieldset class="custom-input"><legend>CAP</legend><input type="text" name="cap" maxlength="5" required></fieldset>
                            <fieldset class="custom-input"><legend>Paese</legend><input type="text" name="paese" required></fieldset>
                            <fieldset class="custom-input full-width"><legend>Note per il corriere</legend><input type="text" name="note"></fieldset>
                        </div>
                        <div class="form-actions">
                            <button type="button" id="btnCancelSpedizione" class="btn-cta cancel-btn form-btn">Annulla</button>
                            <button type="submit" class="btn-cta form-btn">Salva Indirizzo</button>
                        </div>
                    </form>
                </div>

                <div class="scrollable-content">
                    <% if (listaSpedizioni != null && !listaSpedizioni.isEmpty()) {
                        for (Spedizione ind : listaSpedizioni) { %>
                            <div class="info-row-item" data-item-id="<%= ind.getIdSpedizione() %>" data-type="shipping">
                                <div class="item-details">
                                    <p><%= ind.getVia() %> <%= ind.getCivico() %></p>
                                    <p class="sub-text"><%= ind.getCitta() %> (<%= ind.getProvincia().toUpperCase() %>), <%= ind.getCap() %></p>
                                </div>
                                <button class="btn-delete" title="Elimina indirizzo"><i class="fas fa-trash-alt"></i></button>
                            </div>
                    <% } } else { %>
                        <p class="empty-message">Nessun indirizzo di spedizione salvato.</p>
                    <% } %>
                </div>
            </div>

            <div class="profile-card">
                <div class="card-header">
                    <h2><i class="fas fa-credit-card"></i> PAYMENT</h2>
                    <button id="btnAddPagamento" class="btn-add" title="Aggiungi nuovo metodo"><i class="fas fa-plus"></i></button>
                </div>

                <div id="formPagamentoContainer" class="hidden form-container">
                    <form action="<%= request.getContextPath() %>/ProfiloServlet" method="POST">
                        <input type="hidden" name="action" value="salvaPagamento">
                        <div class="form-grid">
                            <fieldset class="custom-input full-width"><legend>Nome Intestatario</legend><input type="text" name="nomeIntestatario" required></fieldset>
                            <fieldset class="custom-input full-width"><legend>Circuito</legend>
                                <select name="circuito" required>
                                    <option value="" disabled selected>Seleziona il circuito...</option>
                                    <option value="Visa">Visa</option>
                                    <option value="Mastercard">Mastercard</option>
                                    <option value="American Express">American Express</option>
                                </select>
                            </fieldset>
                            <fieldset class="custom-input full-width"><legend>Numero Carta</legend><input type="text" name="numeroCarta" maxlength="16" required></fieldset>
                            <fieldset class="custom-input"><legend>Scadenza</legend><input type="text" name="dataScadenza" placeholder="MM/AA" maxlength="5" required></fieldset>
                            <fieldset class="custom-input"><legend>CVV</legend><input type="text" name="cvv" maxlength="4" required></fieldset>
                        </div>
                        <div class="form-actions">
                            <button type="button" id="btnCancelPagamento" class="btn-cta cancel-btn form-btn">Annulla</button>
                            <button type="submit" class="btn-cta form-btn">Salva Carta</button>
                        </div>
                    </form>
                </div>

                <div class="scrollable-content">
                    <% if (listaPagamenti != null && !listaPagamenti.isEmpty()) {
                        for (Pagamento pag : listaPagamenti) {
                            String numCarta = pag.getNumeroCarta();
                            String cartaMascherata = "****" + (numCarta != null && numCarta.length() >= 4 ? numCarta.substring(numCarta.length() - 4) : numCarta);
                    %>
                            <div class="info-row-item" data-item-id="<%= pag.getIdPagamento() %>" data-type="payment">
                                <div class="item-details item-details-flex">
                                    <p><%= pag.getCircuito() %> <%= cartaMascherata %></p>
                                    <p class="sub-text"><%= pag.getDataScadenza() %></p>
                                </div>
                                <button class="btn-delete" title="Elimina metodo"><i class="fas fa-trash-alt"></i></button>
                            </div>
                    <% } } else { %>
                        <p class="empty-message">Nessun metodo di pagamento salvato.</p>
                    <% } %>
                </div>
            </div>

            <div class="profile-card">
                <div class="card-header history-header-wrap">
                    <h2><i class="fas fa-history"></i> HISTORY</h2>
                    
                    <div class="search-history-container">
                        <fieldset class="custom-input search-history-fieldset">
                            <input type="text" id="searchHistory" class="search-history-input" placeholder="Cerca ordine n°..." onkeyup="filtraOrdini()">
                        </fieldset>
                    </div>
                </div>
  
                <div class="history-content scrollable-content">
                    
                    <div id="noSearchResults" class="hidden empty-message empty-history-container compact">
                        <i class="fas fa-search empty-history-icon small"></i>
                        <p>Nessun ordine corrisponde alla tua ricerca.</p>
                    </div>

                    <% if (listaOrdini != null && !listaOrdini.isEmpty()) {
                        for (Ordine ord : listaOrdini) { %>
                            <div class="info-row-item order-row order-row-item" onclick="openOrderModal('<%= ord.getIdOrdine() %>')">
                                <div class="item-details item-details-flex">
                                    <p><strong>#<%= ord.getIdOrdine() %></strong></p>
                                    <p class="sub-text"><%= ord.getDataOrdine() %> &bull; <strong class="order-state-text"><%= ord.getStato() %></strong></p>
                                </div>
                                <button class="btn-edit" title="Vedi Dettagli"><i class="fas fa-eye"></i></button>
                            </div>
                    <% } } else { %>
                        <div class="empty-message empty-history-container">
                            <i class="fas fa-box-open empty-history-icon"></i>
                            <p>Non hai ancora effettuato alcun acquisto.</p>
                            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-cta btn-explore">Esplora la Vetrina</a>
                        </div>
                    <% } %>
                </div>
            </div>

        </div> 

		<%@ include file="/WEB-INF/components/anagrafia.jsp" %>
 		
	</div>
	<a href="<%= request.getContextPath() %>/common/centroAssistenza.jsp" class="btn-cta" >
        	Hai bisogno di aiuto? vai al nostro centro assistenza
        </a>
	<div id="delete-confirm-modal" class="admin-modal-overlay">
            <div class="film-container modal-film-override confirm-modal-box">
                
                <h3 class="form-title" style="margin-bottom: 5px;">Conferma Azione</h3>
                <p id="delete-confirm-message" class="confirm-message"></p>
                
                <div class="confirm-actions">
                    <button type="button" id="btn-cancel-delete" class="btn-cta cancel-btn">Annulla</button>
                    <button type="button" id="btn-confirm-delete" class="btn-cta danger-btn">Procedi</button>
                </div>
            </div>
        </div>
	
			<footer class="site-footer-minimal">
            	&copy; 2026 ReFrame
    		</footer>
	<% if (listaOrdini != null) {
        for (Ordine ord : listaOrdini) { %>
        <div id="modal-<%= ord.getIdOrdine() %>" class="order-modal-overlay hidden">
            <div class="film-container order-modal-content">
                <button class="close-modal-btn" onclick="closeOrderModal('<%= ord.getIdOrdine() %>')"><i class="fas fa-times"></i></button>
                
                <h2 class="form-title modal-form-title">ORDINE #<%= ord.getIdOrdine() %></h2>

                <div class="ticket-selections-analog">
                    <span class="selection-label-analog">Dettagli Transazione</span>
                    <p class="ticket-row"><strong>Data Acquisto:</strong> <%= ord.getDataOrdine() %></p>
                    <p class="ticket-row"><strong>Stato:</strong> <%= ord.getStato() %></p>
                    <p class="ticket-total"><strong>Totale Pagato:</strong> € <%= String.format("%.2f", ord.getTotale()) %></p>
                </div>

                <div class="ticket-selections-analog ticket-items-box">
                    <span class="selection-label-analog">Articoli Acquistati</span>
                    
                    <div class="modal-items-scroll">
                        <% if (ord.getDettagli() != null && !ord.getDettagli().isEmpty()) {
                             for (DettaglioOrdine dett : ord.getDettagli()) { %>
                            <div class="single-modal-item">
                                <span class="modal-item-name">
                                    <%= dett.getQuantitaAcquisto() %>x <%= dett.getNomeProdottoAcquisto() %>
                                </span>
                                <strong>€ <%= String.format("%.2f", dett.getTotaleRiga()) %></strong>
                            </div>
                        <%   } 
                           } else { %>
                            <p class="modal-error-text">Dettagli ordine non trovati.</p>
                        <% } %>
                    </div>
                </div>

                <div class="modal-footer-box">
                    <jsp:include page="/WEB-INF/components/btn-fattura.jsp">
                        <jsp:param name="idOrdine" value="<%= ord.getIdOrdine() %>" />
                    </jsp:include>
                </div>
            </div>
        </div>
    <% } } %>
    <script>const contestoReFrame = '<%= request.getContextPath() %>';</script>
    <script src="<%= request.getContextPath() %>/js/profilo.js"></script>
</body>
</html>