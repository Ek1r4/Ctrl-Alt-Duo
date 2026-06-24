document.addEventListener('DOMContentLoaded', () => {
    // ==========================================
    // GESTIONE TAB DELLA DASHBOARD ADMIN
    // ==========================================
    const btns = document.querySelectorAll('.tab-btn');
    const sections = document.querySelectorAll('.dashboard-section');

    if (btns.length > 0 && sections.length > 0) {
        btns.forEach(btn => {
            btn.addEventListener('click', () => {
                // 1. Rimuove la classe active da tutti i bottoni e tutte le sezioni
                btns.forEach(b => b.classList.remove('active'));
                sections.forEach(s => s.classList.remove('active'));
                
                // 2. Aggiunge la classe active al bottone cliccato
                btn.classList.add('active');
                
                // 3. Mostra la sezione corrispondente usando l'attributo data-target
                const targetId = btn.dataset.target;
                const targetSection = document.getElementById(targetId);
                
                if (targetSection) {
                    targetSection.classList.add('active');
                }
            });
        });
    }
});