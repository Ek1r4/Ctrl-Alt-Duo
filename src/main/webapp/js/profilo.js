document.addEventListener("DOMContentLoaded", function() {

    // --- 1. SETUP E INIZIALIZZAZIONE ---
    // Ganci per i bottoni anagrafica
    const btnEditProfile = document.getElementById("btnEditProfile");
    const btnCancelProfile = document.getElementById("btnCancelProfile");
    const btnSaveProfile = document.getElementById("btnSaveProfile");
    const editActions = document.getElementById("editActions");

    // Ganci per i campi "Read/Write"
    const txtTelefono = document.getElementById("txtTelefono");
    const inputTelefono = document.getElementById("inputTelefono");
    const txtBio = document.getElementById("txtBio");
    const textareaBio = document.getElementById("textareaBio");

    // Gancio errori
	const errorBio = document.getElementById("errorBio");
    const errorTel = document.getElementById("errorTelefono");

    // Variabile di sicurezza: memorizza l'URL originale per il redirect in JS
    const baseUrl = contestoReFrame + "/common/profilo.jsp";


	// --- 2. GESTIONE TOGGLE MODIFICA INLINE (MATITA) ---
	    if (btnEditProfile) {
	        btnEditProfile.addEventListener("click", function() {
	            // 1. Nascondiamo i testi statici e la matita
	            txtTelefono.classList.add("hidden");
	            txtBio.classList.add("hidden");
	            btnEditProfile.classList.add("hidden");
	            
	            // 2. Mostriamo gli input e i pulsanti Salva/Annulla
	            inputTelefono.classList.remove("hidden");
	            textareaBio.classList.remove("hidden");
	            editActions.classList.remove("hidden");

	            // 3. NUOVO: CHIUSURA INCROCIATA
	            // Se il form della password è aperto, lo chiudiamo forzatamente simulando il click su "Annulla"
	            const btnCancelPassword = document.getElementById("btnCancelPassword");
	            const formPasswordContainer = document.getElementById("formPasswordContainer");
	            if (btnCancelPassword && formPasswordContainer && !formPasswordContainer.classList.contains("hidden")) {
	                btnCancelPassword.click();
	            }
	        });
	    }

    if (btnCancelProfile) {
        btnCancelProfile.addEventListener("click", function() {
            // Stato LETTURA: Mostriamo p e matita
            txtTelefono.classList.remove("hidden");
            txtBio.classList.remove("hidden");
            btnEditProfile.classList.remove("hidden");
            
            // Stato MODIFICA: Nascondiamo input e bottoni
            inputTelefono.classList.add("hidden");
            textareaBio.classList.add("hidden");
            editActions.classList.add("hidden");
            
            // Ripristiniamo i valori originari negli input se modificati
            inputTelefono.value = (txtTelefono.textContent === "Non specificato") ? "" : txtTelefono.textContent;
            textareaBio.value = (txtBio.textContent === "Nessuna biografia inserita.") ? "" : txtBio.textContent;
            errorTel.classList.remove("visible");
        });
    }


    // --- 3. AGGIORNAMENTO ANAGRAFICA TRAMITE AJAX (FETCH + SERVLET REDIRECT) ---
    if (btnSaveProfile) {
        btnSaveProfile.addEventListener("click", async function() {
			console.log("Bottone Salva premuto!");
            const telValue = inputTelefono.value.trim();
            const bioValue = textareaBio.value.trim();

            // 1A. VALIDAZIONE: Campo OBBLIGATORIO (come da logica Java backend)
            if (!telValue.match(/^[0-9]{10}$/)) {
                errorTel.textContent = "Il numero di telefono è obbligatorio e deve contenere esattamente 10 cifre.";
                errorTel.classList.add("visible");
                return;
            }
            errorTel.classList.remove("visible");
			
			// 1B. VALIDAZIONE: Biografia (Massimo 255 caratteri)
			if (bioValue.length > 255) {
			   	errorBio.textContent = "La biografia non può superare i 255 caratteri (ne hai inseriti " + bioValue.length + ").";
				errorBio.classList.add("visible");
			   	return;
			}
			if (errorBio) errorBio.classList.remove("visible");

            // 2. PREPARAZIONE DATI PER SERVLET
            // FormData per inviare parametri in POST alla TUA ProfiloServlet
            const formData = new URLSearchParams();
            formData.append("action", "aggiornaAnagrafica"); // Il parametro chiave per lo switch
            formData.append("telefono", telValue);
            formData.append("bio", bioValue);

            try {
                // 3. CHIAMATA AJAX (FETCH)
                // Usiamo il contestoReFrame passato dalla JSP per un URL infallibile
                const response = await fetch(contestoReFrame + "/ProfiloServlet", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: formData.toString()
                });

                // 4. GESTIONE REDIRECT SERVLET
                // La tua Servlet esegue un sendRedirect. La Fetch API lo segue silenziosamente.
                // Verifichiamo se l'URL finale caricato di nascosto contiene il messaggio di successo.
                if (response.url.includes("success=anagrafica")) {
                    // Aggiorniamo l'interfaccia visiva
                    txtTelefono.textContent = telValue;
                    txtBio.textContent = (bioValue === "") ? "Nessuna biografia inserita." : bioValue;
                    
                    // Chiudiamo l'interfaccia di modifica
                    btnCancelProfile.click();
                    alert("Profilo aggiornato con successo!"); 
                } 
                else if (response.url.includes("error=telefonoObbligatorio")) {
                    errorTel.textContent = "Errore dal server: Telefono obbligatorio e di 10 cifre.";
                    errorTel.classList.add("visible");
                } 
                else {
                    alert("Errore generico durante l'aggiornamento. Riprova più tardi.");
                }
            } catch (error) {
                console.error("Errore AJAX durante il salvataggio del profilo:", error);
            }
        });
    }


	// --- 4. CANCELLAZIONE RISORSE TRAMITE AJAX (CESTINO) ---
	    const deleteButtons = document.querySelectorAll(".btn-delete");
	    
	    deleteButtons.forEach(button => {
	        button.addEventListener("click", async function() {
	            // Recuperiamo l'ID e il Tipo (shipping o payment) dai data-attribute della riga HTML
	            const rowItem = button.closest(".info-row-item");
	            const itemId = rowItem.getAttribute("data-item-id");
	            const itemType = rowItem.getAttribute("data-type");

	            if (confirm("Sei sicuro di voler eliminare definitivamente questo elemento?")) {
	                try {
	                    // 1. PREPARAZIONE DATI DA INVIARE ALLA SERVLET (Come se fosse un form)
	                    const formData = new URLSearchParams();
	                    formData.append("action", "eliminaRisorsa"); // Fa scattare il 'case' giusto nel tuo switch
	                    formData.append("id", itemId);
	                    formData.append("type", itemType);

	                    // 2. CHIAMATA AJAX (FETCH) IN POST VERSO LA PROFILOSERVLET
	                    const response = await fetch(contestoReFrame + "/ProfiloServlet", {
	                        method: "POST",
	                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	                        body: formData.toString()
	                    });

	                    // 3. GESTIONE DEL REDIRECT DELLA SERVLET
	                    // La servlet risponde con un sendRedirect. Controlliamo l'URL finale.
	                    if (response.url.includes("success=eliminazione")) {
	                        // Successo! Facciamo sparire l'elemento dallo schermo con una transizione
	                        rowItem.style.opacity = "0";
	                        setTimeout(() => { rowItem.remove(); }, 300);
	                    } 
	                    else if (response.url.includes("error=eliminazioneFallita")) {
	                        alert("Errore nel database: Impossibile eliminare la risorsa.");
	                    } 
	                    else {
	                        alert("Si è verificato un errore durante l'eliminazione.");
	                    }
	                } catch (error) {
	                    console.error("Errore AJAX durante l'eliminazione:", error);
	                    alert("Errore di connessione col server.");
	                }
	            }
	        });
	    });
		
		// --- 5. GESTIONE FORM AGGIUNTA RISORSE (I bottoni "+") ---

		    // Ganci per form Spedizioni
		    const btnAddSpedizione = document.getElementById("btnAddSpedizione");
		    const formSpedizioneContainer = document.getElementById("formSpedizioneContainer");
		    const btnCancelSpedizione = document.getElementById("btnCancelSpedizione");

		    if (btnAddSpedizione && formSpedizioneContainer) {
		        btnAddSpedizione.addEventListener("click", () => {
					// Mostriamo il form delle spedizioni
		            formSpedizioneContainer.classList.remove("hidden");
		            btnAddSpedizione.classList.add("hidden"); 
		        });
		        
		        btnCancelSpedizione.addEventListener("click", () => {
					// Nascondiamo il form delle spedizioni e rimostriamo il +
		            formSpedizioneContainer.classList.add("hidden");
		            btnAddSpedizione.classList.remove("hidden"); 
		        });
		    }

		    // Ganci per form Pagamenti
		    const btnAddPagamento = document.getElementById("btnAddPagamento");
		    const formPagamentoContainer = document.getElementById("formPagamentoContainer");
		    const btnCancelPagamento = document.getElementById("btnCancelPagamento");

		    if (btnAddPagamento && formPagamentoContainer) {
		        btnAddPagamento.addEventListener("click", () => {
					// Mostriamo il form dei pagamenti
		            formPagamentoContainer.classList.remove("hidden");
		            btnAddPagamento.classList.add("hidden");
		        });
		        
		        btnCancelPagamento.addEventListener("click", () => {
					// Nascondiamo il form dei pagamenti e rimostriamo il +
		            formPagamentoContainer.classList.add("hidden");
		            btnAddPagamento.classList.remove("hidden");
		        });
		    }
			
			// --- 6. GESTIONE FORM CAMBIO PASSWORD (Icona Chiave) ---
			    const btnPasswordToggle = document.getElementById("btnPasswordToggle");
			    const formPasswordContainer = document.getElementById("formPasswordContainer");
			    const btnCancelPassword = document.getElementById("btnCancelPassword");

			    if (btnPasswordToggle && formPasswordContainer) {
			        btnPasswordToggle.addEventListener("click", () => {
			            // Mostriamo il form della password
			            formPasswordContainer.classList.remove("hidden");
			            btnPasswordToggle.classList.add("hidden");
			            
			            // Se il form di modifica anagrafica era aperto, lo chiudiamo per evitare sovrapposizioni
			            if (btnCancelProfile && !editActions.classList.contains("hidden")) {
			                btnCancelProfile.click();
			            }
			        });
			        
			        btnCancelPassword.addEventListener("click", () => {
			            // Nascondiamo il form della password e rimostriamo la chiave
			            formPasswordContainer.classList.add("hidden");
			            btnPasswordToggle.classList.remove("hidden");
			        });
			    }
});