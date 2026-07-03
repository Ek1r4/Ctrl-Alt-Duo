<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="reframe.model.beans.Prodotto" %>
<%@ page import="reframe.model.beans.Utente" %>
<%
    Utente utenteLoggato = (Utente) session.getAttribute("utente");
    boolean isAdmin = (utenteLoggato != null && utenteLoggato.getIsAdmin() > 0);
    List<Prodotto> lista = (List<Prodotto>) request.getAttribute("listaProdotti");

    if (lista != null && !lista.isEmpty()) {
        for (Prodotto p : lista) { 
%>
        <div class="product-card">
            <a href="<%= request.getContextPath() %>/DettaglioProdottoServlet?idProdotto=<%= p.getId() %>" class="card-main-link" title="Vedi dettaglio"> </a>
            <% if (isAdmin) { %>
                <form action="<%= request.getContextPath() %>/ProdottoServlet" method="POST" class="admin-delete-form">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="idProdotto" value="<%= p.getId() %>">
                    <button type="submit" class="btn-delete-product" title="Elimina Prodotto">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </form>
            <% } %>

            <div class="product-image-container">
                <div class="card-badges">
                    <span class="badge highlight"><%= p.getTipo() %></span>
                    <% if (p.getCondizioneCollezionistica() != null && !p.getCondizioneCollezionistica().isEmpty()) { %>
                        <span class="badge">Grado <%= p.getCondizioneCollezionistica() %></span>
                    <% } %>
                </div>
                <img src="<%= request.getContextPath() %><%= p.getImageUrl() %>" alt="<%= p.getNome() %>">
            </div>
            
            <span class="product-brand"><%= p.getMarchio() %></span>
            <h3 class="product-name"><%= p.getNome() %></h3>
            
            <div class="card-bottom-row">
    			<div class="product-price">€ <%= String.format("%.2f", p.getPrezzo()) %></div>
    
    			<% 
        			// Mostriamo il carrello SOLO se NON è admin (quindi lo vedono i clienti loggati e gli ospiti non registrati)
        			if (!isAdmin) {
        				if (p.getInStock() > 0) { 
    			%>
    	<form class="quick-add-form" onsubmit="event.preventDefault(); aggiungiVeloceAJAX('<%= p.getId() %>');">
        	<button type="submit" class="btn-quick-add" title="Aggiungi al carrello">
            	<i class="fas fa-cart-plus"></i>
        	</button>
        </form>
    			<% 
            } else { 
    %>
        <button type="button" class="btn-quick-add" title="Prodotto Esaurito" disabled style="opacity: 0.5; cursor: not-allowed; border-color: gray; color: gray;">
            <i class="fas fa-box-open"></i>
        </button>
    <% 
            } // Fine if(inStock > 0)
        } // Fine if(!isAdmin)
    %>
			</div>
            
	</div>
<% 
    } // Fine del ciclo for (Prodotto p : lista)
} else { // Ramo else di if(lista != null && !lista.isEmpty())
%>
    <p class="empty-catalog-msg">Nessuna fotocamera corrisponde ai filtri selezionati.</p>
<% } %>
