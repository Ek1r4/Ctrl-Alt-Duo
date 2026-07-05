document.addEventListener('DOMContentLoaded', () => {
    
    // 1. GESTIONE TAB E ROUTING DINAMICO
    const btns = document.querySelectorAll('.tab-btn');
    const sections = document.querySelectorAll('.dashboard-section');
    
    // Recupera il parametro di stato iniettato lato server nella JSP per ripristinare il tab attivo post-operazione CRUD, bypassando l'hash dell'URL.
    const triggerTab = document.getElementById('triggerTab').value;

    if (triggerTab === 'prodotti') {
        btns.forEach(b => b.classList.remove('active'));
        sections.forEach(s => s.classList.remove('active'));
        
        document.querySelector('.tab-btn[data-target="prodotti"]').classList.add('active');
        document.getElementById('prodotti').classList.add('active'); 
    } else if (triggerTab === 'superadmin') {
        btns.forEach(b => b.classList.remove('active'));
        sections.forEach(s => s.classList.remove('active'));
                
        document.querySelector('.tab-btn[data-target="superadmin"]').classList.add('active');
        document.getElementById('superadmin').classList.add('active');
    } else if (triggerTab === 'ordini') {
        btns.forEach(b => b.classList.remove('active'));
        sections.forEach(s => s.classList.remove('active'));
                
        document.querySelector('.tab-btn[data-target="ordini"]').classList.add('active');
        document.getElementById('ordini').classList.add('active');
    }

    if (btns.length > 0 && sections.length > 0) {
        
        // Risoluzione Hash URL (Accesso Diretto)
        const hash = window.location.hash;
        const requestedTab = hash ? hash.substring(1) : null;

        if (requestedTab) {
            btns.forEach(b => b.classList.remove('active'));
            sections.forEach(s => s.classList.remove('active'));
            
            const targetBtn = document.querySelector(`.tab-btn[data-target="${requestedTab}"]`);
            const targetSection = document.getElementById(requestedTab);
            
            if (targetBtn && targetSection) {
                targetBtn.classList.add('active');
                targetSection.classList.add('active');
            }
        }

        // Navigazione Manuale Tab
        btns.forEach(btn => {
            btn.addEventListener('click', () => {
                btns.forEach(b => b.classList.remove('active'));
                sections.forEach(s => s.classList.remove('active'));
                
                btn.classList.add('active');
                const targetId = btn.dataset.target;
                const sectionToShow = document.getElementById(targetId);
                
                if (sectionToShow) {
                    sectionToShow.classList.add('active');
                }
                
                /*
                 * History API: Aggiorna l'URL corrente iniettando l'ancora della sezione attiva 
                 * senza innescare un refresh della pagina o aggiungere voci superflue 
                 * allo stack cronologico del browser, permettendo al contempo il link sharing diretto.
                 */
                window.history.replaceState(null, null, '#' + targetId);
            });
        });
    }

    // 2. GESTIONE MODALE MODIFICA PRODOTTO
    const editBtns = document.querySelectorAll('.btn-edit-product');
    const editModal = document.getElementById('edit-product-modal');
    const closeEditModal = document.getElementById('close-edit-modal');
    const tipoSelect = document.getElementById('modal-edit-tipo');

    // Manipolazione DOM condizionale per le varianti prodotto
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
        
        // Popolamento dinamico form tramite Data Attributes HTML5
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
                // Normalizzazione cast numerico per uniformità dei decimali nel campo type="number"
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

        // Trigger in-app per mutazioni selettore categoria
        if(tipoSelect) {
            tipoSelect.addEventListener('change', function() {
                toggleFields(this.value);
            });
        }

        // Routine di chiusura modale (Button + Click Outside)
        if (closeEditModal) {
            closeEditModal.addEventListener('click', () => editModal.classList.remove('active'));
        }
        editModal.addEventListener('click', (e) => {
            if (e.target === editModal) editModal.classList.remove('active');
        });
    }
    
    // 3. GESTIONE FORM AGGIUNGI PRODOTTO
    const addTipoSelect = document.getElementById('add-tipo');

    // Manipolazione DOM condizionale variante prodotto (Form Aggiunta)
    const toggleAddFields = (tipoValue) => {
        document.querySelectorAll('.add-field-usato').forEach(el => el.classList.remove('visible'));
        document.querySelectorAll('.add-field-collezione').forEach(el => el.classList.remove('visible'));
        
        if (tipoValue === 'Usato') {
            document.querySelectorAll('.add-field-usato').forEach(el => el.classList.add('visible'));
        } else if (tipoValue === 'Collezione') {
            document.querySelectorAll('.add-field-collezione').forEach(el => el.classList.add('visible'));
        }
    };

    if (addTipoSelect) {
        addTipoSelect.addEventListener('change', function() {
            toggleAddFields(this.value);
        });
        toggleAddFields(addTipoSelect.value);
    }
        
    // 4. VALIDAZIONE FORM CREAZIONE NUOVO ADMIN
    const adminForm = document.querySelector('.admin-form');
    
    // Vincolo di rendering basato su RBAC (Visibile solo a SuperAdmin)
    if (adminForm) {
        const btnSubmitAdmin = adminForm.querySelector('.btn-submit');
        
        // Estrazione instanze input field via attributo name
        const inputUsername = adminForm.querySelector('input[name="username"]');
        const inputNome = adminForm.querySelector('input[name="nome"]');
        const inputCognome = adminForm.querySelector('input[name="cognome"]');
        const inputEmail = adminForm.querySelector('input[name="adminEmail"]');
        const inputPassword = adminForm.querySelector('input[name="adminPassword"]');
        
        let emailAdminGiaInUso = false;
        
        if(btnSubmitAdmin) btnSubmitAdmin.disabled = true;

        /*
         * Iniezione DOM procedurale per error-reporting: incapsula l'input in un wrapper flex 
         * per agganciare uno span diagnostico invisibile, preservando l'allineamento 
         * orizzontale originale imposto dalla classe '.row-form' sui child diretti.
         */
        const createErrorSpan = (inputElement) => {
            const wrapper = document.createElement('div');
            wrapper.style.display = 'flex';
            wrapper.style.flexDirection = 'column';
            wrapper.style.flex = '1'; 
            wrapper.style.position = 'relative';
            
            inputElement.parentNode.insertBefore(wrapper, inputElement);
            wrapper.appendChild(inputElement);
            
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
            
            const fieldName = campo === 'email' ? 'adminEmail' : (campo === 'password' ? 'adminPassword' : campo);
            adminForm.querySelector(`input[name="${fieldName}"]`).style.borderColor = 'var(--rosso-ruggine, #d9534f)';
        }

        function nascondiErroreAdmin(campo) {
            errorsAdmin[campo].style.display = 'none';
            
            const fieldName = campo === 'email' ? 'adminEmail' : (campo === 'password' ? 'adminPassword' : campo);
            adminForm.querySelector(`input[name="${fieldName}"]`).style.borderColor = '';
        }

        // Validatori sincroni/asincroni
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
                    mostraErroreAdmin("username", "Solo lettere, numeri e _.");
                    return false;
                }
                nascondiErroreAdmin("username"); return true;
            },
            password: () => {
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

        // Aggiornamento iterativo stato button submit
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
            campo.addEventListener("input", controllaFormAdmin);
            
            // Fetch API per verifica univocità email
            if (campo === inputEmail) {
                campo.addEventListener("blur", async () => {
                    campo.value = campo.value.trim();
                    if (campo.value.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
                        try {
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
    
    // 5. ACCORDION TABELLA PRODOTTI (MOBILE)
    const righeProdotti = document.querySelectorAll('#prodotti .data-table tbody tr');
    
    righeProdotti.forEach(riga => {
        riga.addEventListener('click', function(e) {
            if (e.target.closest('button') || e.target.closest('form')) {
                return;
            }
            
            righeProdotti.forEach(r => { 
                if(r !== this) r.classList.remove('open'); 
            });
            
            this.classList.toggle('open');
        });
    });
        
    // 6. CONFERMA ELIMINAZIONE (MODALE CUSTOM)
    const deleteButtons = document.querySelectorAll('.btn-icon.delete');
    const deleteModal = document.getElementById('delete-confirm-modal');
    const deleteMessage = document.getElementById('delete-confirm-message');
    const btnConfirmDelete = document.getElementById('btn-confirm-delete');
    const btnCancelDelete = document.getElementById('btn-cancel-delete');
    
    let formDaInviare = null; 

    if (deleteButtons.length > 0 && deleteModal) {
        
        deleteButtons.forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault(); 
                formDaInviare = this.closest('form'); 
                
                // Discriminazione semantica del target via interrogazione dell'albero DOM per classi icone specifiche
                const isAdminDelete = this.querySelector('.fa-user-times') !== null;
                
                if (isAdminDelete) {
                    deleteMessage.innerHTML = "Sei sicuro di voler revocare l'accesso a questo <strong>Admin</strong>?<br>L'azione è irreversibile.";
                } else {
                    deleteMessage.innerHTML = "Sei sicuro di voler eliminare definitivamente questo <strong>Prodotto</strong> dal catalogo?";
                }
                
                deleteModal.classList.add('active');
            });
        });

        if (btnCancelDelete) {
            btnCancelDelete.addEventListener('click', () => {
                deleteModal.classList.remove('active');
                formDaInviare = null; 
            });
        }

        if (btnConfirmDelete) {
            btnConfirmDelete.addEventListener('click', () => {
                if (formDaInviare) {
                    formDaInviare.submit(); 
                }
            });
        }

        deleteModal.addEventListener('click', (e) => {
            if (e.target === deleteModal) {
                deleteModal.classList.remove('active');
                formDaInviare = null;
            }
        });
    }
    
    // 7. TOAST NOTIFICHE GESTIONE ADMIN
    
    // Estrazione parametri di stato iniettati dal dispatcher lato server nella DOM
    const msgSuccess = document.getElementById('serverSuccess') ? document.getElementById('serverSuccess').value : '';
    const msgError = document.getElementById('serverError') ? document.getElementById('serverError').value : '';
    
    function mostraToastAdmin(messaggio, isSuccess = true) {
        let toast = document.querySelector('.toast-notification');
        
        if (!toast) {
            toast = document.createElement('div');
            toast.className = 'toast-notification';
            document.body.appendChild(toast);
        }
        
        const icona = isSuccess ? 'fa-check-circle' : 'fa-exclamation-triangle';
        toast.innerHTML = `<i class="fas ${icona}"></i> ${messaggio}`;
        
        setTimeout(() => {
            toast.classList.add('show');
        }, 10);
        
        setTimeout(() => {
            toast.classList.remove('show');
        }, 3000);
    }

    if (msgSuccess === 'adminCreato') {
        mostraToastAdmin("Nuovo profilo Admin creato con successo!");
    } else if (msgSuccess === 'adminEliminato') {
        mostraToastAdmin("Accesso admin revocato correttamente.");
    } else if (msgError === 'Impossibile_revocare_accesso') {
        mostraToastAdmin("Errore: Impossibile revocare l'accesso.", false);
    }

});