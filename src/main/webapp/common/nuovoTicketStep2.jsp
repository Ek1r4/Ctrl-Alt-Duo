<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="reframe.model.beans.Utente" %>

<%-- CONTROLLO AUTORIZZAZIONI (RBAC) --%>
<%
    // Verifica di sicurezza: l'accesso alla rotta di creazione ticket è subordinato all'esistenza di una sessione utente attiva. 
    // In assenza di token di sessione, il blocco esegue un redirect preventivo alla pagina di login.
    Utente utente = (Utente) session.getAttribute("utente");
    if (utente == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <!-- STRUTTURA PAGINA E HEAD -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuovo Ticket - Step 2 - ReFrame</title>
    
    <link href="https://cdn.jsdelivr.net/npm/remixicon@4.5.0/fonts/remixicon.css" rel="stylesheet" />
    <link rel="stylesheet" href="../css/global.css">
    <link rel="stylesheet" href="../css/variables.css">
    <link rel="stylesheet" href="../css/form.css">
    <link rel="stylesheet" href="../css/assistenza.css">
</head>
<body>
    
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <main class="auth-wrapper">
        
        <div>
            <a href="nuovoTicketStep1.jsp" class="btn-step-nav">
                <i class="ri-arrow-left-line"></i>
                INDIETRO
            </a>
        </div>

        <div class="film-container large">
            
            <h1 class="form-title">
                STEP 2:<br>
                <span>DETTAGLI TICKET</span>
            </h1>

            <form id="formTicketFinale" action="CreaPraticaServlet" method="POST">
                
                <%
                    // Recupero asincrono delle selezioni effettuate nello Step 1 (Ordini e Prodotti) tramite parametri della query string. 
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
                                <input type="hidden" name="ordineSelezionato" class="hidden-selezione-ordine" value="<%= ordine %>">
                        <%  } } %>

                        <% if(prodottiSelezionati != null) { 
                            // Escaping HTML per preservare l'integrità dei nomi prodotto contenenti virgolette durante l'inject nel campo hidden del form.
                            for(String prodotto : prodottiSelezionati) { %>
                                <span class="sel-tag-analog">Prodotto: <%= prodotto %></span>
                                <input type="hidden" name="prodottiSelezionati" class="hidden-selezione-prodotto" value="<%= prodotto.replace("\"", "&quot;") %>">
                        <%  } } %>
                    </div>
                <% } %>

                <!-- FORM INPUT E VALIDAZIONE -->
                <fieldset class="custom-input">
                    <legend>Titolo del problema *</legend>
                    <input type="text" name="titolo" id="titoloTicket" placeholder="Es. Prodotto danneggiato..." maxlength="50" required>
                    <span class="error-message" id="errorTitolo">Il titolo deve contenere tra 5 e 50 caratteri.</span>
                </fieldset>

                <fieldset class="custom-input">
                    <legend>Categoria *</legend>
                    <div class="select-wrapper-analog">
                        <select name="categoria" id="categoriaTicket" required>
                            <option value="" disabled <%= ciSonoSelezioni ? "selected" : "" %> hidden>Seleziona l'argomento...</option>
                            <option value="ordine">Ordine</option>
                            <option value="prodotto">Prodotto</option>
                            <option value="pagamento">Pagamento</option>
                            <option value="account" <%= !ciSonoSelezioni ? "selected" : "" %>>Account</option>
                        </select>
                        <i class="ri-arrow-down-s-line select-arrow-analog"></i>
                    </div>
                    <span class="error-message" id="errorCategoria">Seleziona una categoria.</span>
                </fieldset>

                <fieldset class="custom-input">
                    <legend>Descrizione *</legend>
                    <textarea name="descrizione" id="descrizioneTicket" rows="5" placeholder="Descrivi nel dettaglio il problema..." maxlength="1024" required></textarea>
                    <span class="error-message" id="errorDescrizione">La descrizione deve contenere tra 20 e 1024 caratteri.</span>
                </fieldset>

                <button type="submit" class="btn-cta btn-full-width">INVIA TICKET</button>

            </form>
        </div>
    </main>

    <!-- COMPONENTI UI E SCRIPT -->
    <div class="ticket-popup-overlay" id="popupConferma">
        <div class="ticket-popup-content film-container">
            <i class="ri-checkbox-circle-line popup-icon-success"></i>
            <h2>Ticket Creato!</h2>
            <p>La tua pratica è stata aperta con successo.</p>
        </div>
    </div>

    <jsp:include page="../WEB-INF/components/footer.jsp" />
    <script src="../js/assistenza.js"></script>
</body>
</html>