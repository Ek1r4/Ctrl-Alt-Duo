<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<head>
    <meta charset="UTF-8">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>


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
                <span class="logo-subtitle">STORIE DI FOTOCAMERE | RICERCA & RESTAURO</span>
            </div>
        </a>

        <nav class="header-nav">
            <ul class="nav-links">
                <li><a href="#">RICONDIZIONATE</a></li>
                <li><a href="#">NUOVE</a></li>
                <li><a href="#">COLLEZIONISMO</a></li>
                <!-- Da aggiornare quando ci saranno le pagine -->
            </ul>
        </nav>
        
        <div class="header-search">
            <input type="text" class="search-input" placeholder="Cerca prodotti...">
            <button type="button" class="search-btn" title="Cerca">
                <i class="fas fa-search"></i>
            </button>
        </div>

        <div class="header-icons">
            <a href="${pageContext.request.contextPath}/carrello.jsp" class="icon-link cart-link" title="Carrello">
                <i class="fas fa-shopping-cart"></i>
                <span class="cart-badge"></span> <!-- Da aggiornare quando ci sarà il carello -->
            </a>
            <a href="${pageContext.request.contextPath}/common/profilo.jsp" class="icon-link" title="Area Personale">
                <i class="far fa-user-circle"></i>
            </a>
        </div>
        
    </div>
</header>