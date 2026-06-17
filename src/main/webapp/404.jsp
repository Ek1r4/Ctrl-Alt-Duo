<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Errore 404 - Pagina Non Trovata | ReFrame</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="film-container">
            
            <div class="camera-icon">
                <svg class="icon-rust" viewBox="0 0 24 24" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="22" y1="2" x2="2" y2="22"></line>
                    <path d="M12 2a10 10 0 0 1 10 10"></path>
                </svg>
            </div>
    
            <h1 class="form-title text-rust" >ERRORE 404</h1>
            
            <p class="denied-text">
                Soggetto non a fuoco. La pagina che stai cercando non esiste o è stata spostata.<br>
                Forse hai inserito un rullino sbagliato?
            </p>
            
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-cta">TORNA ALLA HOME</a>
            
        </div>
        
        <footer class="site-footer">
            &copy; 2026 ReFrame
        </footer>
    </div>
</body>
</html>