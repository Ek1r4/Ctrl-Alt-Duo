<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione - ReFrame</title>
    <style>
        /* Stili condivisi con la login per coerenza visiva */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh; /* Permette alla pagina di allungarsi se lo schermo è piccolo */
            margin: 0;
            padding: 20px;
            box-sizing: border-box;
        }
        .register-container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 450px; /* Leggermente più largo del login */
            text-align: center;
        }
        h2 {
            color: #333;
            margin-bottom: 10px;
        }
        p.subtitle {
            color: #666;
            margin-bottom: 20px;
            font-size: 14px;
        }
        /* Griglia Flexbox per affiancare Nome e Cognome sulla stessa riga */
        .form-row {
            display: flex;
            gap: 15px;
        }
        .form-row .form-group {
            flex: 1;
        }
        .form-group {
            margin-bottom: 15px;
            text-align: left;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
            font-size: 14px;
        }
        .form-group input {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box; 
        }
        .btn-submit {
            width: 100%;
            padding: 12px;
            background-color: #2c3e50;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            margin-top: 10px;
            font-weight: bold;
        }
        .btn-submit:hover {
            background-color: #1a252f;
        }
        .login-link {
            margin-top: 20px;
            display: block;
            font-size: 14px;
            color: #666;
            text-decoration: none;
        }
        .login-link:hover {
            text-decoration: underline;
        }
        /* Box degli errori */
        .error-box {
            background-color: #ffe6e6;
            border: 1px solid #ff9999;
            color: #cc0000;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
            text-align: left;
            font-size: 14px;
        }
        .error-box ul {
            margin: 0;
            padding-left: 20px;
        }
        .optional-label {
            font-weight: normal;
            color: #888;
            font-size: 12px;
        }
    </style>
</head>
<body>

    <div class="register-container">
        <h2>Crea un account ReFrame</h2>
        <p class="subtitle">Unisciti a noi per acquistare e vendere fotocamere.</p>
        
        <%
            List<String> errors = (List<String>) request.getAttribute("errors");
            if (errors != null && !errors.isEmpty()) {
        %>
            <div class="error-box">
                <ul>
                    <% for (String errore : errors) { %>
                        <li><%= errore %></li>
                    <% } %>
                </ul>
            </div>
        <%
            }
        %>
        
        <form action="${pageContext.request.contextPath}/RegistrazioneServlet" method="POST">
            
            <div class="form-row">
                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" placeholder="Il tuo nome" required>
                </div>
                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome" placeholder="Il tuo cognome" required>
                </div>
            </div>
            
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" placeholder="Scegli uno username" required>
            </div>
            
            <div class="form-group">
                <label for="email">Indirizzo Email</label>
                <input type="text" id="email" name="email" placeholder="es. mario.rossi@email.it" required>
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="Minimo 8 caratteri" required minlength="6">
            </div>
            
            <div class="form-group">
                <label for="telefono">Telefono</label>
                <input type="text" id="telefono" name="telefono" placeholder="Il tuo numero di telefono">
            </div>
            
            <button type="submit" class="btn-submit">Registrati</button>
            
        </form>
        
        <a href="login.jsp" class="login-link">Hai già un account? Accedi qui</a>
    </div>

</body>
</html>