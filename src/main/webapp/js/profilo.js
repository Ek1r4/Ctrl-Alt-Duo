document.addEventListener("DOMContentLoaded", function() {

    // --- 0. FUNZIONE TOAST NOTIFICATION (MESSAGGI DI CONFERMA) ---
    function showToast(message) {
        // Crea il div del toast
        const toast = document.createElement('div');
        toast.className = 'toast-notification';
        toast.innerHTML = `<i class="fas fa-check-circle"></i> <span>${message}</span>`;
        document.body.appendChild(toast);

        // Anima l'entrata dopo un istante
        setTimeout(() => toast.classList.add('show'), 10);

        // Dopo 3.5 secondi lo nasconde e poi lo distrugge
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 400); // Aspetta che finisca l'animazione CSS
        }, 3500);
    }

    // --- 1. SETUP E INIZIALIZZAZIONE ---
    const btnEditProfile = document.getElementById("btnEditProfile");
    const btnCancelProfile = document.getElementById("btnCancelProfile");
    const btnSaveProfile = document.getElementById("btnSaveProfile");
    const editActions = document.getElementById("editActions");

    const txtTelefono = document.getElementById("txtTelefono");
    const inputTelefono = document.getElementById("inputTelefono");
    const txtBio = document.getElementById("txtBio");
    const textareaBio = document.getElementById("textareaBio");

    const errorBio = document.getElementById("errorBio");
    const errorTel = document.getElementById("errorTelefono");

    const inputVecchia = document.getElementById("inputVecchiaPassword");
    const hintVecchia = document.getElementById("hintVecchiaPassword");


    // --- RICEZIONE MESSAGGI (ERRORE O SUCCESSO) DAL SERVER AL CARICAMENTO ---
    const urlParams = new URLSearchParams(window.location.search);
    
    // Gestione Errore Password
    if (urlParams.get("error") === "vecchiaPasswordErrata") { 
        const btnToggle = document.getElementById("btnPasswordToggle");
        const formContainer = document.getElementById("formPasswordContainer");
        if (btnToggle && formContainer) {
            formContainer.classList.remove("hidden");
            btnToggle.classList.add("hidden");
        }
        if (hintVecchia) {
            hintVecchia.textContent = "La password attuale non è corretta.";
            hintVecchia.classList.add("visible");
        }
    }

    // Gestione Successi (Form standard ricaricati dal Server)
    const successParam = urlParams.get("success");
    if (successParam) {
        let msg = "";
        if (successParam === "passwordModificata") msg = "Password aggiornata con successo!";
        else if (successParam === "spedizioneSalvata") msg = "Indirizzo aggiunto alla rubrica!";
        else if (successParam === "pagamentoSalvato") msg = "Metodo di pagamento salvato!";

        if (msg) setTimeout(() => showToast(msg), 400);
    }

    // Pulizia dell'URL
    if (urlParams.has("error") || urlParams.has("success")) {
        const cleanUrl = window.location.pathname;
        window.history.replaceState({}, document.title, cleanUrl);
    }

    if (inputVecchia && hintVecchia) {
        inputVecchia.addEventListener("input", () => {
            hintVecchia.classList.remove("visible");
        });
    }


    // --- 2. VALIDAZIONE IN TEMPO REALE ---
    
    function checkAnagraficaValidity() {
        if (!inputTelefono || !textareaBio) return;
        const telValue = inputTelefono.value.trim();
        const bioValue = textareaBio.value.trim();
        
        const isTelValid = /^[0-9]{10}$/.test(telValue);
        const isBioValid = bioValue.length <= 255;

        if (telValue.length > 0 && !isTelValid) {
            if (errorTel) {
                errorTel.textContent = "Il numero deve contenere esattamente 10 cifre.";
                errorTel.classList.add("visible");
            }
        } else {
            if (errorTel) errorTel.classList.remove("visible");
        }

        if (!isBioValid) {
            if (errorBio) {
                errorBio.textContent = "La biografia non può superare i 255 caratteri.";
                errorBio.classList.add("visible");
            }
        } else {
            if (errorBio) errorBio.classList.remove("visible");
        }
        
        if (btnSaveProfile) {
            btnSaveProfile.disabled = !(isTelValid && isBioValid);
        }
    }

    if (inputTelefono) inputTelefono.addEventListener("input", checkAnagraficaValidity);
    if (textareaBio) textareaBio.addEventListener("input", checkAnagraficaValidity);


    const allForms = document.querySelectorAll("form");
    allForms.forEach(form => {
        const submitBtn = form.querySelector("button[type='submit']");
        if (submitBtn) {
            
            const checkFormValidity = () => {
                let isValid = form.checkValidity(); 
                
                const actionInput = form.querySelector("input[name='action']");
                if (actionInput && actionInput.value === "cambioPassword") {
                    
                    const inputNuova = form.querySelector("input[name='nuovaPassword']");
                    const inputConferma = form.querySelector("input[name='confermaPassword']");
                    const currentHintVecchia = document.getElementById("hintVecchiaPassword");
                    
                    if (currentHintVecchia && currentHintVecchia.classList.contains("visible")) {
                        isValid = false;
                    }

                    if (inputNuova && inputConferma) {
                        const nuova = inputNuova.value;
                        const conferma = inputConferma.value;
                        const hintNuova = document.getElementById("hintNuovaPassword");
                        const hintConferma = document.getElementById("hintConfermaPassword");
                        const regexPassword = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;

                        if (nuova.length > 0 && !regexPassword.test(nuova)) isValid = false; 
                        if (conferma.length > 0 && nuova !== conferma) isValid = false;

                        if (nuova.length > 0 && hintNuova) {
                            hintNuova.classList.add("visible");
                            if (!regexPassword.test(nuova)) {
                                hintNuova.classList.remove("success");
                                hintNuova.classList.add("error");
                                hintNuova.textContent = "Minimo 8 caratteri: 1 Maiusc, 1 min, 1 num, 1 speciale.";
                            } else {
                                hintNuova.classList.remove("error");
                                hintNuova.classList.add("success");
                                hintNuova.textContent = "✓ Password sicura e valida";
                            }
                        } else if (hintNuova) {
                            hintNuova.classList.remove("visible", "error", "success");
                            hintNuova.textContent = "Minimo 8 caratteri: 1 Maiusc, 1 min, 1 num, 1 speciale.";
                        }

                        if (conferma.length > 0 && hintConferma) {
                            hintConferma.classList.add("visible");
                            if (nuova !== conferma) {
                                hintConferma.classList.remove("success");
                                hintConferma.classList.add("error");
                                hintConferma.textContent = "Le password non coincidono.";
                            } else {
                                hintConferma.classList.remove("error");
                                hintConferma.classList.add("success");
                                hintConferma.textContent = "✓ Le password coincidono";
                            }
                        } else if (hintConferma) {
                            hintConferma.classList.remove("visible", "error", "success");
                            hintConferma.textContent = "Le password non coincidono.";
                        }
                    }
                }
                submitBtn.disabled = !isValid;
            };

            form.addEventListener("input", checkFormValidity);
            checkFormValidity(); 

            form.addEventListener("submit", function() {
                setTimeout(() => {
                    submitBtn.disabled = true;
                    submitBtn.textContent = "Attendere...";
                }, 10);
            });
        }
    });


    // --- 3. GESTIONE TOGGLE MODIFICA INLINE (MATITA) ---
    if (btnEditProfile) {
        btnEditProfile.addEventListener("click", function() {
            txtTelefono.classList.add("hidden");
            txtBio.classList.add("hidden");
            btnEditProfile.classList.add("hidden");
            
            inputTelefono.classList.remove("hidden");
            textareaBio.classList.remove("hidden");
            editActions.classList.remove("hidden");
            
            checkAnagraficaValidity();

            const btnCancelPassword = document.getElementById("btnCancelPassword");
            const formPasswordContainer = document.getElementById("formPasswordContainer");
            if (btnCancelPassword && formPasswordContainer && !formPasswordContainer.classList.contains("hidden")) {
                btnCancelPassword.click();
            }
        });
    }

    if (btnCancelProfile) {
        btnCancelProfile.addEventListener("click", function() {
            txtTelefono.classList.remove("hidden");
            txtBio.classList.remove("hidden");
            btnEditProfile.classList.remove("hidden");
            
            inputTelefono.classList.add("hidden");
            textareaBio.classList.add("hidden");
            editActions.classList.add("hidden");
            
            inputTelefono.value = (txtTelefono.innerText === "Non specificato") ? "" : txtTelefono.innerText;
            textareaBio.value = (txtBio.innerText === "Nessuna biografia inserita.") ? "" : txtBio.innerText;
            
            errorTel.classList.remove("visible");
            if(errorBio) errorBio.classList.remove("visible");
        });
    }


    // --- 4. AGGIORNAMENTO ANAGRAFICA TRAMITE AJAX ---
    if (btnSaveProfile) {
        btnSaveProfile.addEventListener("click", async function() {
            const telValue = inputTelefono.value.trim();
            const bioValue = textareaBio.value.trim();

            btnSaveProfile.disabled = true;
            btnSaveProfile.textContent = "Attendere...";

            const formData = new URLSearchParams();
            formData.append("action", "aggiornaAnagrafica"); 
            formData.append("telefono", telValue);
            formData.append("bio", bioValue);

            try {
                const response = await fetch(contestoReFrame + "/ProfiloServlet", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: formData.toString()
                });

                if (response.url.includes("success=anagrafica")) {
                    txtTelefono.innerText = telValue;
                    txtBio.innerText = bioValue ? bioValue : "Nessuna biografia inserita.";
                    btnCancelProfile.click(); 

                    showToast("Dati anagrafici aggiornati con successo!");
                } 
                else if (response.url.includes("error=telefonoObbligatorio")) {
                    errorTel.textContent = "Errore dal server: Telefono obbligatorio.";
                    errorTel.classList.add("visible");
                } 
            } catch (error) {
                console.error("Errore AJAX:", error);
            } finally {
                btnSaveProfile.textContent = "Salva";
                checkAnagraficaValidity(); 
            }
        });
    }


    // --- 5. CANCELLAZIONE RISORSE TRAMITE AJAX (CESTINO) ---
    const deleteButtons = document.querySelectorAll(".btn-delete");
    
    deleteButtons.forEach(button => {
        button.addEventListener("click", async function() {
            const rowItem = button.closest(".info-row-item");
            const itemId = rowItem.getAttribute("data-item-id");
            const itemType = rowItem.getAttribute("data-type");

            if (confirm("Sei sicuro di voler eliminare definitivamente questo elemento?")) {
                
                button.disabled = true;
                button.style.opacity = "0.5";
                button.style.cursor = "not-allowed";

                try {
                    const formData = new URLSearchParams();
                    formData.append("action", "eliminaRisorsa"); 
                    formData.append("id", itemId);
                    formData.append("type", itemType);

                    const response = await fetch(contestoReFrame + "/ProfiloServlet", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
                        body: formData.toString()
                    });

                    if (response.url.includes("success=eliminazione")) {
                        rowItem.style.opacity = "0";
                        setTimeout(() => { 
                            const parentContainer = rowItem.parentElement;
                            rowItem.remove(); 
                            
                            const remainingItems = parentContainer.querySelectorAll(".info-row-item");
                            if (remainingItems.length === 0) {
                                const emptyMsg = document.createElement("p");
                                emptyMsg.className = "empty-message";
                                emptyMsg.textContent = itemType === "shipping" 
                                    ? "Nessun indirizzo di spedizione salvato." 
                                    : "Nessun metodo di pagamento salvato.";
                                parentContainer.appendChild(emptyMsg);
                            }
                            
                            showToast(itemType === "shipping" ? "Indirizzo rimosso!" : "Metodo di pagamento rimosso!");

                        }, 300);
                    } 
                    else {
                        alert("Si è verificato un errore durante l'eliminazione.");
                        button.disabled = false;
                        button.style.opacity = "1";
                        button.style.cursor = "pointer";
                    }
                } catch (error) {
                    console.error("Errore AJAX:", error);
                    button.disabled = false;
                    button.style.opacity = "1";
                    button.style.cursor = "pointer";
                }
            }
        });
    });
    
    // --- 6. GESTIONE FORM AGGIUNTA RISORSE E RESET ---
    const resetFormState = (formContainer) => {
        const form = formContainer.querySelector("form");
        if (form) {
            form.reset();
            form.dispatchEvent(new Event('input'));
            const comments = form.querySelectorAll(".input-comment");
            comments.forEach(c => c.classList.remove("visible", "error", "success"));
        }
    };

    const btnAddSpedizione = document.getElementById("btnAddSpedizione");
    const formSpedizioneContainer = document.getElementById("formSpedizioneContainer");
    const btnCancelSpedizione = document.getElementById("btnCancelSpedizione");

    if (btnAddSpedizione && formSpedizioneContainer) {
        btnAddSpedizione.addEventListener("click", () => {
            resetFormState(formSpedizioneContainer);
            formSpedizioneContainer.classList.remove("hidden");
            btnAddSpedizione.classList.add("hidden"); 
        });
        
        btnCancelSpedizione.addEventListener("click", () => {
            formSpedizioneContainer.classList.add("hidden");
            btnAddSpedizione.classList.remove("hidden"); 
        });
    }

    const btnAddPagamento = document.getElementById("btnAddPagamento");
    const formPagamentoContainer = document.getElementById("formPagamentoContainer");
    const btnCancelPagamento = document.getElementById("btnCancelPagamento");

    if (btnAddPagamento && formPagamentoContainer) {
        btnAddPagamento.addEventListener("click", () => {
            resetFormState(formPagamentoContainer);
            formPagamentoContainer.classList.remove("hidden");
            btnAddPagamento.classList.add("hidden");
        });
        
        btnCancelPagamento.addEventListener("click", () => {
            formPagamentoContainer.classList.add("hidden");
            btnAddPagamento.classList.remove("hidden");
        });
    }
    
    // --- 7. GESTIONE FORM CAMBIO PASSWORD E RESET ---
    const btnPasswordToggle = document.getElementById("btnPasswordToggle");
    const formPasswordContainer = document.getElementById("formPasswordContainer");
    const btnCancelPassword = document.getElementById("btnCancelPassword");

    if (btnPasswordToggle && formPasswordContainer) {
        btnPasswordToggle.addEventListener("click", () => {
            resetFormState(formPasswordContainer);
            formPasswordContainer.classList.remove("hidden");
            btnPasswordToggle.classList.add("hidden");
            
            if (btnCancelProfile && !editActions.classList.contains("hidden")) {
                btnCancelProfile.click();
            }
        });
        
        btnCancelPassword.addEventListener("click", () => {
            formPasswordContainer.classList.add("hidden");
            btnPasswordToggle.classList.remove("hidden");
        });
    }
	
	// --- 8. MENU A TENDINA PER CELLULARI (ACCORDION) ---
    const accordionCards = document.querySelectorAll('.scrollable-column .profile-card');
    
    accordionCards.forEach(card => {
        card.classList.add('accordion');
        const header = card.querySelector('.card-header');
        if (header) {
            header.addEventListener('click', function(e) {
                if (e.target.closest('.btn-add')) {
                    card.classList.add('open');
                    return;
                }
                card.classList.toggle('open');
            });
        }
    });
});
function openOrderModal(id) {
    const modale = document.getElementById('modal-' + id);
    if (modale) {
        modale.classList.remove('hidden');
        document.body.style.overflow = 'hidden'; // Blocca lo scroll della pagina dietro
    }
}

