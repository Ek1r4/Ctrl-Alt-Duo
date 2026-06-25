document.addEventListener('DOMContentLoaded', () => {
    const btns = document.querySelectorAll('.tab-btn');
    const sections = document.querySelectorAll('.dashboard-section');
    
    // Leggiamo il valore dal campo nascosto nella JSP
    const triggerTab = document.getElementById('triggerTab').value;

    if (triggerTab === 'prodotti') {
        // Forza l'apertura della scheda Prodotti
        btns.forEach(b => b.classList.remove('active'));
        sections.forEach(s => s.classList.remove('active'));
        
        document.querySelector('.tab-btn[data-target="prodotti"]').classList.add('active');
        document.getElementById('prodotti').classList.add('active');
    }

    if (btns.length > 0 && sections.length > 0) {
        
        // A. Controllo all'avvio: c'è un hash nell'URL? (es. #prodotti)
        const hash = window.location.hash;
        const requestedTab = hash ? hash.substring(1) : null;

        if (requestedTab) {
            // Spegne tutte le schede
            btns.forEach(b => b.classList.remove('active'));
            sections.forEach(s => s.classList.remove('active'));
            
            // Accende la scheda richiesta
            const targetBtn = document.querySelector(`.tab-btn[data-target="${requestedTab}"]`);
            const targetSection = document.getElementById(requestedTab);
            
            if (targetBtn && targetSection) {
                targetBtn.classList.add('active');
                targetSection.classList.add('active');
            }
        }

        // B. Azione di click manuale sui bottoni
        btns.forEach(btn => {
            btn.addEventListener('click', () => {
                // Spegne tutto
                btns.forEach(b => b.classList.remove('active'));
                sections.forEach(s => s.classList.remove('active'));
                
                // Accende il bottone cliccato e la sua sezione
                btn.classList.add('active');
                const targetId = btn.dataset.target;
                const sectionToShow = document.getElementById(targetId);
                
                if (sectionToShow) {
                    sectionToShow.classList.add('active');
                }
                
                // Aggiorna l'URL aggiungendo il # senza ricaricare la pagina
                window.history.replaceState(null, null, '#' + targetId);
            });
        });
    }

    // ==========================================
    // 2. GESTIONE MODALE MODIFICA PRODOTTO
    // ==========================================
    const editBtns = document.querySelectorAll('.btn-edit-product');
    const editModal = document.getElementById('edit-product-modal');
    const closeEditModal = document.getElementById('close-edit-modal');
    const tipoSelect = document.getElementById('modal-edit-tipo');

    // Funzione per mostrare/nascondere i campi dinamici
    const toggleFields = (tipoValue) => {
        document.querySelectorAll('.field-usato').forEach(el => el.classList.remove('visible'));
        document.querySelectorAll('.field-collezione').forEach(el => el.classList.remove('visible'));
        
        if (tipoValue === 'Usato') {
            document.querySelectorAll('.field-usato').forEach(el => el.classList.add('visible'));
        } else if (tipoValue === 'Collezione') {
            document.querySelectorAll('.field-collezione').forEach(el => el.classList.add('visible'));
        }
    };

    if (editBtns.length > 0 && editModal) {
        
        // Apertura Modale
        editBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                const nome = this.getAttribute('data-nome');
                const prezzo = this.getAttribute('data-prezzo');
                const stock = this.getAttribute('data-stock');
                const tipo = this.getAttribute('data-tipo');
                const descrizione = this.getAttribute('data-descrizione');
                const stato = this.getAttribute('data-stato');
                const scatti = this.getAttribute('data-scatti');
                const condizione = this.getAttribute('data-condizione');

                document.getElementById('modal-edit-id').value = id;
                document.getElementById('modal-edit-nome').value = nome;
                if(prezzo) document.getElementById('modal-edit-prezzo').value = parseFloat(prezzo.replace(',', '.')).toFixed(2);
                document.getElementById('modal-edit-stock').value = stock;
                document.getElementById('modal-edit-tipo').value = tipo;
                document.getElementById('modal-edit-descrizione').value = descrizione;
                
                document.getElementById('modal-edit-stato').value = stato;
                document.getElementById('modal-edit-scatti').value = scatti;
                document.getElementById('modal-edit-condizione').value = condizione;

                toggleFields(tipo);
                editModal.classList.add('active');
            });
        });

        // Cambio Tipo al volo dentro la modale
        if(tipoSelect) {
            tipoSelect.addEventListener('change', function() {
                toggleFields(this.value);
            });
        }

        // Chiusura Modale (X in alto o click fuori)
        if (closeEditModal) {
            closeEditModal.addEventListener('click', () => editModal.classList.remove('active'));
        }
        editModal.addEventListener('click', (e) => {
            if (e.target === editModal) editModal.classList.remove('active');
        });
    }
	
	// ==========================================
	    // 3. GESTIONE FORM AGGIUNGI PRODOTTO
	    // ==========================================
	    const addTipoSelect = document.getElementById('add-tipo');

	    // Funzione specifica per il form di aggiunta
	    const toggleAddFields = (tipoValue) => {
	        // Nascondo tutto
	        document.querySelectorAll('.add-field-usato').forEach(el => el.classList.remove('visible'));
	        document.querySelectorAll('.add-field-collezione').forEach(el => el.classList.remove('visible'));
	        
	        // Mostro solo in base al valore
	        if (tipoValue === 'Usato') {
	            document.querySelectorAll('.add-field-usato').forEach(el => el.classList.add('visible'));
	        } else if (tipoValue === 'Collezione') {
	            document.querySelectorAll('.add-field-collezione').forEach(el => el.classList.add('visible'));
	        }
	    };

	    if (addTipoSelect) {
	        // Ascolto i cambiamenti durante l'inserimento
	        addTipoSelect.addEventListener('change', function() {
	            toggleAddFields(this.value);
	        });
	        
	        // Inizializzazione quando apri la scheda (di default "Nuovo" nasconde i campi)
	        toggleAddFields(addTipoSelect.value);
	    }
		
		// ==========================================
		    // 5. VALIDAZIONE FORM CREAZIONE NUOVO ADMIN
		    // ==========================================
		    const adminForm = document.querySelector('.admin-form');
		    
		    // Il form esiste solo se l'utente loggato è Super Admin
		    if (adminForm) {
		        const btnSubmitAdmin = adminForm.querySelector('.btn-submit');
		        
		        // Recupero campi tramite l'attributo name per evitare conflitti di ID
		        const inputUsername = adminForm.querySelector('input[name="username"]');
		        const inputNome = adminForm.querySelector('input[name="nome"]');
		        const inputCognome = adminForm.querySelector('input[name="cognome"]');
		        const inputEmail = adminForm.querySelector('input[name="adminEmail"]');
		        const inputPassword = adminForm.querySelector('input[name="adminPassword"]');
		        
		        let emailAdminGiaInUso = false;
		        
		        // Disabilita il bottone di default
		        if(btnSubmitAdmin) btnSubmitAdmin.disabled = true;

		        // Funzione per creare i messaggi di errore sotto gli input senza rompere la "row-form"
		        const createErrorSpan = (inputElement) => {
		            const wrapper = document.createElement('div');
		            wrapper.style.display = 'flex';
		            wrapper.style.flexDirection = 'column';
		            wrapper.style.flex = '1'; 
		            wrapper.style.position = 'relative';
		            
		            // Avvolge l'input nel nuovo div
		            inputElement.parentNode.insertBefore(wrapper, inputElement);
		            wrapper.appendChild(inputElement);
		            
		            // Crea lo span per l'errore
		            const errorSpan = document.createElement('span');
		            errorSpan.style.color = 'var(--rosso-ruggine, #d9534f)';
		            errorSpan.style.fontSize = '0.75rem';
		            errorSpan.style.marginTop = '4px';
		            errorSpan.style.display = 'none';
		            errorSpan.style.fontWeight = '600';
		            
		            wrapper.appendChild(errorSpan);
		            return errorSpan;
		        };

		        const errorsAdmin = {
		            username: createErrorSpan(inputUsername),
		            nome: createErrorSpan(inputNome),
		            cognome: createErrorSpan(inputCognome),
		            email: createErrorSpan(inputEmail),
		            password: createErrorSpan(inputPassword)
		        };

		        function mostraErroreAdmin(campo, messaggio) {
		            errorsAdmin[campo].textContent = messaggio;
		            errorsAdmin[campo].style.display = 'block';
		            
		            // Colora il bordo di rosso
		            const fieldName = campo === 'email' ? 'adminEmail' : (campo === 'password' ? 'adminPassword' : campo);
		            adminForm.querySelector(`input[name="${fieldName}"]`).style.borderColor = 'var(--rosso-ruggine, #d9534f)';
		        }

		        function nascondiErroreAdmin(campo) {
		            errorsAdmin[campo].style.display = 'none';
		            
		            // Ripristina il bordo
		            const fieldName = campo === 'email' ? 'adminEmail' : (campo === 'password' ? 'adminPassword' : campo);
		            adminForm.querySelector(`input[name="${fieldName}"]`).style.borderColor = '';
		        }

		        const validatoriAdmin = {
		            nome: () => {
		                if (inputNome.value.trim().length < 2) {
		                    mostraErroreAdmin("nome", "Minimo 2 caratteri.");
		                    return false;
		                }
		                nascondiErroreAdmin("nome"); return true;
		            },
		            cognome: () => {
		                if (inputCognome.value.trim().length < 2) {
		                    mostraErroreAdmin("cognome", "Minimo 2 caratteri.");
		                    return false;
		                }
		                nascondiErroreAdmin("cognome"); return true;
		            },
		            username: () => {
		                if (!inputUsername.value.trim().match(/^[a-zA-Z0-9_]+$/)) {
		                    mostraErroreAdmin("username", "Solo lettere, numeri e underscore.");
		                    return false;
		                }
		                nascondiErroreAdmin("username"); return true;
		            },
		            password: () => {
		                // Sincronizzato col controllo Java nella tua Servlet (minimo 8)
		                if (inputPassword.value.trim().length < 8) {
		                    mostraErroreAdmin("password", "Minimo 8 caratteri richiesti.");
		                    return false;
		                }
		                nascondiErroreAdmin("password"); return true;
		            },
		            email: () => {
		                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		                if (!inputEmail.value.trim().match(emailRegex)) {
		                    mostraErroreAdmin("email", "Formato email non valido.");
		                    return false;
		                }
		                if (emailAdminGiaInUso) {
		                    mostraErroreAdmin("email", "Email già registrata!");
		                    return false;
		                }
		                nascondiErroreAdmin("email"); return true;
		            }
		        };

		        function controllaFormAdmin() {
		            let formValido = true;
		            for (let key in validatoriAdmin) {
		                if (!validatoriAdmin[key]()) {
		                    formValido = false;
		                }
		            }
		            if(btnSubmitAdmin) btnSubmitAdmin.disabled = !formValido;
		        }

		        const campiAdmin = [inputUsername, inputNome, inputCognome, inputEmail, inputPassword];
		        
		        campiAdmin.forEach(campo => {
		            // Controllo real-time durante la digitazione
		            campo.addEventListener("input", controllaFormAdmin);
		            
		            // Controllo AJAX specifico per l'email quando si esce dal campo (blur)
		            if (campo === inputEmail) {
		                campo.addEventListener("blur", async () => {
		                    campo.value = campo.value.trim();
		                    if (campo.value.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
		                        try {
		                            // Uso contestoReFrame dichiarato in pannelloAdmin.jsp
		                            const response = await fetch(contestoReFrame + '/VerificaEmailServlet?email=' + encodeURIComponent(campo.value));
		                            const data = await response.json(); 
		                            emailAdminGiaInUso = data.esiste; 
		                        } catch (error) {
		                            console.error('Errore AJAX Validazione Email Admin:', error);
		                        }
		                    }
		                    controllaFormAdmin();
		                });
		            }
		        });
		    }
});