<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="reframe.model.beans.Prodotto" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ReFrame - Catalogo</title>
    
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/variables.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/vetrina.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body class="catalog-page">

    <%@ include file="/WEB-INF/components/header.jsp" %>
    
    <%@ page import="reframe.model.beans.Utente" %>
	<%
    Utente utenteLoggato = (Utente) session.getAttribute("utente");
    boolean isAdmin = (utenteLoggato != null && utenteLoggato.getIsAdmin() > 0);
	%>

    <main class="catalog-container">
        
        <div class="catalog-header">
            <h1><%= request.getAttribute("titoloVetrina") != null ? request.getAttribute("titoloVetrina") : "Catalogo" %></h1>
            <p>Esplora la nostra collezione di storie racchiuse in ogni obiettivo.</p>
        </div>

        <%-- Messaggio di errore se il DB è down --%>
        <% if (request.getAttribute("erroreDatabase") != null) { %>
            <div class="db-error-message">
                <p><i class="fas fa-exclamation-triangle"></i> <%= request.getAttribute("erroreDatabase") %></p>
            </div>
        <% } %>

        <div class="catalog-body">
            
            <aside class="catalog-sidebar">
            
            	<div class="sidebar-search">
                    <input type="text" class="catalog-search-input" placeholder="Cerca modello o seriale...">
                    <button type="button" class="catalog-search-btn" title="Cerca">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            
                <h3>FILTRA PER</h3>
                
                <div class="filter-group">
                    <h4>Marca</h4>
                    <label class="filter-label"><input type="checkbox" name="marca" value="Leica"> Leica</label>
                    <label class="filter-label"><input type="checkbox" name="marca" value="Hasselblad"> Hasselblad</label>
                    <label class="filter-label"><input type="checkbox" name="marca" value="Nikon"> Nikon</label>
                    <label class="filter-label"><input type="checkbox" name="marca" value="Canon"> Canon</label>
                    <label class="filter-label"><input type="checkbox" name="marca" value="Sony"> Sony</label>
                    <label class="filter-label"><input type="checkbox" name="marca" value="Fujifilm"> Fujifilm</label>
                    <label class="filter-label"><input type="checkbox" name="marca" value="Polaroid"> Polaroid</label>
                </div>

                <div class="filter-group">
                    <h4>Prezzo</h4>
                    <label class="filter-label"><input type="checkbox" name="prezzo" value="0-500"> Fino a 500 €</label>
                    <label class="filter-label"><input type="checkbox" name="prezzo" value="500-1000"> da 500 € a 1.000 €</label>
                    <label class="filter-label"><input type="checkbox" name="prezzo" value="1000-2000"> da 1.000 € a 2.000 €</label>
                    <label class="filter-label"><input type="checkbox" name="prezzo" value="2000-max"> Oltre 2.000 €</label>
                </div>
            </aside>

            <div class="catalog-main">
                <div class="products-grid">
                    <%
                        List<Prodotto> lista = (List<Prodotto>) request.getAttribute("listaProdotti");
                        
                        if (lista != null && !lista.isEmpty()) {
                            for (Prodotto p : lista) { 
                    %>
                            <div class="product-card">
                                
                                <a href="<%= request.getContextPath() %>/DettaglioProdottoServlet?id=<%= p.getId() %>" class="card-main-link" title="Vedi dettaglio"></a>
                                
                                <%-- SEZIONE ADMIN: Tasto Elimina --%>
                                <% if (isAdmin) { %>
                                    <form action="<%= request.getContextPath() %>/ProdottoServlet" method="POST" class="admin-delete-form">
                                    	<input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="idProdotto" value="<%= p.getId() %>">
                                        <button type="submit" class="btn-delete-product" title="Elimina Prodotto" onclick="return confirm('Sei sicuro di voler eliminare definitivamente questo prodotto?');">
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
                                
                                <div class="product-price">
                                    € <%= String.format("%.2f", p.getPrezzo()) %>
                                </div>
                            </div>
                    <% 
                            }
                        } else if (lista != null && lista.isEmpty()) { 
                    %>
                        <p class="empty-catalog-msg">Nessuna fotocamera trovata per questa categoria.</p>
                    <% } %>
                </div>
            </div> </div> </main>

    <%@ include file="/WEB-INF/components/footer.jsp" %>

</body>
</html>