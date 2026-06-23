<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Prodotto" %>
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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="single-product-page">

    <%@ include file="/WEB-INF/components/header.jsp" %>

    <%
        // Recuperiamo il prodotto passato dalla Servlet
        Prodotto p = (Prodotto) request.getAttribute("prodotto");
        
        if (p != null) {
    %>
    <main class="product-container">
        
        <div class="product-gallery">
            <img src="<%= request.getContextPath() %><%= p.getModelUrl() %>" alt="<%= p.getNome() %>" class="product-main-image">
        </div>

        <div class="product-info">
        
        	<nav class="breadcrumb">
                <a href="<%= request.getContextPath() %>/index.jsp">Home</a> / 
                <a href="<%= request.getContextPath() %>/ProdottoServlet">Catalogo</a> / 
                <span><%= p.getMarchio() %></span>
            </nav>

            <h1 class="product-title"><%= p.getNome() %></h1>
            
            <div class="product-price">€ <%= String.format("%.2f", p.getPrezzo()) %></div>
            
            <div class="product-description">
                <p><%= p.getDescrizione() %></p>
            </div>

            <form action="<%= request.getContextPath() %>/CarrelloServlet" method="POST" class="add-to-cart-form">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="idProdotto" value="<%= p.getId() %>">
                
                <div class="add-to-cart-section">
                    <div class="quantity-selector">
                        <button type="button" class="qty-btn" id="btn-minus">-</button>
                        <input type="number" id="qty-input" name="quantita" class="qty-input" value="1" min="1" max="10" readonly>
                        <button type="button" class="qty-btn" id="btn-plus">+</button>
                    </div>
                    
                    <button type="submit" class="btn-add-cart">Aggiungi al Carrello</button>
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
                        Spedizione gratuita per ordini superiori a 150€. Consegna tracciata in 48/72h lavorative in tutta Italia. <br>
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

    <%@ include file="/WEB-INF/components/footer.jsp" %>

    <script src="<%= request.getContextPath() %>/js/dettaglio-prodotto.js"></script>
</body>
</html>