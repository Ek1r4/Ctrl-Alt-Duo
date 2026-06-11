<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- ==========================================================================
     DIRETTIVE E IMPORT JAVA
     ========================================================================== --%>
<%@ page import="reframe.model.beans.Utente" %>
<%@ page import="reframe.model.beans.Spedizione" %>
<%@ page import="reframe.model.beans.Pagamento" %>
<%@ page import="java.util.List" %>

<%-- ==========================================================================
     CONTROLLO ACCESSO E RECUPERO DATI (BARRIERA DI SICUREZZA)
     ========================================================================== --%>
<%
    // Verifica se l'utente è loggato nella sessione corrente
    Utente utenteLoggato = (Utente) session.getAttribute("utente");
    if (utenteLoggato == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    // Recupero delle liste caricate e passate dalla ProfiloServlet
    List<Spedizione> listaSpedizioni = (List<Spedizione>) request.getAttribute("listaSpedizioni");
    List<Pagamento> listaPagamenti = (List<Pagamento>) request.getAttribute("listaPagamenti");
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>ReFrame - Profilo di @<%= utenteLoggato.getUsername() %></title>
    
    <%-- ==========================================================================
         COLLEGAMENTO CONFIGURAZIONI STILE (FILE ESTERNI)
         ========================================================================== --%>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/user-area.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

    <%-- CONTENITORE GENERALE (Layout a due colonne) --%>
    <div class="profile-page-container">
        
        <%-- ==========================================================================
             3. COLONNA SINISTRA (ELEMENTI SCORRIBILI)
             ========================================================================== --%>
        <div class="profile-column scrollable-column">
            
            <%-- SEZIONE SPEDIZIONI --%>
            <div class="profile-card">
                <div class="card-header">
                    <h2><i class="fas fa-truck"></i> Indirizzi di Spedizione</h2>
                    <button id="btnAddSpedizione" class="btn-add" title="Aggiungi nuovo indirizzo">
                        <i class="fas fa-plus"></i>
                    </button>
                </div>
                
                <%-- 4. Form a scomparsa per l'inserimento di un indirizzo --%>
                <div id="formSpedizioneContainer" class="hidden form-container">
                    <form action="<%= request.getContextPath() %>/ProfiloServlet" method="POST">
                        <input type="hidden" name="action" value="salvaSpedizione">
                        <div class="form-grid">
                            <input type="text" name="via" placeholder="Via (es. Roma)" required>
                            <input type="text" name="civico" placeholder="Civico" required>
                            <input type="text" name="citta" placeholder="Città" required>
                            <input type="text" name="provincia" placeholder="Provincia (es. SA)" maxlength="2" required>
                            <input type="text" name="cap" placeholder="CAP" maxlength="5" required>
                            <input type="text" name="paese" placeholder="Paese" required>
                            <input type="text" name="note" placeholder="Note per il corriere" class="full-width">
                        </div>
                        <div class="form-actions">
                            <button type="button" id="btnCancelSpedizione" class="btn-secondary">Annulla</button>
                            <button type="submit" class="btn-cta">Salva Indirizzo</button>
                        </div>
                    </form>
                </div>

                <%-- Contenitore della lista dinamica indirizzi --%>
                <div class="scrollable-content">
                    <% 
                        if (listaSpedizioni != null && !listaSpedizioni.isEmpty()) {
                            for (Spedizione ind : listaSpedizioni) { 
                    %>
                                <div class="info-row-item" data-item-id="<%= ind.getIdSpedizione() %>" data-type="shipping">
                                    <div class="item-details">
                                        <strong><%= ind.getVia() %>, <%= ind.getCivico() %></strong>
                                        <p><%= ind.getCap() %> - <%= ind.getCitta() %> (<%= ind.getProvincia().toUpperCase() %>), <%= ind.getPaese() %></p>
                                    </div>
                                    <button class="btn-delete" title="Elimina indirizzo">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </div>
                    <% 
                            } 
                        } else { 
                    %>
                            <p class="empty-message">Nessun indirizzo di spedizione salvato.</p>
                    <% 
                        } 
                    %>
                </div>
            </div>

            <%-- SEZIONE METODI DI PAGAMENTO --%>
            <div class="profile-card">
                <div class="card-header">
                    <h2><i class="fas fa-credit-card"></i> Metodi di Pagamento</h2>
                    <button id="btnAddPagamento" class="btn-add" title="Aggiungi nuovo metodo">
                        <i class="fas fa-plus"></i>
                    </button>
                </div>

                <%-- 4. Form a scomparsa per l'inserimento di una carta --%>
                <div id="formPagamentoContainer" class="hidden form-container">
                    <form action="<%= request.getContextPath() %>/ProfiloServlet" method="POST">
                        <input type="hidden" name="action" value="salvaPagamento">
                        <div class="form-grid">
                            <input type="text" name="nomeIntestatario" placeholder="Nome Intestatario" class="full-width" required>
                            <select name="circuito" required class="full-width">
                                <option value="" disabled selected>Seleziona Circuito...</option>
                                <option value="Visa">Visa</option>
                                <option value="Mastercard">Mastercard</option>
                                <option value="American Express">American Express</option>
                            </select>
                            <input type="text" name="numeroCarta" placeholder="Numero Carta" maxlength="16" required class="full-width">
                            <input type="text" name="dataScadenza" placeholder="Scadenza (MM/AA)" maxlength="5" required>
                            <input type="text" name="cvv" placeholder="CVV" maxlength="4" required>
                        </div>
                        <div class="form-actions">
                            <button type="button" id="btnCancelPagamento" class="btn-secondary">Annulla</button>
                            <button type="submit" class="btn-cta">Salva Carta</button>
                        </div>
                    </form>
                </div>

                <%-- Contenitore della lista dinamica metodi di pagamento --%>
                <div class="scrollable-content">
                    <% 
                        if (listaPagamenti != null && !listaPagamenti.isEmpty()) {
                            for (Pagamento pag : listaPagamenti) {
                                String numCarta = pag.getNumeroCarta();
                                String cartaMascherata = "•••• •••• •••• " + (numCarta != null && numCarta.length() >= 4 ? numCarta.substring(numCarta.length() - 4) : numCarta); 
                    %>
                                <div class="info-row-item" data-item-id="<%= pag.getIdPagamento() %>" data-type="payment">
                                    <div class="item-details">
                                        <strong><%= pag.getCircuito() %> - <%= pag.getNomeIntestatario() %></strong>
                                        <p><%= cartaMascherata %> (Scadenza: <%= pag.getDataScadenza() %>)</p>
                                    </div>
                                    <button class="btn-delete" title="Elimina metodo">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </div>
                    <% 
                            } 
                        } else { 
                    %>
                            <p class="empty-message">Nessun metodo di pagamento salvato.</p>
                    <% 
                        } 
                    %>
                </div>
            </div>

        </div> 


        <%-- ==========================================================================
             3. COLONNA DESTRA (ELEMENTI FISSI)
             ========================================================================== --%>
        <div class="profile-column fixed-column">
            
            <%-- SEZIONE ANAGRAFICA UTENTE --%>
            <div class="profile-card general-info-card">
                <div class="card-header">
                    <h2><i class="fas fa-user-circle"></i> @<%= utenteLoggato.getUsername() %></h2>
                    <div>
                        <button id="btnPasswordToggle" class="btn-edit" title="Cambia Password" style="margin-right: 10px;">
                            <i class="fas fa-key"></i>
                        </button>
                        <button id="btnEditProfile" class="btn-edit" title="Modifica Profilo">
                            <i class="fas fa-pencil-alt"></i>
                        </button>
                    </div>
                </div>

                <%-- 4. Form a scomparsa per il cambio password --%>
                <div id="formPasswordContainer" class="hidden form-container">
                    <form action="<%= request.getContextPath() %>/ProfiloServlet" method="POST">
                        <input type="hidden" name="action" value="cambioPassword">
                        <div class="form-grid">
                            <input type="password" name="vecchiaPassword" placeholder="Vecchia Password" class="full-width" required>
                            <input type="password" name="nuovaPassword" placeholder="Nuova Password (min. 8 caratteri)" required>
                            <input type="password" name="confermaPassword" placeholder="Conferma Nuova Password" required>
                        </div>
                        <div class="form-actions">
                            <button type="button" id="btnCancelPassword" class="btn-secondary">Annulla</button>
                            <button type="submit" class="btn-cta">Aggiorna Password</button>
                        </div>
                    </form>
                </div>

                <%-- Griglia dei dettagli anagrafici (Lettura / Modifica Inline) --%>
                <div class="user-details-grid">
                    <div class="detail-block">
                        <label>Nome Completo</label>
                        <p><%= utenteLoggato.getNome() %> <%= utenteLoggato.getCognome() %></p>
                    </div>
                    
                    <div class="detail-block">
                        <label>Email</label>
                        <p><%= utenteLoggato.getEmail() %></p>
                    </div>
                    
                    <div class="detail-block">
                        <label>Telefono</label>
                        <p id="txtTelefono"><%= utenteLoggato.getTelefono() != null ? utenteLoggato.getTelefono() : "Non specificato" %></p>
                        <input type="text" id="inputTelefono" class="edit-input hidden" value="<%= utenteLoggato.getTelefono() != null ? utenteLoggato.getTelefono() : "" %>">
                        <span id="errorTelefono" class="error-text"></span>
                    </div>

                    <div class="detail-block full-width">
                        <label>Biografia</label>
                        <p id="txtBio"><%= utenteLoggato.getBio() != null ? utenteLoggato.getBio() : "Nessuna biografia inserita." %></p>
                        <textarea id="textareaBio" class="edit-input hidden" rows="3"><%= utenteLoggato.getBio() != null ? utenteLoggato.getBio() : "" %></textarea>
                        <span id="errorBio" class="error-text"></span>
                    </div>
                </div>

                <%-- Pulsanti di salvataggio/annullamento per la modifica inline dell'anagrafica --%>
                <div id="editActions" class="edit-actions hidden">
                    <button id="btnCancelProfile" class="btn-secondary">Annulla</button>
                    <button id="btnSaveProfile" class="btn-cta">Salva</button>
                </div>
            </div>

            <%-- SEZIONE CRONOLOGIA ORDINI (PER ORA NON DINAMICA)--%>
            <div class="profile-card">
                <div class="card-header">
                    <h2><i class="fas fa-history"></i> Cronologia Ordini</h2>
                    <button class="btn-add"><i class="fas fa-external-link-alt"></i></button>
                </div>
                <div class="history-content">
                    <div class="order-summary-row">
                        <span><strong>Ordine #RF-94821</strong> - 14/05/2026</span>
                        <span class="order-status consegnato">Consegnato</span>
                        <span>€ 1.249,00</span>
                    </div>
                </div>
            </div>

        </div> 

    </div>

    <%-- ==========================================================================
         SCRIPT JAVASCRIPT ED HELPER DI LOGICA INTERATTIVA
         ========================================================================== --%>
    <script>const contestoReFrame = '<%= request.getContextPath() %>';</script>
    <script src="<%= request.getContextPath() %>/js/profilo.js"></script>
</body>
</html>