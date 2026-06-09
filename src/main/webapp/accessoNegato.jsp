<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accesso Negato - ReFrame</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
    
</head>

<body>

    <div class="auth-wrapper">
        
        <div class="film-container">
            
            <div class="camera-icon">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#A93F35" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
            </div>
            
            <h1 class="form-title" style="color: var(--rosso-ruggine);">ACCESSO NEGATO</h1>
            
            <p class="denied-text">
                Spiacenti, non hai le autorizzazioni necessarie per visualizzare questa pagina. 
                L'area richiesta è riservata agli amministratori di ReFrame.
            </p>
            
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-cta">TORNA ALLA HOME</a>
            
        </div>
        
        <footer class="site-footer">
            &copy; 2026 ReFrame
        </footer>

    </div>

</body>
</html>