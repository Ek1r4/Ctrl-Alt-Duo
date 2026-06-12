<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/preloader.css">
<div id="reframe-loader">
    <div class="loader-content">
        <div class="viewfinder">
            <span class="bracket top-left"></span>
            <span class="bracket top-right"></span>
            <span class="bracket bottom-left"></span>
            <span class="bracket bottom-right"></span>
            
            <h1 class="loader-logo">REFRAME</h1>
        </div>
        <p class="loader-text">Messa a fuoco in corso...</p>
    </div>
    <script type="text/javascript">
    window.addEventListener("load", () => {
        const loader = document.getElementById("reframe-loader");
        
        if (loader) {
            // Aggiungiamo un piccolissimo delay di mezzo secondo. 
            // Dà al browser il tempo di stabilizzare il canvas e permette 
            // all'utente di godersi l'effetto visivo del mirino.
            setTimeout(() => {
                loader.classList.add("loaded");
                
                // Opzionale ma consigliato per le performance: 
                // Elimina fisicamente il loader dal DOM dopo che è scomparso
                setTimeout(() => {
                    loader.remove();
                }, 800); // 800ms corrisponde alla durata della transition nel CSS
                
            }, 2000);
        }
    });
    </script>
</div>