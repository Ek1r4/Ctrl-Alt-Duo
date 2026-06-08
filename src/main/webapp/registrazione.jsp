<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione - ReFrame</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
</head>

<body>

    <div class="auth-wrapper">
        
        <div class="film-container large">
            
        <div class="camera-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#2A2A2A" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <path d="M14.31 8l5.74 9.94M9.69 8h11.48M7.38 12l5.74-9.94M9.69 16L3.95 6.06M14.31 16H2.83m13.79-4l-5.74 9.94"></path>
            </svg>
        </div>
            
            <h1 class="form-title">ENTRA IN REFRAME</h1>
            
            <%
            	List<String> errors = (List<String>) request.getAttribute("errors");
                if (errors != null && !errors.isEmpty()) {
            %>
                <div class="error-box">
                    <ul>
                            <li><%= errors.get(0) %></li>
                    </ul>
                </div>
            <%
                }
            %>
            
			<form action="${pageContext.request.contextPath}/RegistrazioneServlet" method="POST">
                
                <div class="form-row">
                    <fieldset class="custom-input">
                        <legend>Nome</legend>
                        <input type="text" name="nome">
                    </fieldset>

                    <fieldset class="custom-input">
                        <legend>Cognome</legend>
                        <input type="text" name="cognome">
                    </fieldset>
                </div>
                               
                <fieldset class="custom-input">
                    <legend>E-mail</legend>
                    <input type="email" name="email">
                </fieldset>
                    
                <fieldset class="custom-input">
                    <legend>Password</legend>
                    <input type="password" name="password">
                </fieldset>
                
                <fieldset class="custom-input">
                        <legend>Conferma Password</legend>
                        <input type="password" id="confermaPassword" name="confermaPassword">
                </fieldset>
                
                <div class="form-row">
                <fieldset class="custom-input">
                    <legend>Username</legend>
                    <input type="text" name="username">
                </fieldset>
                
				<fieldset class="custom-input">
    				<legend>Telefono</legend>
    				<input type="tel" name="telefono" pattern="[0-9]" title="Inserisci un numero di telefono valido" required>
				</fieldset>
              	</div>
              	
                <button type="submit" class="btn-cta">REGISTRATI</button>
                
            </form>
            
            <a href="${pageContext.request.contextPath}/jsp/login.jsp" class="form-link">Hai già un account? Accedi qui</a>
       
    </div>
            
        <footer class="site-footer">
            &copy; 2026 ReFrame
        </footer>
    
</body>
</html>