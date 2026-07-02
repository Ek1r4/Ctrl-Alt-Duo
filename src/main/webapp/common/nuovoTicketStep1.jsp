<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="reframe.model.beans.Utente" %>
<%@ page import="reframe.model.beans.Ordine" %>
<%@ page import="reframe.model.beans.DettaglioOrdine" %>
<%@ page import="reframe.model.dao.OrdineDAO" %>
<%@ page import="java.util.List" %>
<%
    // 1. Sicurezza: Controllo Login
    Utente utente = (Utente) session.getAttribute("utente");
    if (utente == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // 2. Recupero Dati Reali
    OrdineDAO ordineDAO = new OrdineDAO();
    List<Ordine> listaOrdini = null;
    try {
        listaOrdini = ordineDAO.getOrdiniCompletiByUtente(utente.getUsername());
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuovo Ticket - Step 1 - ReFrame</title>
    
    <link rel="stylesheet" href="../css/global.css">
    <link rel="stylesheet" href="../css/variables.css">
    <link rel="stylesheet" href="../css/assistenza.css">
</head>
<body>
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <main class="assistenza-main">
        
        <div class="step-nav">
            <a href="centroAssistenza.jsp" class="btn-step-nav">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
                INDIETRO
            </a>
            <a href="nuovoTicketStep2.jsp" class="btn-step-nav">
                SALTA
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
            </a>
        </div>

        <div class="assistenza-content">
            
            <h1 class="assistenza-title">
                STEP 1: <span class="text-accent">SELEZIONA L'ORDINE</span>
            </h1>

            <form id="formSelezioneOrdine" action="nuovoTicketStep2.jsp" method="GET">

                <div class="assistenza-accordion-container">
                    
                    <% if (listaOrdini != null && !listaOrdini.isEmpty()) {
                        for (Ordine ordine : listaOrdini) { 
                    %>
                        <details class="ordine-item">
                            <summary class="ordine-header">
                                <div class="ordine-meta-left">
                                    <label class="custom-cb-wrapper">
                                        <input type="checkbox" class="cb-ordine" name="ordineSelezionato" value="<%= ordine.getIdOrdine() %>">
                                        <span class="custom-cb"></span>
                                    </label>
                                    <div class="ordine-meta">
                                        <span class="ordine-id">ORDINE #<%= ordine.getIdOrdine() %></span>
                                        <span class="ordine-date"><%= ordine.getDataOrdine() %></span>
                                    </div>
                                </div>
                                <div class="ordine-trigger">
                                    <span class="ordine-status">Visualizza Prodotti</span>
                                    <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </summary>
                            
                            <div class="ordine-products">
                                <% if (ordine.getDettagli() != null && !ordine.getDettagli().isEmpty()) {
                                    for (DettaglioOrdine dett : ordine.getDettagli()) { 
                                %>
                                    <div class="prodotto-row">
                                        <label class="custom-cb-wrapper">
                                            <input type="checkbox" class="cb-prodotto" name="prodottiSelezionati" value="<%= dett.getNomeProdottoAcquisto().replace("\"", "&quot;") %>">
                                            <span class="custom-cb"></span>
                                        </label>
                                        <span class="prodotto-nome"><%= dett.getNomeProdottoAcquisto() %></span>
                                        <span class="prodotto-qta">Q.tà: <%= dett.getQuantitaAcquisto() %></span>
                                    </div>
                                <%  }
                                   } else { %>
                                    <div class="prodotto-row">
                                        <span class="prodotto-nome" style="color:var(--grigio-taupe);">Nessun prodotto trovato per questo ordine.</span>
                                    </div>
                                <% } %>
                            </div>
                        </details>
                    <%  }
                       } else { %>
                        <div style="text-align: center; padding: 40px 20px;">
                            <p style="color: var(--grigio-taupe); font-style: italic;">Non hai ancora effettuato ordini. Puoi saltare questo passaggio.</p>
                        </div>
                    <% } %>

                </div>

                <div class="floating-submit-container" id="floatingSubmit">
                    <button type="submit" class="btn btn-cta">CONFERMA SELEZIONE</button>
                </div>

            </form>

            <div class="assistenza-link-container">
                <a href="nuovoTicketStep2.jsp" class="assistenza-sublink">
                    Il tuo problema non riguarda alcun ordine? Salta al passaggio successivo
                </a>
            </div>
            
        </div>
    </main>

    <jsp:include page="../WEB-INF/components/footer.jsp" />
    <script>
    document.addEventListener("DOMContentLoaded", function() {
        const form = document.getElementById("formSelezioneOrdine");
        const floatingSubmit = document.getElementById("floatingSubmit");
        const ordiniItems = document.querySelectorAll(".ordine-item");

        function checkVisibility() {
            const anyChecked = form.querySelectorAll('input[type="checkbox"]:checked').length > 0;
            if (anyChecked) {
                floatingSubmit.classList.add("visible");
            } else {
                floatingSubmit.classList.remove("visible");
            }
        }

        ordiniItems.forEach(item => {
            const cbOrdine = item.querySelector(".cb-ordine");
            const cbProdotti = item.querySelectorAll(".cb-prodotto");

            cbOrdine.addEventListener("change", function() {
                cbProdotti.forEach(cb => cb.checked = this.checked);
                checkVisibility();
            });

            cbProdotti.forEach(cb => {
                cb.addEventListener("change", function() {
                    const anyProductChecked = Array.from(cbProdotti).some(p => p.checked);
                    cbOrdine.checked = anyProductChecked;
                    checkVisibility();
                });
            });
        });
    });
    </script>
</body>
</html>