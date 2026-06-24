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
});