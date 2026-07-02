<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="reframe.model.beans.Recensione" %>
<%@ page import="reframe.model.beans.Utente" %>
<%@ page import="reframe.model.beans.Prodotto" %>

<%
    // Recupero la lista delle recensioni passata dalla Servlet
    List<Recensione> listaRecensioni = (List<Recensione>) request.getAttribute("recensioniProdotto");
    
    // Recupero l'utente attualmente loggato in sessione
    Utente utenteRecensione = (Utente) session.getAttribute("utente");
%>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/dettagliProdotto.css">

<div class="clean-reviews-wrapper">
    <h3 class="reviews-title">RECENSIONI DEI CLIENTI</h3>

    <% if (utenteRecensione == null) { %>
        <div class="login-prompt-minimal">
            Per lasciare una recensione devi <a href="<%= request.getContextPath() %>/login.jsp">accedere al tuo account</a>.
        </div>
        
    <% } else if (utenteRecensione.getIsAdmin() > 0) { %>
        <div class="login-prompt-minimal">
            <em>Gli account amministratore non sono abilitati a rilasciare recensioni sui prodotti.</em>
        </div>
        
    <% } else { %>
        <div class="minimal-review-form-container">
            <form action="<%= request.getContextPath() %>/RecensioneServlet" method="POST" class="minimal-review-form">
                <input type="hidden" name="action" value="aggiungi">
                <input type="hidden" name="idProdotto" value="<%= p.getId() %>"> 
                
                <div class="review-form-header">
                    <span class="user-greeting">Lascia una recensione come <strong>@<%= utenteRecensione.getUsername() %></strong></span>
                    
                    <select name="rating" required class="clean-rating-select">
                        <option value="" disabled selected>Il tuo voto...</option>
                        <option value="5">★★★★★ (5/5)</option>
                        <option value="4">★★★★☆ (4/5)</option>
                        <option value="3">★★★☆☆ (3/5)</option>
                        <option value="2">★★☆☆☆ (2/5)</option>
                        <option value="1">★☆☆☆☆ (1/5)</option>
                    </select>
                </div>
                
                <textarea name="descrizione" rows="3" class="clean-review-textarea" placeholder="Condividi la tua esperienza con questa fotocamera..." required></textarea>
                
                <div class="review-form-footer">
                    <button type="submit" class="btn-clean-submit">PUBBLICA RECENSIONE</button>
                </div>
            </form>
        </div>
    <% } %>

    <div class="minimal-reviews-list">
        <% if (listaRecensioni != null && !listaRecensioni.isEmpty()) { 
            for (Recensione rec : listaRecensioni) { 
                int stellePiene = (int) Math.floor(rec.getRating());
        %>
            <div class="minimal-review-item">
                <div class="review-item-header">
                    <div style="display: flex; align-items: center; gap: 15px;">
                        <span class="review-author"><i class="fas fa-user-circle"></i> @<%= rec.getIdUtente() %></span>
                        <div class="review-stars-minimal">
                            <% for(int i=0; i<5; i++) { %>
                                <i class="<%= i < stellePiene ? "fas" : "far" %> fa-star"></i>
                            <% } %>
                        </div>
                    </div>
                    
                    <% if (utenteRecensione != null && utenteRecensione.getIsAdmin() > 0) { %>
                        <form action="<%= request.getContextPath() %>/RecensioneServlet" method="POST" style="margin: 0;">
                            <input type="hidden" name="action" value="elimina">
                            <input type="hidden" name="idRecensione" value="<%= rec.getIdRecensione() %>">
                            
                            <button type="submit" class="btn-delete-review" title="Elimina recensione (Admin)">
                                <i class="fas fa-trash-alt"></i>
                            </button>
                        </form>
                    <% } %>
                </div>
                <p class="review-text"><%= rec.getDescrizione() %></p>
            </div>
        <%  } 
        } else { %>
            <p class="no-reviews-msg">Nessuna recensione presente. Sii il primo a condividere la tua opinione!</p>
        <% } %>
    </div>
</div>