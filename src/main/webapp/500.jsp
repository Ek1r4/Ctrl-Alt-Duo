<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Errore 500 - Errore Interno | ReFrame</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="film-container">
            
            <div class="camera-icon">
                <svg class="icon-rust" viewBox="0 0 24 24" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                    <line x1="12" y1="9" x2="12" y2="13"></line>
                    <line x1="12" y1="17" x2="12.01" y2="17"></line>
                </svg>
            </div>
    
            <h1 class="form-title text-rust">ERRORE 500</h1>
            
            <p class="denied-text">
                L'otturatore si è inceppato. Stiamo riscontrando un problema interno del server durante lo sviluppo della tua richiesta.<br>
                Il nostro team tecnico è già in camera oscura per risolvere il problema.
            </p>
            
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-cta">RIPROVA DALLA HOME</a>
            
        </div>
        
        <footer class="site-footer">
            &copy; 2026 ReFrame
        </footer>
    </div>
</body>
</html>