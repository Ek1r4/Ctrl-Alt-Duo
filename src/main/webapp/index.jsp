<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Utente" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Home - ReFrame</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
        .container { background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto; text-align: center; }
        .navbar { display: flex; justify-content: space-between; align-items: center; background-color: #2c3e50; color: white; padding: 15px 20px; border-radius: 8px; margin-bottom: 20px;}
        .navbar a { color: white; text-decoration: none; font-weight: bold; }
        .navbar a:hover { text-decoration: underline; }
        .btn { display: inline-block; padding: 10px 20px; margin: 10px; background-color: #3498db; color: white; text-decoration: none; border-radius: 4px; font-weight: bold;}
        .btn:hover { background-color: #2980b9; }
        .test-box { margin-top: 30px; padding: 20px; background-color: #e9ecef; border-radius: 8px; border: 2px dashed #ccc;}
        
        /* Stile aggiuntivo per il bottone del profilo nella navbar */
        .nav-btn-profilo { background-color: #f39c12; padding: 5px 10px; border-radius: 4px; margin-right: 15px; }
        .nav-btn-profilo:hover { background-color: #e67e22; text-decoration: none !important; }
    </style>
</head>
<body>
    <div class="container">
        
        <%
            // 1. IL CONTROLLO DELLA RAM: Cerco lo scatolone "utente"
            Utente utenteLoggato = (Utente) session.getAttribute("utente");
        %>

        <div class="navbar">
            <div style="font-size: 1.2em; font-weight: bold;">📷 ReFrame</div>
            <div>
                <% if (utenteLoggato != null) { %>
                    <span style="margin-right: 15px;">Benvenuto, <b><%= utenteLoggato.getNome() %></b>!</span>
                    
                    <a href="<%= request.getContextPath() %>/ProfiloServlet" class="nav-btn-profilo">Il mio Profilo</a>
                    
                    <a href="${pageContext.request.contextPath}/LogoutServlet" style="color: #ff7675;">Esci</a>
                <% } else { %>
                    <a href="${pageContext.request.contextPath}/login.jsp">Accedi</a> |
                    <a href="${pageContext.request.contextPath}/registrazione.jsp">Registrati</a>
                <% } %>
            </div>
        </div>

        <h1>Il mondo della fotografia</h1>
        <p>Acquista e vendi fotocamere nuove, usate e da collezione.</p>

        <div class="test-box">
            <h3>Area di Collaudo (Testa i Filtri!)</h3>
            <p>Clicca sui bottoni qui sotto per verificare se la sicurezza funziona:</p>
            
            <a href="${pageContext.request.contextPath}/common/provaUtente.jsp" class="btn">Test Area Cliente (/common/)</a>
            
            <a href="${pageContext.request.contextPath}/admin/provaAdmin.jsp" class="btn">Test Area Admin (/admin/)</a>
        </div>

    </div>

<!-- SCRIPT PER NOTIFICA DI LOGIN CON SUCCESSO !!!!!!!!!!!!! -->
<script>
document.addEventListener("DOMContentLoaded", function() {
    // 1. Legge i parametri dall'URL
    const urlParams = new URLSearchParams(window.location.search);
    
    // 2. Se trova il successo del login, lancia la notifica
    if (urlParams.get("success") === "login") {
        mostraNotifica("Login effettuato con successo!", "success-banner");
        
        // 3. Magia: pulisce l'URL cancellando "?success=login" senza ricaricare la pagina!
        // Così se l'utente fa F5 (aggiorna), la notifica non riappare all'infinito.
        window.history.replaceState({}, document.title, window.location.pathname);
    }

    // Funzione Utility per disegnare il banner
    function mostraNotifica(messaggio, classeCss) {
        const banner = document.createElement("div");
        banner.textContent = messaggio;
        banner.classList.add("notification-banner", classeCss);
        
        // Visto che in index.jsp usiamo la classe .container invece di .film-container
        const container = document.querySelector('.container');
        if (container) {
            container.insertBefore(banner, container.firstChild);
        } else {
            document.body.insertBefore(banner, document.body.firstChild);
        }
        
        // Fa sparire il banner dopo 4 secondi
        setTimeout(() => { banner.remove(); }, 4000);
    }
});
</script>

</body>
</html>