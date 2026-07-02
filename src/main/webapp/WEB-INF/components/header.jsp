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
            <img src="${pageContext.request.contextPath}/assets/logoReFrame.png" alt="Logo ReFrame" class="logo-img"> 
            <div class="logo-text">
                <span class="logo-title">REFRAME</span>
            </div>
        </a>

        <nav class="header-nav">
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Usato">RICONDIZIONATE</a></li>
				<li><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Nuovo">NUOVE</a></li>
				<li><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Collezione">COLLEZIONISMO</a></li>
            </ul>
        </nav>
        
        <div class="header-search">
        <form action="<%= request.getContextPath() %>/ProdottoServlet" method="GET" class="header-search-form">
    		<input type="text" name="search" class="search-input" placeholder="Cerca..." 
          		 value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
    		<button type="submit" class="search-btn">
        		<i class="fas fa-search"></i>
    		</button>
		</form>
		</div>
		
		<button class="hamburger-btn" id="mobileMenuBtn">
            <i class="fas fa-bars"></i>
        </button>

        <div class="header-icons">
            
            <ul class="nav-links mobile-only-links">
                <li><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Usato">RICONDIZIONATE</a></li>
				<li><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Nuovo">NUOVE</a></li>
				<li><a href="${pageContext.request.contextPath}/ProdottoServlet?tipo=Collezione">COLLEZIONISMO</a></li>
            </ul>

            
            
            <% if (session.getAttribute("utente") != null) { 
            	reframe.model.beans.Utente userMenu = (reframe.model.beans.Utente) session.getAttribute("utente");
            %>
                <% if (userMenu.getIsAdmin() > 0) { %>
                
                    <a href="${pageContext.request.contextPath}/admin/gestioneTicket.jsp" class="icon-link" title="Gestione Ticket">
                        <i class="fas fa-headset"></i>
                        <span class="icon-label">Assistenza</span> 
                    </a>
                    
                    <a href="${pageContext.request.contextPath}/PannelloAdminServlet" class="icon-link" title="Pannello di Gestione">
                    	<i class="fas fa-sliders-h"></i> <span class="icon-label">Gestione</span>
                	</a>
                	
                <% } else { %>
                
                    <a href="${pageContext.request.contextPath}/ProfiloServlet" class="icon-link" title="Area Personale">
                        <i class="far fa-user-circle"></i>
                        <span class="icon-label">Il Mio Profilo</span> 
                    </a>
                    
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
                            <div class="mc-item" id="mc-item-<%= mItem.getIdProdotto() %>">
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
                            <span id="mc-totale-complessivo">€ <%= String.format("%.2f", mc.getTotale()) %></span>
                        </div>
                        <a href="${pageContext.request.contextPath}/common/checkout.jsp" class="btn-cta mc-checkout-btn">VAI AL CHECKOUT</a>
                    </div>
                    <% } %>
                </div>
                <% } %>
            </div>
            
            <% if (session.getAttribute("utente") != null) { 
            	reframe.model.beans.Utente userMenu = (reframe.model.beans.Utente) session.getAttribute("utente");
            %>
                <% if (userMenu.getIsAdmin() > 0) { %>
                
                    <a href="${pageContext.request.contextPath}/admin/gestioneTicket.jsp" class="icon-link" title="pannello di assistenza">
                        <i class="fas fa-headset"></i>
                        <span class="icon-label">Assistenza</span> 
                    </a>
                    
                    <a href="${pageContext.request.contextPath}/PannelloAdminServlet" class="icon-link" title="Pannello di Gestione">
                    	<i class="fas fa-sliders-h"></i> <span class="icon-label">Gestione</span>
                	</a>
                	
                <% } else { %>
                
                    <a href="${pageContext.request.contextPath}/ProfiloServlet" class="icon-link" title="Area Personale">
                        <i class="far fa-user-circle"></i>
                        <span class="icon-label">Il Mio Profilo</span> 
                    </a>
                    
                    
            		
                <% } %>
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