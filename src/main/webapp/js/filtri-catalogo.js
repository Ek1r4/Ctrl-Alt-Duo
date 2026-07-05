document.addEventListener("DOMContentLoaded", () => {
    
    /* INIZIALIZZAZIONE E GESTIONE EVENTI FILTRI */
    
    const checkboxes = document.querySelectorAll('.filter-label input[type="checkbox"]');
    const searchInput = document.querySelector('.catalog-search-input');
    const searchBtn = document.querySelector('.catalog-search-btn');
    const btnReset = document.getElementById('btn-reset-filtri');

    let debounceTimer; 

    checkboxes.forEach(chk => {
        chk.addEventListener('change', applicaFiltri);
    });

    if (searchInput) {
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer); 
            debounceTimer = setTimeout(() => {
                applicaFiltri(); 
            }, 300); 
        });
    }
    
    if (searchBtn) {
        searchBtn.addEventListener('click', applicaFiltri);
    }

    if (btnReset) {
        btnReset.addEventListener('click', () => {
            checkboxes.forEach(chk => chk.checked = false);
            if (searchInput) searchInput.value = '';
            applicaFiltri();
        });
    }
    
    /* GESTIONE SIDEBAR MOBILE */
    
    const btnToggleFilters = document.getElementById('btn-toggle-filters');
    const btnCloseFilters = document.getElementById('btn-close-filters');
    const sidebar = document.querySelector('.catalog-sidebar');

    if (btnToggleFilters && sidebar) {
        btnToggleFilters.addEventListener('click', () => {
            sidebar.classList.add('open');
        });
    }

    if (btnCloseFilters && sidebar) {
        btnCloseFilters.addEventListener('click', () => {
            sidebar.classList.remove('open');
        });
    }

    /* CHIAMATA AJAX APPLICAZIONE FILTRI */
    
    function applicaFiltri() {
        const marche = Array.from(document.querySelectorAll('input[name="marca"]:checked')).map(cb => cb.value);
        const prezzi = Array.from(document.querySelectorAll('input[name="prezzo"]:checked')).map(cb => cb.value);
        const searchText = searchInput ? searchInput.value.trim() : '';

        const params = new URLSearchParams();
        params.append("ajax", "true");
        marche.forEach(m => params.append("marca", m));
        prezzi.forEach(p => params.append("prezzo", p));
        
        if (searchText) {
            params.append("search", searchText);
        }
        
        /* Recupera il parametro 'tipo' dalla query string dell'URL corrente per preservare il contesto di navigazione durante le mutazioni asincrone della griglia. */
        const urlParams = new URLSearchParams(window.location.search);
        const tipoCorrente = urlParams.get('tipo');
        if (tipoCorrente) {
            params.append("tipo", tipoCorrente);
        }

        fetch(contextPath + "/ProdottoServlet?" + params.toString())
            .then(response => {
                if (!response.ok) throw new Error("Errore rete");
                return response.text(); 
            })
            .then(html => {
                document.getElementById('grid-container').innerHTML = html;
            })
            .catch(error => console.error("Errore AJAX:", error));
    }
    
    /* MODALE CONFERMA ELIMINAZIONE PRODOTTO */
    
    const deleteButtons = document.querySelectorAll('.btn-delete-product'); 
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
                
                deleteMessage.innerHTML = "Sei sicuro di voler rimuovere questo <strong>Prodotto</strong>?";
                deleteModal.classList.add('active');
            });
        });

        const chiudiModale = () => {
            deleteModal.classList.remove('active');
            formDaInviare = null;
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
                if (formDaInviare) {
                    formDaInviare.submit(); 
                }
            });
        }
    }
        
    /* GESTIONE TOAST NOTIFICHE */
    
    window.mostraToastNotifica = function(messaggio, isSuccess = true) {
        let toast = document.querySelector('.toast-notification');
        
        if (!toast) {
            toast = document.createElement('div');
            toast.className = 'toast-notification';
            document.body.appendChild(toast);
        }
        
        const icona = isSuccess ? 'fa-check-circle' : 'fa-exclamation-circle';
        const colore = 'var(--verde-ottanio)'; 
        
        toast.innerHTML = `<i class="fas ${icona}" style="color: ${colore};"></i> <span>${messaggio}</span>`;
        toast.style.borderLeftColor = colore;
        
        toast.classList.remove('show');
        
        /* Hack basato su un timeout di 10ms per forzare il ricalcolo del DOM (reflow) da parte del browser, garantendo l'esecuzione della transizione CSS per l'aggiunta della classe 'show'. */
        setTimeout(() => {
            toast.classList.add('show');
        }, 10);
        
        if (window.toastTimeout) clearTimeout(window.toastTimeout);
        
        window.toastTimeout = setTimeout(() => {
            toast.classList.remove('show');
        }, 3000);
    };  
});