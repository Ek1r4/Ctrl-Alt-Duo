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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/form.css">
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

        <% if (request.getAttribute("erroreDatabase") != null) { %>
            <div class="db-error-message">
                <p><i class="fas fa-exclamation-triangle"></i> <%= request.getAttribute("erroreDatabase") %></p>
            </div>
        <% } %>

        <div class="catalog-body">
        
        <div class="mobile-filter-bar">
            <button class="btn-toggle-filters" id="btn-toggle-filters">
                <i class="fas fa-sliders-h"></i> Filtri
            </button>
        </div>

        <aside class="catalog-sidebar">
            <button class="btn-close-filters" id="btn-close-filters" title="Chiudi">&times;</button>

            
            	<div class="sidebar-search">
                    <input type="text" class="catalog-search-input" placeholder="Cerca..." 
                           value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                    <button type="button" class="catalog-search-btn" title="Cerca">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            
                <div class="sidebar-title-container">
                    <h3>FILTRA PER</h3>
                    <button type="button" id="btn-reset-filtri" class="btn-reset-filtri" title="Azzera filtri">
                        <i class="fas fa-eraser"></i>
                    </button>
                </div>
                
                <div class="filter-group">
                    <h4>Marca</h4>
                    <%
                        // Recuperiamo la lista di stringhe passata dalla Servlet
                        List<String> marcheDisponibili = (List<String>) request.getAttribute("marcheDisponibili");
                        
                        if (marcheDisponibili != null && !marcheDisponibili.isEmpty()) {
                            for (String marca : marcheDisponibili) {
                    %>
                                <label class="filter-label">
                                    <input type="checkbox" name="marca" value="<%= marca %>"> <%= marca %>
                                </label>
                    <%
                            }
                        } else {
                    %>
                            <p class="empty-catalog-msg" >Nessuna marca disponibile</p>
                    <%  } %>
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
                <div class="products-grid" id="grid-container">
                    <jsp:include page="/WEB-INF/components/griglia-prodotti.jsp" />
                </div>
            </div>
        </div>
        
    </main>

    <%@ include file="/WEB-INF/components/footer.jsp" %>
	
	<script>
        // Rendiamo il contextPath globale per farlo leggere al file .js
        const contextPath = '<%= request.getContextPath() %>';
    </script>
    <script src="<%= request.getContextPath() %>/js/filtri-catalogo.js"></script>
    
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
    
</body>
</html>