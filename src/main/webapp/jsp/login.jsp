<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accedi - ReFrame</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .login-container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 400px;
            text-align: center;
        }
        h2 {
            color: #333;
            margin-bottom: 20px;
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
            padding: 10px;
            background-color: #2c3e50;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            margin-top: 10px;
        }
        .btn-submit:hover {
            background-color: #1a252f;
        }
        .register-link {
            margin-top: 15px;
            display: block;
            font-size: 14px;
            color: #666;
            text-decoration: none;
        }
        .register-link:hover {
            text-decoration: underline;
        }
        /* Stile per il box degli errori inviati dalla Servlet */
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
    </style>
</head>
<body>

    <div class="login-container">
        <h2>Bentornato su ReFrame</h2>
        <p>Accedi per gestire i tuoi ordini e le tue fotocamere.</p>
        
        <%
            // Leggo la lista di errori passata dalla LoginServlet
            List<String> errors = (List<String>) request.getAttribute("errors");
            
            // Se la lista esiste e contiene almeno un errore, creo il box rosso
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
        
        <form action="${pageContext.request.contextPath}/LoginServlet" method="POST">
            
            <div class="form-group">
                <label for="email">Indirizzo Email</label>
                <input type="text" id="email" name="email" placeholder="es. mario.rossi@email.it" required>
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="Inserisci la tua password" required>
            </div>
            
            <button type="submit" class="btn-submit">Accedi</button>
            
        </form>
        
        <a href="registrazione.jsp" class="register-link">Non hai un account? Registrati qui</a>
    </div>

</body>
</html>