// 3. Logica di chiusura Modali Ordini
function closeOrderModal(id) {
    const modale = document.getElementById('modal-' + id);
    if (modale) {
        modale.classList.add('hidden');
        document.body.style.overflow = 'auto'; // Riattiva lo scroll
    }
}

// 4. Chiudi il modale se l'utente clicca fuori (sullo sfondo scuro)
window.addEventListener('click', function(event) {
    if (event.target.classList.contains('order-modal-overlay')) {
        event.target.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
});
// 1. Funzione per filtrare lo storico ordini in tempo reale
function filtraOrdini() {
    const inputElement = document.getElementById("searchHistory");
    if (!inputElement) return;
    
    const input = inputElement.value.toUpperCase();
    const righe = document.querySelectorAll(".history-content .order-row");
    const messaggioVuoto = document.getElementById("noSearchResults");
    
    let righeVisibili = 0;

    righe.forEach(riga => {
        // Legge tutto il testo della riga
        const testoRiga = riga.innerText.toUpperCase();
        
        // Controlla se c'è un match
        if (testoRiga.indexOf(input) > -1) {
            riga.style.display = "flex"; 
            righeVisibili++; // Contiamo quante righe restano visibili
        } else {
            riga.style.display = "none";
        }
    });

    // Se abbiamo righe in totale, ma nessuna è visibile, mostriamo il messaggio di errore
    if (messaggioVuoto) {
        if (righe.length > 0 && righeVisibili === 0) {
            messaggioVuoto.classList.remove("hidden");
        } else {
            messaggioVuoto.classList.add("hidden");
        }
    }
}













