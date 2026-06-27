<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="reframe.model.beans.Utente" %>
<%@ page import="reframe.model.beans.Prodotto" %>
<%@ page import="reframe.model.beans.Ordine" %>

<%
    // Protezione della rotta
    Utente adminLoggato = (Utente) session.getAttribute("utente");
    if (adminLoggato == null || adminLoggato.getIsAdmin() == 0) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    boolean isSuperAdmin = (adminLoggato.getIsAdmin() == 2); 

    List<Prodotto> listaProdotti = (List<Prodotto>) request.getAttribute("listaProdotti");
    List<Utente> listaAdmins = (List<Utente>) request.getAttribute("listaAdmins");
    List<Ordine> listaOrdini = (List<Ordine>) request.getAttribute("listaOrdini");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Admin - ReFrame</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/amministrazione.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/user-area.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/form.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="admin-layout">

	<%
    String checkSuccess = request.getParameter("success");
    String paramTab = request.getParameter("tab");
    String targetTab = "";
    
    // 1. Controllo primario: Se l'URL ci dice esplicitamente in che tab andare (proveniente dal form di filtro)
    if (paramTab != null && !paramTab.trim().isEmpty()) {
        targetTab = paramTab;
    }
    // 2. Controllo secondario: Gestione dei redirect dalle Servlet
    else if ("filtroApplicato".equals(checkSuccess) || "filtroFallito".equals(checkSuccess)) {
        targetTab = "ordini";
    }
    else if ("statoAggiornato".equals(checkSuccess) || "updateFallito".equals(checkSuccess)) {
        targetTab = "ordini";
    }
    else if ("adminCreato".equals(checkSuccess) || "adminEliminato".equals(checkSuccess)) {
        targetTab = "superadmin";
    } 
    else if (checkSuccess != null || request.getParameter("errore") != null) {
        targetTab = "prodotti";
    }
	%>
	
