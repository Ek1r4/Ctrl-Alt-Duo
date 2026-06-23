<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="reframe.model.beans.Carrello" %>
<%@ page import="reframe.model.beans.CarrelloItem" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Il tuo Carrello - ReFrame</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/form.css">
    <style>
        .cart-container { max-width: 1200px; margin: 2rem auto; padding: 0 1rem; }
        .cart-table { width: 100%; border-collapse: collapse; margin-bottom: 2rem; }
        .cart-table th, .cart-table td { padding: 1rem; border-bottom: 1px solid #ddd; text-align: left; }
        .cart-summary { background: #f9f9f9; padding: 1.5rem; border-radius: 8px; text-align: right; }
        .alert-success { background: #d4edda; color: #155724; padding: 1rem; border-radius: 4px; margin-bottom: 1rem; }
        
        /* Media query base per responsività [cite: 42, 43] */
        @media (max-width: 768px) {
            .cart-table thead { display: none; }
            .cart-table tbody td { display: block; text-align: right; }
            .cart-table tbody td::before { content: attr(data-label); float: left; font-weight: bold; }
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/components/header.jsp" />

    <main class="cart-container">
        <h1>Il tuo Carrello</h1>

        <% String successMsg = (String) request.getAttribute("successMessage"); 
           if(successMsg != null) { %>
            <div class="alert-success"><%= successMsg %></div>
        <% } %>

        <% String errorMsg = (String) request.getAttribute("errorMessage"); 
           if(errorMsg != null) { %>
            <div class="alert-error"><%= errorMsg %></div>
        <% } %>

        <div style="background: #f9f9f9; padding: 15px; margin-bottom: 20px; border: 2px dashed #ccc; border-radius: 8px;">
            <p style="margin-top: 0; font-weight: bold; color: #555;">Pannello di Test (Simulatore Vetrina):</p>
            <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="id" value="NEW-21">
                    <input type="hidden" name="quantita" value="1">
                    <button type="submit" class="btn" style="padding: 8px; font-size: 0.9rem;">+ Sony Alpha 7 IV</button>
                </form>
                <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="id" value="NEW-11">
                    <input type="hidden" name="quantita" value="1">
                    <button type="submit" class="btn" style="padding: 8px; font-size: 0.9rem;">+ Canon EOS R5</button>
                </form>
                <form action="${pageContext.request.contextPath}/Carrello" method="POST">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="id" value="PRD00003">
                    <input type="hidden" name="quantita" value="1">
                    <button type="submit" class="btn" style="padding: 8px; font-size: 0.9rem;">+ Minolta X-700</button>
                </form>
            </div>
        </div>

        <% 
            Carrello carrello = (Carrello) session.getAttribute("carrello");
            if (carrello == null || carrello.getItems().isEmpty()) { 
        %>
            <p>Il tuo carrello è attualmente vuoto.</p>
        <% } else { %>
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>Prodotto</th>
                        <th>Prezzo Unitario</th>
                        <th>Quantità</th>
                        <th>Totale</th>
                        <th>Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (CarrelloItem item : carrello.getItems()) { %>
                        <tr id="riga-<%= item.getIdProdotto() %>">
                            <td data-label="Prodotto"><%= item.getNome() %></td>
                            <td data-label="Prezzo">€ <%= String.format("%.2f", item.getPrezzo()) %></td>
                            
                            <td data-label="Quantità">
                                <input type="number" 
                                       value="<%= item.getQuantita() %>" 
                                       min="1" max="10" 
                                       style="width: 60px; padding: 5px; text-align: center; border: 1px solid #ccc;"
                                       onchange="aggiornaQuantitaAJAX('<%= item.getIdProdotto() %>', this.value)">
                            </td>
                            
                            <td data-label="Totale" id="totale-riga-<%= item.getIdProdotto() %>">
                                € <%= String.format("%.2f", item.getPrezzoTotale()) %>
                            </td>
                            
                            <td data-label="Azioni">
                                <button type="button" class="btn-small btn-danger" onclick="rimuoviProdottoAJAX('<%= item.getIdProdotto() %>')">
                                    Rimuovi
                                </button>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>

            <div class="cart-summary">
                <h3 id="totale-carrello-testo">Totale Carrello: € <%= String.format("%.2f", carrello.getTotale()) %></h3>
                <a href="${pageContext.request.contextPath}/common/checkout.jsp" class="btn btn-primary" style="margin-top: 1rem; display: inline-block;">Procedi al Checkout</a>
            </div>
        <% } %>
    </main>

    <jsp:include page="/WEB-INF/components/footer.jsp" />
    
    
    
    
    <script>
        // Funzione per aggiornare la quantità
        function aggiornaQuantitaAJAX(idProdotto, nuovaQuantita) {
            
            // Prepariamo i dati da inviare via POST
            const formData = new URLSearchParams();
            formData.append('action', 'update');
            formData.append('id', idProdotto);
            formData.append('quantita', nuovaQuantita);
            formData.append('ajax', 'true'); // Attiva la risposta JSON nella Servlet

            // Chiamata asincrona Fetch API
            fetch('${pageContext.request.contextPath}/Carrello', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if(data.status === 'success') {
                    // 1. Aggiorna visivamente il totale della singola riga
                    document.getElementById('totale-riga-' + idProdotto).innerText = '€ ' + data.totaleRiga.toFixed(2);
                    
                    // 2. Aggiorna visivamente il totale complessivo del carrello
                    document.getElementById('totale-carrello-testo').innerText = 'Totale Carrello: € ' + data.totaleCarrello.toFixed(2);
                }
            })
            .catch(error => console.error('Errore durante l\'aggiornamento AJAX:', error));
        }

        // Funzione per rimuovere un prodotto
        function rimuoviProdottoAJAX(idProdotto) {
            if(!confirm('Sei sicuro di voler rimuovere questo prodotto?')) return;

            const formData = new URLSearchParams();
            formData.append('action', 'remove');
            formData.append('id', idProdotto);
            formData.append('ajax', 'true');

            fetch('${pageContext.request.contextPath}/Carrello', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if(data.status === 'success') {
                    // 1. Rimuove la riga (<tr>) dalla tabella visivamente
                    const riga = document.getElementById('riga-' + idProdotto);
                    if (riga) riga.remove();
                    
                    // 2. Aggiorna il totale complessivo
                    document.getElementById('totale-carrello-testo').innerText = 'Totale Carrello: € ' + data.totaleCarrello.toFixed(2);

                    // 3. Se il carrello è diventato vuoto (0 euro), ricarichiamo la pagina per mostrare il messaggio "Carrello vuoto"
                    if(data.totaleCarrello === 0) {
                        window.location.reload();
                    }
                }
            })
            .catch(error => console.error('Errore durante la rimozione AJAX:', error));
        }
    </script>
</body>
</html>