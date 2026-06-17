document.addEventListener("DOMContentLoaded", () => {
        const canvas = document.getElementById("camera-canvas");
        const context = canvas.getContext("2d");

        // Configurazione e risoluzione Canvas
        const frameCount = 190;
        canvas.width = 1920;
        canvas.height = 1080;
        
        const currentFrame = index => {
            const fileNumber = index - 1;
            const path = window.MY_APP_CONTEXT + "/assets/Minolta_OnScroll/frame_" + fileNumber.toString().padStart(4, '0') + ".webp";
            return path;
        };

        // 1. Caricamento primo frame e Preload immagini
        const img = new Image();
        img.src = currentFrame(1);
        img.onload = () => {
            context.drawImage(img, 0, 0);
        };

        const preloadImages = () => {
            for (let i = 1; i < frameCount; i++) {
                const preloadImg = new Image();
                preloadImg.src = currentFrame(i);
            }
        };
        preloadImages();

        const canvasContainer = document.getElementById("camera-sequence-container");
        const scrollContainer = document.querySelector(".scroll-container");

        // 2. Gestione evento Scroll
        window.addEventListener("scroll", () => {
            const isMobile = window.innerWidth <= 1024;
            let scrollFraction = 0;
            
            if (isMobile) {
                const scrollHeight = scrollContainer.offsetHeight || 0;
                const winHeight = window.innerHeight || 0;
                const rectTop = scrollContainer.getBoundingClientRect().top || 0;
                
                const scrollableDistance = scrollHeight - winHeight;
                const scrolledInside = -rectTop;
                const offset = 300; 

                if (scrolledInside < -offset) {
                    scrollFraction = 0;
                    canvasContainer.style.transform = 'translateX(100vw)';
                    canvasContainer.style.opacity = '0'; 
                } else if (scrolledInside > scrollableDistance + offset) {
                    scrollFraction = 1;
                    canvasContainer.style.transform = 'translateX(-100vw)';
                    canvasContainer.style.opacity = '0'; 
                } else {
                    canvasContainer.style.opacity = '1';
                    const totalDistance = scrollableDistance + (offset * 2);
                    const currentScroll = scrolledInside + offset;

                    if (totalDistance > 0) {
                        scrollFraction = Math.max(0, Math.min(currentScroll / totalDistance, 1));
                    } else {
                        scrollFraction = 0;
                    }
                    
                    // Logica a 3 Fasi Mobile
                    const p1End = 50 / frameCount;
                    const p2End = 120 / frameCount;
                    let xPos = 0;

                    if (scrollFraction <= p1End) {
                        let localProgress = scrollFraction / p1End;
                        xPos = 100 - (localProgress * 100);
                    } else if (scrollFraction <= p2End) {
                        xPos = 0;
                    } else {
                        let localProgress = (scrollFraction - p2End) / (1 - p2End);
                        xPos = -(localProgress * 100);
                    }
                    
                    canvasContainer.style.transform = 'translateX(' + xPos + 'vw)';
                }
            } else {
                // Logica Desktop
                canvasContainer.style.opacity = '1';
                canvasContainer.style.transform = 'translateX(0)'; 
                
                const scrollTop = document.documentElement.scrollTop;
                const scrollDistance = window.innerHeight * 4; 
                scrollFraction = Math.min(scrollTop / scrollDistance, 1);
            }

            const frameIndex = Math.min(
                frameCount - 1,
                Math.floor(scrollFraction * frameCount)
            );

            requestAnimationFrame(() => updateImage(frameIndex + 1));
        });
        
        // 3. Funzione aggiornamento Canvas
        const updateImage = index => {
            const updateImg = new Image();
            updateImg.src = currentFrame(index);
            updateImg.onload = () => {
                context.clearRect(0, 0, canvas.width, canvas.height);
                context.drawImage(updateImg, 0, 0);
            };
        };
    });