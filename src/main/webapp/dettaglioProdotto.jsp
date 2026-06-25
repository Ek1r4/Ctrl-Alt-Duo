<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Prodotto" %>
<%@ page import="reframe.model.beans.Utente" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ReFrame - Dettaglio Prodotto</title>
    
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/variables.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/dettaglioProdotto.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/form.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="single-product-page">

    <%@ include file="/WEB-INF/components/header.jsp" %>
    
    <%
		Utente utenteDettaglio = (Utente) session.getAttribute("utente");
		boolean isAdmin = (utenteDettaglio != null && utenteDettaglio.getIsAdmin() == 1);
     %>

    <%
        // Recuperiamo il prodotto passato dalla Servlet
        Prodotto p = (Prodotto) request.getAttribute("prodotto");
        
        if (p != null) {
    %>
    <main class="product-container">
        
        <div class="product-gallery">
        	<div class="container-3d">
    		<model-viewer
        		src="<%= request.getContextPath() %><%= p.getModelUrl() %>"
        		alt="Modello 3D interattivo di <%= p.getMarchio() %> <%= p.getNome() %>" 
        		camera-controls 
        		auto-rotate 
        		rotation-per-second="30deg"
        		shadow-intensity="1" 
        		environment-image="neutral"
        		exposure="1.2">
    		</model-viewer>
			</div>
        </div>

        <div class="product-info">
        
        	<nav class="breadcrumb">
                <a href="<%= request.getContextPath() %>/index.jsp">Home</a> / 
                <a href="<%= request.getContextPath() %>/ProdottoServlet">Catalogo</a> / 
                <span><%= p.getMarchio() %></span>
            </nav>

            <div class="product-title-wrapper">
                <h1 class="product-title"><%= p.getNome() %></h1>

                <% if (isAdmin) { %>
                    <button type="button" class="btn-edit-pencil" id="open-edit-modal" title="Modifica Prodotto">
                        <i class="fas fa-pencil-alt"></i>
                    </button>
                <% } %>
            </div>
            
            <div class="product-price">€ <%= String.format("%.2f", p.getPrezzo()) %></div>
            
            <div class="product-description">
                <p><%= p.getDescrizione() %></p>
            </div>


            <form action="<%= request.getContextPath() %>/Carrello" method="POST" class="add-to-cart-form">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="idProdotto" value="<%= p.getId() %>">
                
                <div class="add-to-cart-section">
                    <div class="quantity-selector">
                        <button type="button" class="qty-btn" id="btn-minus" <%= isAdmin ? "disabled" : "" %>>-</button>
                        <input type="number" id="qty-input" name="quantita" class="qty-input" value="1" min="1" max="10" readonly>
                        <button type="button" class="qty-btn" id="btn-plus" <%= isAdmin ? "disabled" : "" %>>+</button>
                    </div>
                    
                    <button type="submit" class="btn-cta" <%= isAdmin ? "disabled-for-admin" : "" %>" <%= isAdmin ? "disabled" : "" %>>
                        Aggiungi al Carrello
                    </button>
                </div>
            </form>

            <div class="product-details-accordion">
                <details>
                    <summary>Specifiche Tecniche</summary>
                    <div class="accordion-content">
                        <table class="product-spec-table">
                            <tbody>
                                <tr>
                                    <td>Marchio</td>
                                    <td><%= p.getMarchio() %></td>
                                </tr>
                                <tr>
                                    <td>Modello</td>
                                    <td><%= p.getNome() %></td>
                                </tr>
                                <tr>
                                    <td>Numero Seriale</td>
                                    <td><%= p.getSeriale() %></td>
                                </tr>
                                <tr>
                                    <td>Tipologia</td>
                                    <td><%= p.getTipo() %></td>
                                </tr>
                                
                                <% if ("Usato".equalsIgnoreCase(p.getTipo())) { %>
                                    <tr>
                                        <td>Stato di Usura</td>
                                        <td><%= p.getStato() != null ? p.getStato() : "N/D" %></td>
                                    </tr>
                                    <tr>
                                        <td>Numero Scatti</td>
                                        <td><%= p.getNumeroScatti() > 0 ? p.getNumeroScatti() : "Non rilevato" %></td>
                                    </tr>
                                <% } %>

                                <% if ("Collezione".equalsIgnoreCase(p.getTipo())) { %>
                                    <tr>
                                        <td>Grado Collezionistico</td>
                                        <td><%= p.getCondizioneCollezionistica() != null ? p.getCondizioneCollezionistica() : "N/D" %></td>
                                    </tr>
                                    
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </details>
                
                <details>
                    <summary>Spedizione e Resi</summary>
                    <div class="accordion-content">
                        Costo di Spedizione fisso a 5€. <br> Consegna tracciata in 48h lavorative in tutta Italia. <br>
                        Reso garantito entro 14 giorni dalla ricezione del prodotto, a patto che le condizioni dello stesso non siano state alterate.
                    </div>
                </details>

                <details>
                    <summary>Garanzia ReFrame</summary>
                    <div class="accordion-content">
                        Tutti i nostri prodotti ricondizionati e da collezione sono testati dai nostri tecnici. Offriamo una garanzia di 2 anni su difetti meccanici non dichiarati.
                    </div>
                </details>
            </div>

        </div>
    </main>
    <% 
        } else { 
    %>
        <div class="product-container product-not-found-container">
            <h2>Prodotto non trovato</h2>
            <p>La fotocamera che stai cercando non esiste o è stata rimossa dal catalogo.</p>
            <a href="<%= request.getContextPath() %>/ProdottoServlet" class="product-not-found-link">Torna al catalogo</a>
        </div>
    <% 
        } 
    %>
	
	<% if (isAdmin) { %>
    <div id="edit-product-modal" class="edit-modal-overlay">
        
        <div class="film-container large modal-film-override">
            
            <div class="camera-icon">
                <svg class="icon-edit-modal" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </div>
            
            <button type="button" id="close-edit-modal" class="btn-close-modal" title="Chiudi">&times;</button>
            
            <h1 class="form-title">Modifica Prodotto</h1>
            
            <form action="<%= request.getContextPath() %>/AdminModificaProdottoServlet" method="POST">
                <input type="hidden" name="idProdotto" value="<%= p.getId() %>">
                
                <fieldset class="custom-input">
                    <legend>Nome Prodotto</legend>
                    <input type="text" id="edit-nome" name="nome" value="<%= p.getNome() %>" required>
                </fieldset>
                
                <fieldset class="custom-input">
                    <legend>Prezzo (€)</legend>
                    <input type="number" id="edit-prezzo" name="prezzo" step="0.01" value="<%= p.getPrezzo() %>" required>
                </fieldset>
                
                <fieldset class="custom-input">
                    <legend>Descrizione</legend>
                    <textarea id="edit-descrizione" name="descrizione" rows="4" class="custom-textarea" required><%= p.getDescrizione() %></textarea>
                </fieldset>
                
                <button type="submit" class="btn-cta" style="margin-top: 15px;">Salva Modifiche</button>
            </form>
        </div>
    </div>
    <% } %>
	
    <%@ include file="/WEB-INF/components/footer.jsp" %>

    <script src="<%= request.getContextPath() %>/js/dettaglio-prodotto.js"></script>
    <script type="module" src="https://ajax.googleapis.com/ajax/libs/model-viewer/3.4.0/model-viewer.min.js"></script>
</body>
</html>