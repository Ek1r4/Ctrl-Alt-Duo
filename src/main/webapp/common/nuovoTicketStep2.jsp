<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="reframe.model.beans.Utente" %>
<%
    // Sicurezza: Controllo Login
    Utente utente = (Utente) session.getAttribute("utente");
    if (utente == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuovo Ticket - Step 2 - ReFrame</title>
    
    <link rel="stylesheet" href="../css/global.css">
    <link rel="stylesheet" href="../css/variables.css">
    <link rel="stylesheet" href="../css/form.css">
    <link rel="stylesheet" href="../css/assistenza.css">
</head>
<body>
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <main class="auth-wrapper" >
        
        <div>
            <a href="nuovoTicketStep1.jsp" class="btn-step-nav">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
                INDIETRO
            </a>
        </div>

        <div class="film-container large">
            
            <h1 class="form-title">
                STEP 2:<br>
                <span>DETTAGLI TICKET</span>
            </h1>

            <form id="formTicketFinale" action="#" method="POST">
                
                <%
                    String[] ordiniSelezionati = request.getParameterValues("ordineSelezionato");
                    String[] prodottiSelezionati = request.getParameterValues("prodottiSelezionati");
                    boolean ciSonoSelezioni = (ordiniSelezionati != null && ordiniSelezionati.length > 0) ||
                                              (prodottiSelezionati != null && prodottiSelezionati.length > 0);
                %>

                <% if(ciSonoSelezioni) { %>
                    <div class="ticket-selections-analog">
                        <span class="selection-label-analog">Hai selezionato:</span>
                        
                        <% if(ordiniSelezionati != null) { 
                            for(String ordine : ordiniSelezionati) { %>
                                <span class="sel-tag-analog">Ordine #<%= ordine %></span>
                                <input type="hidden" name="ordineSelezionato" value="<%= ordine %>">
                        <%  } } %>

                        <% if(prodottiSelezionati != null) { 
                            for(String prodotto : prodottiSelezionati) { %>
                                <span class="sel-tag-analog">Prodotto: <%= prodotto %></span>
                                <input type="hidden" name="prodottiSelezionati" value="<%= prodotto.replace("\"", "&quot;") %>">
                        <%  } } %>
                    </div>
                <% } %>

                <fieldset class="custom-input">
                    <legend>Titolo del problema *</legend>
                    <input type="text" name="titolo" placeholder="Es. Prodotto danneggiato..." required>
                </fieldset>

                <fieldset class="custom-input">
                    <legend>Categoria *</legend>
                    <div class="select-wrapper-analog">
                        <select name="tag" required>
                            <option value="" disabled selected hidden>Seleziona l'argomento...</option>
                            <option value="ordine">Ordine</option>
                            <option value="prodotto">Prodotto</option>
                            <option value="pagamento">Pagamento</option>
                            <option value="account">Account</option>
                        </select>
                        <span class="select-arrow-analog">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                        </span>
                    </div>
                </fieldset>

                <fieldset class="custom-input">
                    <legend>Descrizione *</legend>
                    <textarea name="descrizione" rows="5" placeholder="Descrivi nel dettaglio il problema..." required></textarea>
                </fieldset>

                <button type="submit" class="btn-cta" style="width: 100%;">INVIA TICKET</button>

            </form>
        </div>
    </main>

    <div class="ticket-popup-overlay" id="popupConferma"></div>

    <jsp:include page="../WEB-INF/components/footer.jsp" />

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const form = document.getElementById("formTicketFinale");
            const popup = document.getElementById("popupConferma");

            form.addEventListener("submit", function(e) {
                e.preventDefault(); 
                popup.classList.add("active");
                // Qui potrai rimuovere preventDefault e lasciare che il form vada verso la Servlet!
            });
        });
    </script>
</body>
</html>