<input type="hidden" id="triggerTab" value="<%= targetTab %>">

    <aside class="sidebar">
        <div class="sidebar-header">
            <a href="${pageContext.request.contextPath}/index.jsp" class="header-logo">
            <img src="${pageContext.request.contextPath}/assets/logoReFrame.png" alt="Logo ReFrame" class="logo-img"> 
            <div class="logo-text">
                <span class="logo-title">REFRAME</span>
            </div>
        </a><br>
            <span class="role-badge"><%= isSuperAdmin ? "Super Admin" : "Admin" %></span>
        </div>
        <nav class="sidebar-nav">
            <button class="tab-btn active" data-target="profilo"><i class="fas fa-user-circle"></i> Anagrafica</button>
            <button class="tab-btn" data-target="prodotti"><i class="fas fa-box-open"></i> Catalogo</button>
            <button class="tab-btn" data-target="aggiungi"><i class="fas fa-plus-square"></i> Nuovo Prodotto</button>
            <button class="tab-btn" data-target="ordini"><i class="fas fa-receipt"></i> Ordini</button>
            
            <% if (isSuperAdmin) { %>
                <button class="tab-btn" data-target="superadmin"><i class="fas fa-users-cog"></i> Gestione Admin</button>
            <% } %>
        </nav>
        <div class="sidebar-footer">
            <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn-logout"><i class="fas fa-sign-out-alt"></i> Logout</a>
            <a href="<%= request.getContextPath() %>/index.jsp" class="btn-home"><i class="fas fa-home"></i> Home</a>
        </div>
    </aside>

    <main class="main-content">

        <section id="profilo" class="dashboard-section active">
            <%@ include file="/WEB-INF/components/anagrafia.jsp" %>
        </section>

        <section id="prodotti" class="dashboard-section">
            <div class="rect-card">
                <h3>Lista Prodotti</h3>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Seriale</th>
                            <th>Tipo</th>
                            <th>Marchio</th>
                            <th>Modello</th>
                            <th>Prezzo</th>
                            <th>Stock</th>
                            <th>Azioni</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (listaProdotti != null && !listaProdotti.isEmpty()) { 
                            for (Prodotto p : listaProdotti) { %>
                        <tr>
                            <td><%= p.getSeriale() %></td>
                            <td><%= p.getTipo() %></td>
                            <td><%= p.getMarchio() %></td>
                            <td><%= p.getNome() %></td>
                            <td>€ <%= String.format("%.2f", p.getPrezzo()) %></td>
                            <td><%= p.getInStock() %></td>
                            <td class="actions">
                                <button type="button" class="btn-icon edit btn-edit-product" title="Modifica"
                                    data-id="<%= p.getId() %>"
                                    data-nome="<%= p.getNome() != null ? p.getNome().replace("\"", "&quot;") : "" %>"
                                    data-prezzo="<%= p.getPrezzo() %>"
                                    data-stock="<%= p.getInStock() %>"
                                    data-tipo="<%= p.getTipo() %>"
                                    data-descrizione="<%= p.getDescrizione() != null ? p.getDescrizione().replace("\"", "&quot;") : "" %>"
                                    data-stato="<%= p.getStato() != null ? p.getStato().replace("\"", "&quot;") : "" %>"
                                    data-scatti="<%= p.getNumeroScatti() > 0 ? p.getNumeroScatti() : "" %>"
                                    data-condizione="<%= p.getCondizioneCollezionistica() != null ? p.getCondizioneCollezionistica().replace("\"", "&quot;") : "" %>">
                                    <i class="fas fa-edit"></i>
                                </button>
                                
                                <form action="<%= request.getContextPath() %>/ProdottoServlet" method="POST" class="inline-form">
    								<input type="hidden" name="action" value="delete"> 
    								<input type="hidden" name="idProdotto" value="<%= p.getId() %>">
    								<button type="submit" class="btn-icon delete" title="Elimina"><i class="fas fa-trash-alt"></i></button>
								</form>
                            </td>
                        </tr>
                        <%  } 
                           } else { %>
                        <tr><td colspan="6" class="text-center">Nessun prodotto in catalogo.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </section>

        <section id="aggiungi" class="dashboard-section">
            <div class="film-container">
                <h1 class="form-title">Inserisci Nuovo Prodotto</h1>
                
                <!-- ATTENZIONE: Aggiunto enctype e cambiata la action verso la nuova Servlet -->
                <form action="<%= request.getContextPath() %>/PannelloAdminServlet" method="POST" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="aggiungiProdotto"> 
                    
                    <div class="form-grid">
                        <fieldset class="custom-input">
                            <legend>ID Prodotto</legend>
                            <input type="text" name="idProdotto" required>
                        </fieldset>
                        <fieldset class="custom-input">
                            <legend>Seriale</legend>
                            <input type="text" name="seriale" required>
                        </fieldset>
                        <fieldset class="custom-input">
                            <legend>Marchio</legend>
                            <input type="text" name="marchio" required>
                        </fieldset>
                        <fieldset class="custom-input">
                            <legend>Nome / Modello</legend>
                            <input type="text" name="nome" required>
                        </fieldset>
                        <fieldset class="custom-input">
                            <legend>Prezzo (€)</legend>
                            <input type="number" step="0.01" name="prezzo" required>
                        </fieldset>
                        <fieldset class="custom-input">
                            <legend>Quantità in Stock</legend>
                            <input type="number" name="stock" min="0" required>
                        </fieldset>
                        
                        <fieldset class="custom-input">
                            <legend>Tipologia</legend>
                            <select name="tipo" id="add-tipo" required class="custom-select-film">
                                <option value="Nuovo">Nuovo</option>
                                <option value="Usato">Usato</option>
                                <option value="Collezione">Collezione</option>
                            </select>
                        </fieldset>
                        
                        <fieldset class="custom-input dynamic-field add-field-usato">
                            <legend>Stato di Usura</legend>
                            <input type="text" name="stato" placeholder="es. Ottimo, Segni d'uso...">
                        </fieldset>
                        
                        <fieldset class="custom-input dynamic-field add-field-usato">
                            <legend>Numero Scatti</legend>
                            <input type="number" name="numeroScatti" min="0">
                        </fieldset>
                        
                        <fieldset class="custom-input full-width dynamic-field add-field-collezione">
                            <legend>Condizione Collezionistica</legend>
                            <input type="text" name="condizioneCollezionistica" placeholder="es. Mint, Grade A...">
                        </fieldset>

                        <!-- NUOVI CAMPI UPLOAD FILE (Immagine e Modello 3D) -->
                        <fieldset class="custom-file-input full-width">
                            <legend>Immagine Copertina (.jpg, .png)</legend>
                            <input type="file" name="immagineCopertina" accept="image/*" required>
                        </fieldset>
                        
                        <fieldset class="custom-file-input full-width">
                            <legend>Modello 3D (.glb, .gltf)</legend>
                            <input type="file" name="modello3D" accept=".glb,.gltf" required>
                        </fieldset>
                    </div>
                    
                    <fieldset class="custom-input full-width">
                        <legend>Descrizione Completa</legend>
                        <textarea name="descrizione" rows="4" class="custom-textarea" required></textarea>
                    </fieldset>
                    
                    <button type="submit" class="btn-cta">Aggiungi al Catalogo</button>
                </form>
            </div>
        </section>

        <section id="ordini" class="dashboard-section">
    
    <div class="rect-card mb-20">
        <h3>Filtra Ordini</h3>
        <form action="<%= request.getContextPath() %>/PannelloAdminServlet" method="GET" class="filter-form">
        	<input type="hidden" name="tab" value="ordini">
            <input type="text" name="cliente" placeholder="Cerca per Cliente..." class="search-input">
            
            <div class="date-filter">
                <label>Da:</label>
                <input type="date" name="dataInizio" title="Data Inizio">
            </div>
            
            <div class="date-filter">
                <label>A:</label>
                <input type="date" name="dataFine" title="Data Fine">
            </div>
            
            <button type="submit" class="btn-cta"><i class="fas fa-search"></i> Applica Filtri</button>
        </form>
    </div>

    <div class="rect-card">
        <h3>Lista Ordini</h3>
        <table class="data-table">
            <thead>
                <tr>
                    <th>N° Ordine</th>
                    <th>Data</th>
                    <th>Cliente</th>
                    <th>Totale</th>
                    <th>Stato Attuale</th>
                    <th>Modifica Stato</th>
                </tr>
            </thead>
            <tbody>
                <%
                   if (listaOrdini != null && !listaOrdini.isEmpty()) { 
                       for (Ordine o : listaOrdini) { 
                %>
                <tr>
                    <td>#<%= o.getIdOrdine() %></td>
                    <td><%= o.getDataOrdine() %></td>
                    <td><%= o.getIdUtente() %></td>
                    <td>&euro; <%= String.format("%.2f", o.getTotale()) %></td>
                    <td><span class="status-badge"><%= o.getStato() %></span></td>
                    <td>
                        <form action="<%= request.getContextPath() %>/PannelloAdminServlet" method="POST" class="status-form">
                            <input type="hidden" name="action" value="aggiornaStatoOrdine">
                            
                            <input type="hidden" name="idOrdine" value="<%= o.getIdOrdine() %>">
                            <select name="nuovoStato" class="status-select">
                                <option value="In lavorazione" <%= "In lavorazione".equals(o.getStato()) ? "selected" : "" %>>In lavorazione</option>
                                <option value="In consegna" <%= "Spedito".equals(o.getStato()) ? "selected" : "" %>>In consegna</option>
                                <option value="Consegnato" <%= "Consegnato".equals(o.getStato()) ? "selected" : "" %>>Consegnato</option>
                            </select>
                            <button type="submit" class="btn-icon update" title="Aggiorna Stato"><i class="fas fa-check-circle"></i></button>
                        </form>
                    </td>
                </tr>
                <%     } 
                   } else { %>
                <tr><td colspan="6" class="text-center">Nessun ordine trovato.</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</section>

        <% if (isSuperAdmin) { %>
        <section id="superadmin" class="dashboard-section">
            <div class="rect-card mb-20">
                <h3>Crea nuovo profilo Admin</h3>
                <form action="<%= request.getContextPath() %>/PannelloAdminServlet" method="POST" class="admin-form row-form">
                	<input type="text" name="username" id="username" placeholder="Username" autocomplete="off" required>
                    <input type="text" name="nome" id="nome" placeholder="Nome" required>
                    <input type="text" name="cognome" id="cognome" placeholder="Cognome" required>
                    <input type="email" name="adminEmail" id="email" placeholder="Email aziendale" autocomplete="off" required>
                    <input type="password" name="adminPassword" id="password" placeholder="Password provvisoria" autocomplete="off" required>
                    <button type="submit" class="btn-cta">Crea Account</button>
                </form>
            </div>

            <div class="rect-card">
                <h3>Monitoraggio Profili Admin</h3>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Livello</th>
                            <th>Revoca Accesso</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (listaAdmins != null) { 
                            for (Utente adm : listaAdmins) { %>
                        <tr>
                            <td><%= adm.getNome() %> <%= adm.getCognome() %></td>
                            <td><%= adm.getEmail() %></td>
                            <td><%= adm.getIsAdmin() == 2 ? "Super Admin" : "Admin" %></td>
                            <td>
                                <% if(adm.getUsername() != adminLoggato.getUsername()) { %>
                                <form action="<%= request.getContextPath() %>/PannelloAdminServlet" method="POST" class="inline-form">
                                    <input type="hidden" name="userAdmin" value="<%= adm.getUsername() %>">
                                    <button type="submit" class="btn-icon delete" title="Revoca Accesso"><i class="fas fa-user-times"></i></button>
                                </form>
                                <% } else { %>
                                    <span class="text-muted">Il tuo account</span>
                                <% } %>
                            </td>
                        </tr>
                        <%  } 
                           } %>
                    </tbody>
                </table>
            </div>
        </section>
        <% } %>

	<div id="edit-product-modal" class="admin-modal-overlay">
            
            <div class="film-container modal-film-override">
                
                <div class="camera-icon">
                    <svg class="icon-edit-modal" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </div>
                
                <button type="button" class="btn-close-modal" id="close-edit-modal" title="Chiudi">&times;</button>
                
                <div class="modal-body-scroll">
                <h1 class="form-title">Modifica Prodotto</h1>
                
                <form action="<%= request.getContextPath() %>/ProdottoServlet" method="POST">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="idProdotto" id="modal-edit-id">
                    
                    <div class="form-grid">
                        <fieldset class="custom-input">
                            <legend>Nome / Modello</legend>
                            <input type="text" name="nome" id="modal-edit-nome" required>
                        </fieldset>
                        
                        <fieldset class="custom-input">
                            <legend>Prezzo (€)</legend>
                            <input type="number" step="0.01" name="prezzo" id="modal-edit-prezzo" required>
                        </fieldset>
                        
                        <fieldset class="custom-input">
                            <legend>Quantità in Stock</legend>
                            <input type="number" name="stock" id="modal-edit-stock" min="0" required>
                        </fieldset>
                        
                        <fieldset class="custom-input">
                            <legend>Tipologia</legend>
                            <select name="tipo" id="modal-edit-tipo" required class="custom-select-film">
                                <option value="Nuovo">Nuovo</option>
                                <option value="Usato">Usato</option>
                                <option value="Collezione">Collezione</option>
                            </select>
                        </fieldset>
                        
                        <fieldset class="custom-input dynamic-field field-usato">
                            <legend>Stato di Usura</legend>
                            <input type="text" name="stato" id="modal-edit-stato" placeholder="es. Ottimo, Segni d'uso...">
                        </fieldset>
                        
                        <fieldset class="custom-input dynamic-field field-usato">
                            <legend>Numero Scatti</legend>
                            <input type="number" name="numeroScatti" id="modal-edit-scatti" min="0">
                        </fieldset>
                        
                        <fieldset class="custom-input full-width dynamic-field field-collezione">
                            <legend>Condizione Collezionistica</legend>
                            <input type="text" name="condizioneCollezionistica" id="modal-edit-condizione" placeholder="es. Mint, Grade A...">
                        </fieldset>
                    </div>
                    
                    <fieldset class="custom-input full-width">
                        <legend>Descrizione</legend>
                        <textarea name="descrizione" id="modal-edit-descrizione" rows="4" class="custom-textarea" required></textarea>
                    </fieldset>
                    
                    <button type="submit" class="btn-cta">Salva Modifiche</button>
                </form>
                </div>
            </div>
        </div>
        
        <div id="delete-confirm-modal" class="admin-modal-overlay">
            <div class="film-container modal-film-override confirm-modal-box">
                
                <h3 class="form-title">Conferma Azione</h3>
                <p id="delete-confirm-message" class="confirm-message"></p>
                
                <div class="confirm-actions">
                    <button type="button" id="btn-cancel-delete" class="btn-cta cancel-btn">Annulla</button>
                    <button type="button" id="btn-confirm-delete" class="btn-cta danger-btn">Procedi</button>
                </div>
            </div>
        </div>

    </main>
    <script>const contestoReFrame = '<%= request.getContextPath() %>';</script>
    <script src="<%= request.getContextPath() %>/js/profilo.js"></script>
    <script src="<%= request.getContextPath() %>/js/amministrazione.js"></script>
</body>
</html>