document.addEventListener("DOMContentLoaded", function() {

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


    // --- RICEZIONE ERRORE VECCHIA PASSWORD DAL SERVER ---
    const urlParams = new URLSearchParams(window.location.search);
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

        const cleanUrl = window.location.pathname;
        window.history.replaceState({}, document.title, cleanUrl);
    }

    if (inputVecchia && hintVecchia) {
        inputVecchia.addEventListener("input", () => {
            hintVecchia.classList.remove("visible");
        });
    }


    // --- 2. VALIDAZIONE IN TEMPO REALE ---
    
    // A) Validazione Anagrafica (Telefono e Bio) con Feedback Visivo
    function checkAnagraficaValidity() {
        if (!inputTelefono || !textareaBio) return;
        const telValue = inputTelefono.value.trim();
        const bioValue = textareaBio.value.trim();
        
        const isTelValid = /^[0-9]{10}$/.test(telValue);
        const isBioValid = bioValue.length <= 255;

        // Feedback in tempo reale per il Telefono
        if (telValue.length > 0 && !isTelValid) {
            if (errorTel) {
                errorTel.textContent = "Il numero deve contenere esattamente 10 cifre.";
                errorTel.classList.add("visible");
            }
        } else {
            if (errorTel) errorTel.classList.remove("visible");
        }

        // Feedback in tempo reale per la Bio
        if (!isBioValid) {
            if (errorBio) {
                errorBio.textContent = "La biografia non può superare i 255 caratteri.";
                errorBio.classList.add("visible");
            }
        } else {
            if (errorBio) errorBio.classList.remove("visible");
        }
        
        // Accende o spegne il tasto "Salva"
        if (btnSaveProfile) {
            btnSaveProfile.disabled = !(isTelValid && isBioValid);
        }
    }

    // Ascoltatori per far scattare il controllo ad ogni carattere digitato
    if (inputTelefono) inputTelefono.addEventListener("input", checkAnagraficaValidity);
    if (textareaBio) textareaBio.addEventListener("input", checkAnagraficaValidity);


    // B) Validazione Form Standard (Spedizioni, Pagamenti, Password)
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

                        if (nuova.length > 0 && !regexPassword.test(nuova)) {
                            isValid = false; 
                        }
                        if (conferma.length > 0 && nuova !== conferma) {
                            isValid = false;
                        }

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
});