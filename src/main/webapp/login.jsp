<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - ReFrame</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
</head>

<body>

<div class="auth-wrapper">

    <div class="film-container">
        
        <div class="camera-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#2A2A2A" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <path d="M14.31 8l5.74 9.94M9.69 8h11.48M7.38 12l5.74-9.94M9.69 16L3.95 6.06M14.31 16H2.83m13.79-4l-5.74 9.94"></path>
            </svg>
        </div>

        <h1 class="form-title">LOGIN</h1>

        <%
            String erroreUrl = request.getParameter("errore");
            List<String> errors = (List<String>) request.getAttribute("errors");

            if ("credenziali_errate".equals(erroreUrl)) {
        %>
            <div class="error-box">
                <ul>
                    <li>Email o password errate. Riprova.</li>
                </ul>
            </div>
        <%
            } else if (errors != null && !errors.isEmpty()) {
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
            
            <fieldset class="custom-input">
                <legend>E-mail</legend>
                <input type="text" name="email">
            </fieldset>

            <fieldset class="custom-input">
                <legend>Password</legend>
                <input type="password" name="password">
            </fieldset>

            <button type="submit" class="btn-cta">ACCEDI</button>

        </form>

        <a href="${pageContext.request.contextPath}/jsp/registrazione.jsp" class="form-link">Non hai un account? Registrati</a>

    </div>
    
    <footer class="site-footer">
            &copy; 2026 ReFrame
        </footer>
        
</div>


</body>
</html>