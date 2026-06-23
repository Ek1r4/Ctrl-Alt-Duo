document.addEventListener("DOMContentLoaded", () => {
    const checkboxes = document.querySelectorAll('.filter-label input[type="checkbox"]');
    const searchInput = document.querySelector('.catalog-search-input');
    const searchBtn = document.querySelector('.catalog-search-btn');
    const btnReset = document.getElementById('btn-reset-filtri');

    let debounceTimer; // Variabile per tenere traccia del timer

    // 1. Ascoltatore per i checkbox (istantaneo)
    checkboxes.forEach(chk => {
        chk.addEventListener('change', applicaFiltri);
    });

    // 2. Ascoltatore per la barra di ricerca (CON DEBOUNCE)
    if (searchInput) {
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer); // Cancella il timer precedente se l'utente sta ancora scrivendo
            debounceTimer = setTimeout(() => {
                applicaFiltri(); // Esegue la ricerca solo dopo 300ms di inattività
            }, 300); 
        });
    }
    
    // Ascoltatore per il click sull'icona della lente
    if (searchBtn) {
        searchBtn.addEventListener('click', applicaFiltri);
    }

    // 3. Ascoltatore per la gomma (reset totale)
    if (btnReset) {
        btnReset.addEventListener('click', () => {
            checkboxes.forEach(chk => chk.checked = false);
            if (searchInput) searchInput.value = '';
            applicaFiltri();
        });
    }

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
});