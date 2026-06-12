<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Homepage</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">

</head>
<body>
<jsp:include page="/WEB-INF/components/preloader.jsp" />
    <jsp:include page="/WEB-INF/components/header.jsp" />

    <main class="home-container">
        <jsp:include page="/WEB-INF/components/home-hero.jsp" />
        </main>
        <div id="camera-sequence-container">
    <canvas id="camera-canvas"></canvas>
</div>

<div class="scroll-container">
    
    <div class="story-div">
        <h1>spero che tutto il creato esploda nel modo più violento</h1>
        </div>
    
</div>
<main class="home-container">
        <jsp:include page="/WEB-INF/components/home-goals.jsp" />
      
         <div class="card-div">
        <h1>spero che tutto il creato esploda nel modo più violento</h1>
        </div>  </main>
        <jsp:include page="/WEB-INF/components/footer.jsp" />



        <script type="text/javascript">
        document.addEventListener("DOMContentLoaded", () => {
            const canvas = document.getElementById("camera-canvas");
            const context = canvas.getContext("2d");

            // CONFIGURAZIONE
            const frameCount = 190;
            // Sostituisci con il path reale. Modifica la funzione padStart se i tuoi file 
            // si chiamano 1.png, 2.png (senza zeri iniziali) rimuovendo .padStart(4, '0')
    		const currentFrame = index => {
			    const fileNumber = index - 1;
			    const path = "${pageContext.request.contextPath}/assets/Minolta_OnScroll/frame_" + fileNumber.toString().padStart(4, '0') + ".webp";
			    console.log("Generato path:", path); // <--- TI DICE ESATTAMENTE COSA STA CERCANDO
			    return path;
			};
            // Imposta la risoluzione reale del canvas (usa le dimensioni in pixel di un tuo frame originale)
            // Questo garantisce che l'immagine non sgrani, indipendentemente dal CSS
            canvas.width = 1920; 
            canvas.height = 1080;

            // 1. CARICAMENTO DEL PRIMO FRAME E PRELOADER
            const img = new Image();
            img.src = currentFrame(1);
            
            // Disegna il primo frame appena l'immagine è caricata
            img.onload = () => {
                context.drawImage(img, 0, 0);
            };

            // Pre-carica in memoria tutte le altre immagini per evitare scatti durante lo scroll
            const preloadImages = () => {
                for (let i = 1; i < frameCount; i++) {
                    const preloadImg = new Image();
                    preloadImg.src = currentFrame(i);
                }
            };
            preloadImages();

         // 2. GESTIONE DELLO SCROLL
            window.addEventListener("scroll", () => {
                const scrollTop = document.documentElement.scrollTop;
                
                // LA MAGIA E' QUI: Definisci quanti pixel di scroll servono per completare l'animazione.
                // In questo caso, usiamo 3 volte l'altezza della finestra (deve corrispondere al div fittizio che hai creato).
                // Aumenta il '3' per farla andare più lenta, diminuiscilo per renderla più veloce.
                const scrollDistance = window.innerHeight * 4; 
                
                // Calcola la percentuale basata sulla nostra distanza fissa.
                // Usiamo Math.min(..., 1) per assicurarci che il calcolo si fermi al 100% 
                // anche se l'utente continua a scrollare oltre.
                const scrollFraction = Math.min(scrollTop / scrollDistance, 1);

                // Mappa la percentuale al numero di frame
                const frameIndex = Math.min(
                    frameCount - 1,
                    Math.floor(scrollFraction * frameCount)
                );
                
                // Usa requestAnimationFrame per aggiornare il canvas
                requestAnimationFrame(() => updateImage(frameIndex + 1));
            });

            // 3. FUNZIONE DI AGGIORNAMENTO CANVAS
            const updateImage = index => {
                const updateImg = new Image();
                updateImg.src = currentFrame(index);
                updateImg.onload = () => {
                    // Pulisce il canvas prima di disegnare il nuovo frame (utile per le trasparenze)
                    context.clearRect(0, 0, canvas.width, canvas.height);
                    context.drawImage(updateImg, 0, 0);
                };
            };
        });
        </script>
        
</body>
</html>