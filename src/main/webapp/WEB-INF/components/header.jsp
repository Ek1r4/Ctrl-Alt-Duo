<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Carrello" %>
<%@ page import="reframe.model.beans.CarrelloItem" %>
<% 
   // Capiamo se siamo nella pagina carrello o checkout per nascondere la preview
   String currentURI = request.getRequestURI();
   boolean isCartOrCheckout = currentURI.endsWith("carrello.jsp") || currentURI.endsWith("checkout.jsp"); 
   
   // Calcoliamo la quantità per il badge istantaneamente lato Server
   int qtaAttuale = 0;
   Carrello c = (Carrello) session.getAttribute("carrello");
   if(c != null) { qtaAttuale = c.getTotaleArticoli(); }
%>

<script>const contestoReFrame = '${pageContext.request.contextPath}';</script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

<header class="site-header">
    <div class="header-container">
        
        <a href="${pageContext.request.contextPath}/index.jsp" class="header-logo">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" class="logo-icon" width="40" height="40" fill="currentColor">
                <path d="M 10 45 L 10 10 L 55 10 L 43 22 L 22 22 L 22 57 Z" />
                <path d="M 65 10 L 110 10 L 110 40 L 120 40 L 104 60 L 88 40 L 98 40 L 98 22 L 53 22 Z" />
                <path d="M 110 75 L 110 110 L 65 110 L 77 98 L 98 98 L 98 63 Z" />
                <path d="M 55 110 L 10 110 L 10 80 L 0 80 L 16 60 L 32 80 L 22 80 L 22 98 L 67 98 Z" />

                <path d="M 60 26 A 34 34 0 1 1 59.9 26 Z M 60 36 A 24 24 0 1 0 60.1 36 Z" />

                <mask id="aperture-mask">
                    <circle cx="60" cy="60" r="24" fill="white" />
                    <g stroke="black" stroke-width="2.5" fill="none" stroke-linecap="round">
                        <path d="M 60 36 Q 45 48 50 66" />
                        <path d="M 60 36 Q 45 48 50 66" transform="rotate(60 60 60)" />
                        <path d="M 60 36 Q 45 48 50 66" transform="rotate(120 60 60)" />
                        <path d="M 60 36 Q 45 48 50 66" transform="rotate(180 60 60)" />
                        <path d="M 60 36 Q 45 48 50 66" transform="rotate(240 60 60)" />
                        <path d="M 60 36 Q 45 48 50 66" transform="rotate(300 60 60)" />
                    </g>
                    <circle cx="60" cy="60" r="5" fill="black" />
                </mask>
                <circle cx="60" cy="60" r="24" mask="url(#aperture-mask)" />
            </svg> 
            <div class="logo-text">
                <span class="logo-title">REFRAME</span>
            </div>
        </a>

        <nav class="header-nav">
            <ul class="nav-links">
                <li><a href="#">RICONDIZIONATE</a></li>
                <li><a href="#">NUOVE</a></li>
                <li><a href="#">COLLEZIONISMO</a></li>
            </ul>
        </nav>
        
        <div class="header-search">
            <input type="text" class="search-input" placeholder="Cerca prodotti...">
            <button type="button" class="search-btn" title="Cerca">
                <i class="fas fa-search"></i>
            </button>
        </div>

        <button class="hamburger-btn" id="mobileMenuBtn">
            <i class="fas fa-bars"></i>
        </button>

        <div class="header-icons">
            
            <ul class="nav-links mobile-only-links">
                <li><a href="#">RICONDIZIONATE</a></li>
                <li><a href="#">NUOVE</a></li>
                <li><a href="#">COLLEZIONISMO</a></li>
            </ul>

            <div class="cart-wrapper">
                <a href="${pageContext.request.contextPath}/common/carrello.jsp" class="icon-link cart-link" title="Carrello">
                    <i class="fas fa-shopping-cart"></i>
                    <span class="icon-label">Carrello</span> 
                    
                    <span class="cart-badge" style="<%= qtaAttuale == 0 ? "display:none;" : "" %>"><%= qtaAttuale > 0 ? qtaAttuale : "" %></span>
                </a>

                <% if (!isCartOrCheckout) { %>
                <div class="mini-cart-preview">
                    
                    <div class="mini-cart-header">
                        <h4>IL TUO CARRELLO</h4>
                    </div>
                    
                    <div class="mini-cart-items">
                        <%
                            Carrello mc = (Carrello) session.getAttribute("carrello");
                            if (mc == null || mc.getItems().isEmpty()) {
                        %>
                            <p class="empty-mc">Nessun articolo presente.</p>
                        <% } else {
                            for (CarrelloItem mItem : mc.getItems()) {
                        %>
                            <div class="mc-item">
                                <div class="mc-item-info">
                                    <span class="mc-name"><%= mItem.getNome() %></span>
                                    <span class="mc-qty">Quantità: <%= mItem.getQuantita() %></span>
                                </div>
                                <div class="mc-item-actions">
                                    <span class="mc-price">€ <%= String.format("%.2f", mItem.getPrezzoTotale()) %></span>
                                    <button type="button" class="mc-remove-btn" onclick="rimuoviDaMiniCart('<%= mItem.getIdProdotto() %>')" title="Rimuovi">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </div>
                            </div>
                        <%  }
                           } %>
                    </div>
                    
                    <% if (mc != null && !mc.getItems().isEmpty()) { %>
                    <div class="mini-cart-footer">
                        <div class="mc-total">
                            <span>TOTALE:</span>
                            <span>€ <%= String.format("%.2f", mc.getTotale()) %></span>
                        </div>
                        <a href="${pageContext.request.contextPath}/common/checkout.jsp" class="btn-cta mc-checkout-btn">VAI AL CHECKOUT</a>
                    </div>
                    <% } %>
                </div>
                <% } %>
            </div>
            
            <% if (session.getAttribute("utente") != null) { %>
                <a href="${pageContext.request.contextPath}/common/profilo.jsp" class="icon-link" title="Area Personale">
                    <i class="far fa-user-circle"></i>
                    <span class="icon-label">Il Mio Profilo</span> </a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="icon-link logout-link" title="Esci">
                    <i class="fas fa-sign-out-alt"></i>
                    <span class="icon-label">Esci</span> </a>
            <% } else { %>
                <a href="${pageContext.request.contextPath}/login.jsp" class="login-link">ACCEDI</a>
            <% } %>
            
        </div>
        
    </div>
</header>

<script src="${pageContext.request.contextPath}/js/header.js"></script>