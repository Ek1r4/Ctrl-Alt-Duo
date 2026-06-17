<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Utente" %>
<%@ page import="reframe.model.beans.Spedizione" %>
<%@ page import="reframe.model.beans.Pagamento" %>
<%@ page import="java.util.List" %>

<%
    Utente utenteLoggato = (Utente) session.getAttribute("utente");
    if (utenteLoggato == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    List<Spedizione> listaSpedizioni = (List<Spedizione>) request.getAttribute("listaSpedizioni");
    List<Pagamento> listaPagamenti = (List<Pagamento>) request.getAttribute("listaPagamenti");
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>ReFrame - Profilo di @<%= utenteLoggato.getUsername() %></title>
    
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/variables.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/user-area.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

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
                                    <option value="" disabled selected>Seleziona...</option>
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
                            String cartaMascherata = "****" + (numCarta != null && numCarta.length() >= 4 ? numCarta.substring(numCarta.length() - 4) : numCarta); %>
                            <div class="info-row-item" data-item-id="<%= pag.getIdPagamento() %>" data-type="payment">
                                <div class="item-details" style="display: flex; width: 100%; justify-content: space-between; padding-right: 15px;">
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
                <div class="card-header">
                    <h2>HISTORY</h2>
                </div>
                <div class="history-content"></div>
            </div>

        </div> 

		<div class="profile-column fixed-column">
            
            <div class="profile-card general-info-card">
                
                <div class="card-header profile-card-header">
                    <h2><i class="fas fa-user"></i> <%= utenteLoggato.getUsername() %></h2>
                    <div>
                        <button id="btnEditProfile" class="btn-edit-profile" title="Modifica Profilo">
                            <i class="fas fa-pencil-alt"></i>
                        </button>
                    </div>
                </div>

                <div class="user-details-grid">
                    <div class="detail-block">
                        <p><span class="profile-label">NOME:</span> <%= utenteLoggato.getNome() %></p>
                    </div>
                    
                    <div class="detail-block">
                        <p><span class="profile-label">COGNOME:</span> <%= utenteLoggato.getCognome() %></p>
                    </div>
                    
                    <div class="detail-block">
                        <p><span class="profile-label">EMAIL:</span> <%= utenteLoggato.getEmail() %></p>
                    </div>
                    
                    <div class="password-display-row">
                    	<p><span class="profile-label">PASSWORD:</span> <span class="tech-text">&bull;&bull;&bull;&bull;&bull;&bull;&bull;&bull;</span></p>
                    	<button id="btnPasswordToggle" class="btn-edit" title="Cambia Password">
                        	<i class="fas fa-key"></i>
                    	</button>
                	</div>
                    
                    <div class="detail-block">
                        <p id="txtTelefono"><%= utenteLoggato.getTelefono() != null ? utenteLoggato.getTelefono() : "Non specificato" %></p>
                        <input type="text" id="inputTelefono" class="edit-input hidden" value="<%= utenteLoggato.getTelefono() != null ? utenteLoggato.getTelefono() : "" %>">
                        <span id="errorTelefono" class="error-text"></span>
                    </div>

                    <div class="detail-block">
                        <p><span class="profile-label">BIO</span></p>
                        <p id="txtBio"><%= utenteLoggato.getBio() != null ? utenteLoggato.getBio() : "Nessuna biografia inserita." %></p>
                        <textarea id="textareaBio" class="edit-input hidden" rows="6"><%= utenteLoggato.getBio() != null ? utenteLoggato.getBio() : "" %></textarea>
                        <span id="errorBio" class="error-text"></span>
                    </div>
                </div>

                <div id="editActions" class="edit-actions hidden">
                    <button id="btnCancelProfile" class="btn-cta cancel-btn form-btn">Annulla</button>
                    <button id="btnSaveProfile" class="btn-cta form-btn">Salva</button>
                </div>

                <div id="formPasswordContainer" class="hidden form-container password-form-container">
                    <form action="<%= request.getContextPath() %>/ProfiloServlet" method="POST">
                        <input type="hidden" name="action" value="cambioPassword">
                        
                        <div class="form-grid">
                            <fieldset class="custom-input full-width"><legend>Vecchia Password</legend><input type="password" name="vecchiaPassword" required></fieldset>
                            <fieldset class="custom-input full-width"><legend>Nuova Password</legend><input type="password" name="nuovaPassword" required placeholder="Minimo 8 caratteri"></fieldset>
                            <fieldset class="custom-input full-width"><legend>Conferma Password</legend><input type="password" name="confermaPassword" required></fieldset>
                        </div>

                        <div class="form-actions">
                            <button type="button" id="btnCancelPassword" class="btn-cta cancel-btn form-btn">Annulla</button>
                            <button type="submit" class="btn-cta form-btn">Aggiorna</button>
                        </div>
                    </form>
                </div>

            </div> 
		</div>

    <script>const contestoReFrame = '<%= request.getContextPath() %>';</script>
    <script src="<%= request.getContextPath() %>/js/profilo.js"></script>
</body>
</html>