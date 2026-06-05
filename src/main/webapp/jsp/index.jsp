<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Utente" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Home - ReFrame</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
        .container { background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto; text-align: center; }
        .navbar { display: flex; justify-content: space-between; align-items: center; background-color: #2c3e50; color: white; padding: 15px 20px; border-radius: 8px; margin-bottom: 20px;}
        .navbar a { color: white; text-decoration: none; font-weight: bold; }
        .navbar a:hover { text-decoration: underline; }
        .btn { display: inline-block; padding: 10px 20px; margin: 10px; background-color: #3498db; color: white; text-decoration: none; border-radius: 4px; font-weight: bold;}
        .btn:hover { background-color: #2980b9; }
        .test-box { margin-top: 30px; padding: 20px; background-color: #e9ecef; border-radius: 8px; border: 2px dashed #ccc;}
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
                    <a href="${pageContext.request.contextPath}/LogoutServlet" style="color: #ff7675;">Esci</a>
                <% } else { %>
                    <a href="${pageContext.request.contextPath}/jsp/login.jsp">Accedi</a> |
                    <a href="${pageContext.request.contextPath}/jsp/registrazione.jsp">Registrati</a>
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
</body>
</html>