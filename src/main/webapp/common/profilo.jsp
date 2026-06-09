<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Utente" %>

<%
    // Controllo di sicurezza: se non sei loggato, fuori di qui!
    Utente utenteLoggato = (Utente) session.getAttribute("utente");
    if (utenteLoggato == null) {
    	response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Test Profilo - ReFrame</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* Qualche stile inline temporaneo per i messaggi di notifica */
        .alert-box { padding: 15px; margin-bottom: 20px; border-radius: 5px; text-align: center; font-weight: bold; }
        .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .form-section { margin-bottom: 40px; border-bottom: 2px dashed #ccc; padding-bottom: 20px; }
    </style>
</head>
<body>

<div class="film-container">
    <h1>AREA PERSONALE DI <%= utenteLoggato.getUsername().toUpperCase() %></h1>
    
    <% 
        String success = request.getParameter("success");
        String error = request.getParameter("error");
        
        if (success != null) {
    %>
        <div class="alert-box alert-success">
            Operazione completata con successo! (Codice: <%= success %>)
        </div>
    <%  } else if (error != null) { %>
        <div class="alert-box alert-error">
            Ops! C'è stato un problema. (Codice errore: <%= error %>)
        </div>
    <%  } %>

    <div class="form-section">
        <h2>Aggiorna Anagrafica</h2>
        <form action="${pageContext.request.contextPath}/ProfiloServlet" method="POST">
            <input type="hidden" name="action" value="aggiornaAnagrafica">
            
            <div class="form-row">
                <fieldset class="custom-input">
                    <legend>Telefono (Obbligatorio)</legend>
                    <input type="tel" name="telefono" value="<%= utenteLoggato.getTelefono() %>" required>
                </fieldset>
            </div>
            
            <fieldset class="custom-input">
                <legend>La tua Bio</legend>
                <textarea name="bio" rows="4" style="width: 100%; border: none; outline: none; resize: vertical;"><%= utenteLoggato.getBio() != null ? utenteLoggato.getBio() : "" %></textarea>
            </fieldset>
            
            <button type="submit" class="btn-cta">Salva Anagrafica</button>
        </form>
    </div>

    <div class="form-section">
        <h2>Cambio Password</h2>
        <form action="${pageContext.request.contextPath}/ProfiloServlet" method="POST">
            <input type="hidden" name="action" value="cambioPassword">
            
            <fieldset class="custom-input">
                <legend>Password Attuale</legend>
                <input type="password" name="vecchiaPassword" required>
            </fieldset>
            
            <div class="form-row">
                <fieldset class="custom-input">
                    <legend>Nuova Password</legend>
                    <input type="password" name="nuovaPassword" required minlength="8">
                </fieldset>
                <fieldset class="custom-input">
                    <legend>Conferma Nuova Password</legend>
                    <input type="password" name="confermaPassword" required minlength="8">
                </fieldset>
            </div>
            
            <button type="submit" class="btn-cta">Aggiorna Password</button>
        </form>
    </div>

    <div class="form-section">
        <h2>Aggiungi Indirizzo di Spedizione</h2>
        <form action="${pageContext.request.contextPath}/ProfiloServlet" method="POST">
            <input type="hidden" name="action" value="salvaSpedizione">
            
            <div class="form-row">
                <fieldset class="custom-input"><legend>Via/Piazza</legend><input type="text" name="via" required></fieldset>
                <fieldset class="custom-input"><legend>Civico</legend><input type="text" name="civico" required></fieldset>
            </div>
            
            <div class="form-row">
                <fieldset class="custom-input"><legend>Città</legend><input type="text" name="citta" required></fieldset>
                <fieldset class="custom-input"><legend>CAP</legend><input type="text" name="cap" required maxlength="5"></fieldset>
            </div>
            
            <div class="form-row">
                <fieldset class="custom-input"><legend>Provincia (Sigla)</legend><input type="text" name="provincia" required maxlength="2"></fieldset>
                <fieldset class="custom-input"><legend>Paese</legend><input type="text" name="paese" value="Italia" required></fieldset>
            </div>
            
            <fieldset class="custom-input">
                <legend>Note per il corriere</legend>
                <input type="text" name="note" placeholder="Es. Lasciare al portiere">
            </fieldset>
            
            <button type="submit" class="btn-cta">Salva Indirizzo</button>
        </form>
    </div>

    <div class="form-section" style="border-bottom: none;">
        <h2>Aggiungi Metodo di Pagamento</h2>
        <form action="${pageContext.request.contextPath}/ProfiloServlet" method="POST">
            <input type="hidden" name="action" value="salvaPagamento">
            
            <fieldset class="custom-input">
                <legend>Nome Intestatario</legend>
                <input type="text" name="nomeIntestatario" required>
            </fieldset>
            
            <div class="form-row">
                <fieldset class="custom-input">
                    <legend>Circuito</legend>
                    <select name="circuito" required style="width: 100%; border: none; outline: none; background: transparent;">
                        <option value="Visa">Visa</option>
                        <option value="Mastercard">Mastercard</option>
                        <option value="American Express">American Express</option>
                    </select>
                </fieldset>
                <fieldset class="custom-input">
                    <legend>Numero Carta</legend>
                    <input type="text" name="numeroCarta" required minlength="15" maxlength="16">
                </fieldset>
            </div>
            
            <div class="form-row">
                <fieldset class="custom-input">
                    <legend>Scadenza (MM/YY)</legend>
                    <input type="text" name="dataScadenza" required maxlength="5" placeholder="12/28">
                </fieldset>
                <fieldset class="custom-input">
                    <legend>CVV</legend>
                    <input type="text" name="cvv" required minlength="3" maxlength="4">
                </fieldset>
            </div>
            
            <button type="submit" class="btn-cta">Salva Carta</button>
        </form>
    </div>

</div>

</body>
</html>