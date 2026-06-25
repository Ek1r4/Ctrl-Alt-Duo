<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
                
                    <a href="${pageContext.request.contextPath}/admin/profiloAdmin.jsp" class="icon-link" title="Area Personale Admin">
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
                    
                    <a href="${pageContext.request.contextPath}/carrello.jsp" class="icon-link cart-link" title="Carrello">
                		<i class="fas fa-shopping-cart"></i>
                		<span class="icon-label">Carrello</span> <span class="cart-badge"></span>
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
     
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        const mobileMenuBtn = document.getElementById('mobileMenuBtn');
        const siteHeader = document.querySelector('.site-header');
        
        if (mobileMenuBtn && siteHeader) {
            mobileMenuBtn.addEventListener('click', function() {
                // Aggiunge o toglie la classe "menu-open" all'header
                siteHeader.classList.toggle('menu-open');
            });
        }
    });
	</script>
    
    </header>