/* UTILITIES E NOTIFICHE */

// Inietta proceduralmente il componente toast nel DOM qualora non esista.
function showToast(message) {
    let toast = document.querySelector('.toast-notification');
    
    if (!toast) {
        toast = document.createElement('div');
        toast.className = 'toast-notification';
        document.body.appendChild(toast);
    }
    
    toast.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    toast.classList.add('show');
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

document.addEventListener('DOMContentLoaded', () => {
    
    /* GESTIONE QUANTITÀ E STOCK */

    const btnPlus = document.getElementById('btn-plus');
    const btnMinus = document.getElementById('btn-minus');
    const qtyInput = document.getElementById('qty-input');

    if (btnPlus && btnMinus && qtyInput) {
        const maxStock = parseInt(qtyInput.getAttribute('data-stock'), 10);

        btnPlus.addEventListener('click', () => {
            let currentQty = parseInt(qtyInput.value, 10);
            
            if (currentQty < maxStock) {
                qtyInput.value = currentQty + 1;
            } else {
                showToast("Stock esaurito! Hai raggiunto la quantità massima disponibile.");
            }
        });

        btnMinus.addEventListener('click', () => {
            let currentQty = parseInt(qtyInput.value, 10);
            if (currentQty > 1) {
                qtyInput.value = currentQty - 1;
            }
        });
    }

    /* MODALE EDIT PRODOTTO (ADMIN) */

    const btnOpenEdit = document.getElementById('open-edit-modal');
    const btnCloseEdit = document.getElementById('close-edit-modal');
    const editModal = document.getElementById('edit-product-modal');

    if (btnOpenEdit && btnCloseEdit && editModal) {
        btnOpenEdit.addEventListener('click', () => {
            editModal.classList.add('active');
            document.body.style.overflow = 'hidden'; 
        });

        btnCloseEdit.addEventListener('click', () => {
            editModal.classList.remove('active');
            document.body.style.overflow = 'auto'; 
        });

        editModal.addEventListener('click', (e) => {
            if (e.target === editModal) {
                editModal.classList.remove('active');
                document.body.style.overflow = 'auto';
            }
        });
    }

    /* MODALE ELIMINAZIONE RECENSIONI */

    const deleteReviewBtns = document.querySelectorAll('.btn-delete-review'); 
    const deleteModal = document.getElementById('delete-confirm-modal');
    const deleteMessage = document.getElementById('delete-confirm-message');
    const btnConfirmDelete = document.getElementById('btn-confirm-delete');
    const btnCancelDelete = document.getElementById('btn-cancel-delete');

    let formRecensioneDaInviare = null; 

    if (deleteReviewBtns.length > 0 && deleteModal) {
        
        deleteReviewBtns.forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                formRecensioneDaInviare = this.closest('form'); 
                deleteMessage.innerHTML = "Sei sicuro di voler rimuovere definitivamente questa <strong>Recensione</strong>?";
                deleteModal.classList.add('active');
            });
        });

        const chiudiModale = () => {
            deleteModal.classList.remove('active');
            formRecensioneDaInviare = null;
        };

        if (btnCancelDelete) {
            btnCancelDelete.addEventListener('click', chiudiModale);
        }

        deleteModal.addEventListener('click', (e) => {
            if (e.target === deleteModal) {
                chiudiModale();
            }
        });

        if (btnConfirmDelete) {
            btnConfirmDelete.addEventListener('click', () => {
                if (formRecensioneDaInviare) {
                    formRecensioneDaInviare.submit(); 
                }
            });
        }
    }

});