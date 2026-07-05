document.addEventListener("DOMContentLoaded", function() {
    
    /* INIZIALIZZAZIONE E GESTIONE URL */
    
    const form = document.getElementById("loginForm");
    const btnSubmit = document.getElementById("btnSubmit");
    const email = document.getElementById("email");
    const password = document.getElementById("password");

    const urlParams = new URLSearchParams(window.location.search);
    let urlCleanNeeded = false;

    if (urlParams.get("success") === "registrazione") {
        mostraNotifica("Registrazione completata con successo! Ora puoi accedere.");
        urlCleanNeeded = true;
    }
    if (urlParams.get("success") === "login") {
        mostraNotifica("Login effettuato con successo!");
        urlCleanNeeded = true;
    }

    /* Sfrutta l'History API nativa per effettuare il push di un nuovo stato all'URL corrente rimuovendo i parametri di query (es. ?success=login) in modo completamente trasparente, evitando un reload. */
    if (urlCleanNeeded) {
        const cleanUrl = window.location.pathname;
        window.history.replaceState({}, document.title, cleanUrl);
    }

    btnSubmit.disabled = true;

    /* CONFIGURAZIONE VALIDATORI E LISTENER */
    
    const validatori = {
        email: () => validaEmail(email, "emailError"),
        password: () => validaPassword(password, "passwordError")
    };

    const campi = [email, password];
    campi.forEach(campo => {
        campo.addEventListener("input", controllaFormInTempoReale);
        
        /* Delega la sanitizzazione della stringa (trim) all'evento blur per impedire che l'utente subisca alterazioni involontarie del cursore durante la fase attiva di digitazione. */
        campo.addEventListener("blur", () => {
            campo.value = campo.value.trim();
            controllaFormInTempoReale();
        });
    });

    /* FUNZIONI DI VALIDAZIONE E STATO FORM */
    
    function validaEmail(input, errorId) {
        const value = input.value.trim();
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        if (value === "") {
            nascondiErrore(errorId);
            return false;
        }
        if (!value.match(regex)) {
            mostraErrore(errorId, "Inserisci un indirizzo email valido.");
            return false;
        }
        nascondiErrore(errorId);
        return true;
    }

    function validaPassword(input, errorId) {
        const value = input.value.trim();
        if (value === "") {
            nascondiErrore(errorId);
            return false;
        }
        nascondiErrore(errorId);
        return true;
    }

    function controllaFormInTempoReale() {
        const formValido = Object.keys(validatori).every(key => validatori[key]());
        btnSubmit.disabled = !formValido;
    }

    /* UTILITY GRAFICHE DOM */
    
    function mostraErrore(id, messaggio) {
        const el = document.getElementById(id);
        if(el) {
            el.textContent = messaggio;
            el.classList.add("visible");
        }
    }

    function nascondiErrore(id) {
        const el = document.getElementById(id);
        if(el) { 
            el.classList.remove("visible"); 
        }
    }
});

/* SISTEMA NOTIFICHE GLOBALI (TOAST) */

function mostraNotifica(messaggio) {
    const toast = document.createElement("div");
    toast.className = "toast-notification";
    
    toast.style.borderLeft = "5px solid #4CAF50";
    toast.innerHTML = `<i class="fas fa-check-circle" style="color: #4CAF50; font-size: 24px;"></i> <span>${messaggio}</span>`;
    
    document.body.appendChild(toast);

    /* Richiede un ritardo minimo per consentire al browser di completare l'inserimento dell'elemento nel DOM (reflow) prima di innescare l'animazione di entrata associata alla classe CSS, prevenendo l'apparizione istantanea del pop-up senza transizione. */
    setTimeout(() => toast.classList.add("show"), 10);

    setTimeout(() => {
        toast.classList.remove("show");
        setTimeout(() => toast.remove(), 400); 
    }, 3500);
}