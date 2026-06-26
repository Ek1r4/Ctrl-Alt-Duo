document.addEventListener('DOMContentLoaded', function() {
    const btnMinus = document.getElementById('btn-minus');
    const btnPlus = document.getElementById('btn-plus');
    const inputQty = document.getElementById('qty-input');

    if (btnMinus && btnPlus && inputQty) {
        btnMinus.addEventListener('click', () => {
            let currentValue = parseInt(inputQty.value);
            if (currentValue > 1) {
                inputQty.value = currentValue - 1;
            }
        });

        btnPlus.addEventListener('click', () => {
            let currentValue = parseInt(inputQty.value);
            if (currentValue < 10) { // Limite massimo impostato a 10 pezzi
                inputQty.value = currentValue + 1;
            }
        });
    }
	
	// ==========================================
	    // GESTIONE MODALE EDIT (Solo Admin)
	    // ==========================================
	    const btnOpenEdit = document.getElementById('open-edit-modal');
	    const btnCloseEdit = document.getElementById('close-edit-modal');
	    const editModal = document.getElementById('edit-product-modal');

	    if (btnOpenEdit && btnCloseEdit && editModal) {
	        // Apri la modale cliccando la matita
	        btnOpenEdit.addEventListener('click', () => {
	            editModal.classList.add('active');
	            document.body.style.overflow = 'hidden'; // Blocca lo scroll della pagina dietro
	        });

	        // Chiudi la modale cliccando la X
	        btnCloseEdit.addEventListener('click', () => {
	            editModal.classList.remove('active');
	            document.body.style.overflow = 'auto'; // Riabilita lo scroll
	        });

	        // Chiudi la modale cliccando sullo sfondo scuro fuori dalla finestra
	        editModal.addEventListener('click', (e) => {
	            if (e.target === editModal) {
	                editModal.classList.remove('active');
	                document.body.style.overflow = 'auto';
	            }
	        });
	    }

		    // ==========================================
		    // MODALE CONFERMA ELIMINAZIONE (RECENSIONI)
		    // ==========================================
		    
		    // Cerca tutti i cestini delle recensioni
		    const deleteReviewBtns = document.querySelectorAll('.btn-delete-review'); 
		    
		    // Elementi del modale (già presenti in dettaglioProdotto.jsp)
		    const deleteModal = document.getElementById('delete-confirm-modal');
		    const deleteMessage = document.getElementById('delete-confirm-message');
		    const btnConfirmDelete = document.getElementById('btn-confirm-delete');
		    const btnCancelDelete = document.getElementById('btn-cancel-delete');
		    
		    let formRecensioneDaInviare = null; // Memoria temporanea

		    if (deleteReviewBtns.length > 0 && deleteModal) {
		        
		        // Quando l'admin clicca il cestino...
		        deleteReviewBtns.forEach(btn => {
		            btn.addEventListener('click', function(e) {
		                e.preventDefault(); // Blocca l'invio immediato del form
		                
		                formRecensioneDaInviare = this.closest('form'); // Salva il form specifico di quella recensione
		                
		                // Personalizza il testo del modale
		                deleteMessage.innerHTML = "Sei sicuro di voler rimuovere definitivamente questa <strong>Recensione</strong>?";
		                
		                // Fa apparire il modale
		                deleteModal.classList.add('active');
		            });
		        });

		        // Funzione per chiudere e resettare
		        const chiudiModale = () => {
		            deleteModal.classList.remove('active');
		            formRecensioneDaInviare = null;
		        };

		        // Click su "Annulla"
		        if (btnCancelDelete) {
		            btnCancelDelete.addEventListener('click', chiudiModale);
		        }

		        // Click fuori dalla finestra (sullo sfondo scuro)
		        deleteModal.addEventListener('click', (e) => {
		            if (e.target === deleteModal) {
		                chiudiModale();
		            }
		        });

		        // Click su "Procedi"
		        if (btnConfirmDelete) {
		            btnConfirmDelete.addEventListener('click', () => {
		                // Se c'è un form salvato in memoria, sparalo al server!
		                if (formRecensioneDaInviare) {
		                    formRecensioneDaInviare.submit(); 
		                }
		            });
		        }
		    }
});