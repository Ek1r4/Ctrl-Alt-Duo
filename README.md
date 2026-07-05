# 📸 ReFrame - L'E-commerce "Analog-Chic"

Benvenuto nella repository di **ReFrame**, il progetto su cui abbiamo sudato (come team **Ctrl-Alt-Duo**) per unire il fascino senza tempo delle macchine fotografiche vintage con lo sviluppo web moderno. 

L'obiettivo fin dall'inizio era creare qualcosa che non fosse il solito shop pre-confezionato o un semplice esercizio di stile. Volevamo un'estetica curata, quasi da boutique, spingendo il più possibile sulle interazioni UI e sull'esperienza utente, senza rinunciare a un'architettura backend solida.

## ✨ Cosa c'è di figo qui dentro?

Più che un semplice sito, abbiamo provato a costruire un'esperienza. Abbiamo sbattuto un bel po' la testa per implementare alcune feature tra cui:
* **Visualizzatore 3D integrato:** Abbiamo inserito modelli 3D navigabili direttamente nelle pagine web (es. date un occhio ai file `.glb` per farvi un'idea), così l'utente può esplorare le macchine fotografiche a 360 gradi.
* **Animazioni allo scroll:** Tutta la sequenza di frame della *Minolta* (la trovate tra gli assets) si anima fluidamente man mano che l'utente scrolla la pagina.

## 🛠 Tech Stack

Il progetto è strutturato per girare come un orologio, seguendo un'architettura basata sul pattern MVC:
* **Backend:** Java (Servlets + JSP). Niente astrazioni magiche, solo sano controllo sul ciclo di vita di request e response.
* **Database:** MySQL (JDBC) per la gestione relazionale di utenti, ordini e inventario.
* **Frontend:** HTML5, CSS3, e JavaScript vanilla (con diverse logiche asincrone per la gestione in tempo reale del carrello e dei filtri).
* **Ambiente:** Progetto nativo Eclipse, pronto per il deploy su server web Apache Tomcat.

## 🚀 Funzionalità Sotto il Cofano

Oltre alla vetrina "Analog-Chic", l'applicativo gestisce un bel po' di logica di business:
* **Autenticazione e Sicurezza:** Registrazione, login, gestione del profilo utente e hashing delle password.
* **Core E-commerce:** Carrello dinamico, flusso di checkout completo, pagamenti e generazione automatica delle fatture.
* **Pannello Admin:** Una dashboard riservata per gestire il catalogo prodotti, tracciare gli ordini in corso e monitorare le spedizioni.
* **Customer Care:** Un sistema di Ticketing (pratiche di assistenza) e una live chat integrata.
* **Feedback System:** Sistema di recensioni integrato per ogni modello.

---
*Sviluppato dal team **Ctrl-Alt-Duo**, fatene buon uso.* 🚀
