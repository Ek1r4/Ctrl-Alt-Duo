<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="reframe.model.beans.Prodotto" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Prodotti - Area Admin ReFrame</title>
    <link rel="stylesheet" href="../css/global.css">
    <link rel="stylesheet" href="../css/form.css">
    <link rel="stylesheet" href="../css/amministrazione.css">
    <style>
        .admin-container { max-width: 1200px; margin: 2rem auto; padding: 1rem; }
        .success-msg { background: #d4edda; color: #155724; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        .error-msg { background: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #f2f2f2; }
        .add-form-container { background: #f9f9f9; padding: 20px; border-radius: 8px; margin-bottom: 30px;}
    </style>
</head>
<body>
    <jsp:include page="../WEB-INF/components/header.jsp" />

    <main class="admin-container">
        <h1>Pannello di Controllo Prodotti</h1>

        <% String successMsg = (String) request.getAttribute("successMessage");
           if(successMsg != null) { %>
            <div class="success-msg"><%= successMsg %></div>
        <% } %>
        <% String errorMsg = (String) request.getAttribute("errorMessage");
           if(errorMsg != null) { %>
            <div class="error-msg"><%= errorMsg %></div>
        <% } %>

        <section class="add-form-container">
            <h3>Aggiungi un Prodotto di Test</h3>
            <form action="${pageContext.request.contextPath}/ProdottoServlet" method="POST">
                <input type="hidden" name="action" value="insert">
             
                <input type="text" name="marchio" placeholder="Marchio" required>
                <input type="text" name="nome" placeholder="Nome Prodotto" required>
                <input type="text" name="seriale" placeholder="Seriale (Univoco)" required>
                <input type="number" step="0.01" name="prezzo" placeholder="Prezzo" required>
                <input type="number" name="iva" placeholder="IVA (%)" value="22" required>
                <input type="number" name="stock" placeholder="Quantità" required>
          
                <input type="text" name="descrizione" placeholder="Descrizione breve" required>
                <button type="submit" class="btn btn-primary">Inserisci Prodotto</button>
            </form>
        </section>

        <h3>Catalogo Attuale</h3>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Marchio</th>
                    <th>Modello</th>
                    <th>Seriale</th>
                    <th>Prezzo</th>
                    <th>IVA</th> <th>Stock</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    List<Prodotto> prodotti = (List<Prodotto>) request.getAttribute("listaProdotti");
                    if(prodotti != null && !prodotti.isEmpty()) {
                        for(Prodotto p : prodotti) { 
                %>
                    <tr>
                        <td><%= p.getId() %></td>
                        <td><%= p.getMarchio() %></td>
                        <td><%= p.getNome() %></td>
                        <td><%= p.getSeriale() %></td>
                        <td>€ <%= p.getPrezzo() %></td>
                        <td><%= p.getIva() %>%</td> <td><%= p.getInStock() %></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/ProdottoServlet" method="POST" onsubmit="return confirm('Sei sicuro di voler eliminare definitivamente questo prodotto? L\'operazione è irreversibile.');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="idProdotto" value="<%= p.getId() %>">
                                <button type="submit" class="btn btn-danger">Elimina</button>
                            </form>
                        </td>
                    </tr>
                <%      }
                    } else { %>
                    <tr><td colspan="8">Nessun prodotto presente nel database.</td></tr>
                <% } %>
            </tbody>
        </table>
    </main>

    <jsp:include page="../WEB-INF/components/footer.jsp" />
</body>
</html>