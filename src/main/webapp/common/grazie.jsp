<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Ordine" %>
<%
    // Recuperiamo l'ordine appena salvato dalla Servlet
    Ordine ordine = (Ordine) request.getAttribute("ordineEffettuato");
    if (ordine == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grazie per l'acquisto | ReFrame</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
    <style>
        .ordine-recap { background-color: rgba(0,0,0,0.02); border: 1px solid var(--grigio-taupe); padding: 1.5rem; text-align: left; margin: 2rem auto; border-radius: 4px; max-width: 350px;}
        .ordine-recap p { margin: 0.5rem 0; color: var(--antracite-scuro); }
        .btn-home { background-color: transparent; color: var(--antracite-scuro); border: 2px solid var(--antracite-scuro); padding: 15px 30px; font-size: 1.1rem; font-weight: bold; text-transform: uppercase; text-decoration: none; display: inline-block; margin-top: 2rem; transition: all 0.3s; }
        .btn-home:hover { background-color: var(--antracite-scuro); color: var(--panna-carta); }
    </style>
</head>
<body>
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <div class="auth-wrapper" style="margin: 5vh auto;">
        <div class="film-container" style="max-width: 550px;">
            
            <div class="camera-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="rgb(61, 116, 111)" stroke-linecap="round" stroke-linejoin="round" style="width: 50px; height: 50px; stroke-width: 2;">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
            </div>
    
            <h1 class="form-title" style="color: var(--verde-ottanio);">ACQUISTO COMPLETATO</h1>
            <p style="font-weight: bold; color: var(--antracite-scuro); margin-top: -10px;">Soggetto perfettamente a fuoco.</p>
            
            <div class="ordine-recap">
                <p><strong>N° Ordine:</strong> <%= ordine.getIdOrdine() %></p>
                <p><strong>Data:</strong> <%= ordine.getDataOrdine() %></p>
                <p><strong>Totale Pagato:</strong> € <%= String.format("%.2f", ordine.getTotale()) %></p>
                <p><strong>Stato:</strong> In Lavorazione</p>
            </div>

            <p class="denied-text">
                Grazie per aver scelto ReFrame. Abbiamo inviato una mail di conferma con tutti i dettagli.
            </p>

            <jsp:include page="../WEB-INF/components/btn-fattura.jsp">
                <jsp:param name="idOrdine" value="<%= ordine.getIdOrdine() %>" />
            </jsp:include>
            
            <br>

            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-home">Torna alla Vetrina</a>
        </div>
    </div>

    <jsp:include page="../WEB-INF/components/footer.jsp" />
    
    
    <!-- Iframe nascosto per il caricamento della fattura -->
    <iframe id="iframeFattura" style="position: absolute; width: 0; height: 0; border: none; visibility: hidden;"></iframe>

    <script>
        function stampaFatturaNascosta(idOrdine) {
            const iframe = document.getElementById('iframeFattura');
            // Imposta l'URL della Servlet: l'iframe caricherà la pagina in background
            iframe.src = '${pageContext.request.contextPath}/Fattura?id=' + idOrdine;
        }
    </script>
</body>
</html>