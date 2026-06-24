<%@ page import="reframe.model.beans.Utente" %>
<% Utente utenteLoggatoAnagrafica = (Utente) session.getAttribute("utente"); %>

<div class="profile-column fixed-column">
            
            <div class="profile-card general-info-card">
                
                <div class="card-header profile-card-header">
                    <h2>
        				<i class="fas fa-user"></i> <%= utenteLoggatoAnagrafica.getUsername() %>
    				</h2>
                    <div>
                        <button id="btnEditProfile" class="btn-edit-profile" title="Modifica Profilo">
                            <i class="fas fa-pencil-alt"></i>
                        </button>
                    </div>
                </div>

                <div class="user-details-grid">
                    <div class="detail-block">
                        <p><span class="profile-label">NOME:</span> <%= utenteLoggatoAnagrafica.getNome() %></p>
                    </div>
                    
                    <div class="detail-block">
                        <p><span class="profile-label">COGNOME:</span> <%= utenteLoggatoAnagrafica.getCognome() %></p>
                    </div>
                    
                    <div class="detail-block">
                        <p><span class="profile-label">EMAIL:</span> <%= utenteLoggatoAnagrafica.getEmail() %></p>
                    </div>
                    
                    <div class="password-display-row">
                    	<p><span class="profile-label">PASSWORD:</span> <span class="tech-text">&bull;&bull;&bull;&bull;&bull;&bull;&bull;&bull;</span></p>
                    	<button id="btnPasswordToggle" class="btn-edit" title="Cambia Password">
                        	<i class="fas fa-key"></i>
                    	</button>
                	</div>
                    
                    <div class="detail-block">
                        <p id="txtTelefono"><%= utenteLoggatoAnagrafica.getTelefono() != null ? utenteLoggatoAnagrafica.getTelefono() : "Non specificato" %></p>
                        <input type="text" id="inputTelefono" class="edit-input hidden" value="<%= utenteLoggatoAnagrafica.getTelefono() != null ? utenteLoggatoAnagrafica.getTelefono() : "" %>">
                        <span id="errorTelefono" class="error-text"></span>
                    </div>

                    <div class="detail-block">
                        <p><span class="profile-label">BIO</span></p>
                        <p id="txtBio"><%= (utenteLoggatoAnagrafica.getBio() != null && !utenteLoggatoAnagrafica.getBio().trim().isEmpty()) ? utenteLoggatoAnagrafica.getBio() : "Nessuna biografia inserita." %></p>
                        <textarea id="textareaBio" class="edit-input hidden" rows="6"><%= utenteLoggatoAnagrafica.getBio() != null ? utenteLoggatoAnagrafica.getBio() : "" %></textarea>
                        <span id="errorBio" class="error-text"></span>
                    </div>
                </div>

                <div id="editActions" class="edit-actions hidden">
                    <button id="btnCancelProfile" class="btn-cta cancel-btn form-btn">Annulla</button>
                    <button id="btnSaveProfile" class="btn-cta form-btn">Salva</button>
                </div>

                <div id="formPasswordContainer" class="hidden form-container password-form-container">
                    <form action="<%= request.getContextPath() %>/ProfiloServlet" method="POST">
                        <input type="hidden" name="action" value="cambioPassword">
                        
                        <div class="form-grid">
                            <div class="full-width">
                                <fieldset class="custom-input">
                                    <legend>Vecchia Password</legend>
                                    <input type="password" id="inputVecchiaPassword" name="vecchiaPassword" required>
                                </fieldset>
                                <span id="hintVecchiaPassword" class="error-text"></span>
                            </div>
                            
                            <div class="full-width">
                                <fieldset class="custom-input">
                                    <legend>Nuova Password</legend>
                                    <input type="password" id="inputNuovaPassword" name="nuovaPassword" required placeholder="Minimo 8 caratteri">
                                </fieldset>
                                <span id="hintNuovaPassword" class="error-text">Minimo 8 caratteri: 1 Maiusc, 1 min, 1 num, 1 speciale.</span>
                            </div>

                            <div class="full-width">
                                <fieldset class="custom-input">
                                    <legend>Conferma Password</legend>
                                    <input type="password" id="inputConfermaPassword" name="confermaPassword" required>
                                </fieldset>
                                <span id="hintConfermaPassword" class="error-text">Le password non coincidono.</span>
                            </div>
                        </div>

                        <div class="form-actions">
                            <button type="button" id="btnCancelPassword" class="btn-cta cancel-btn form-btn">Annulla</button>
                            <button type="submit" class="btn-cta form-btn">Aggiorna</button>
                        </div>
                    </form>
                </div>
            </div>
		</div>