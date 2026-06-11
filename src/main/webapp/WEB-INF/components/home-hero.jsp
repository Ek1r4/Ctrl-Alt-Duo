<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<section class="hero-section">
    <div class="hero-frame-overlay" style="background-image: url('${pageContext.request.contextPath}/assets/old_frame.png');"></div>

    <div class="hero-3d-background">
        <div class="canvas-container">
            <canvas id="minoltaCanvas" width="1920" height="1080"></canvas>
        </div>
    </div>

    <div class="hero-content">
        <h1 class="hero-title">SCOPRI LA TUA<br>PROSSIMA STORIA</h1>
        <p class="hero-subtitle">Boutique online per la cultura dell'immagine.<br>Macchine fotografiche rigenerate e garantite.</p>
        
        <div class="hero-actions">
            <a href="${pageContext.request.contextPath}/vetrina.jsp" class="btn-cta">Esplora lo Shop</a>
        </div>
    </div>
</section>

<script>
    const canvas = document.getElementById('minoltaCanvas');
    const context = canvas.getContext('2d');
    const frameCount = 250; // Modifica in base al numero esatto dei tuoi frame
    
    const currentFrame = index => {
        let paddedIndex = index.toString().padStart(4, '0');
        return `<%= request.getContextPath() %>/assets/render_scroll_minolta/frame_${paddedIndex}.png`;
    };

    const images = [];
    const firstImg = new Image();
    firstImg.src = currentFrame(1);
    firstImg.onload = () => { context.drawImage(firstImg, 0, 0); };

    for (let i = 1; i <= frameCount; i++) {
        const img = new Image();
        img.src = currentFrame(i);
        images.push(img);
    }

    window.addEventListener('scroll', () => {
        const scrollTop = document.documentElement.scrollTop;
        const maxScrollTop = document.documentElement.scrollHeight - window.innerHeight;
        const scrollFraction = scrollTop / maxScrollTop;
        const frameIndex = Math.min(frameCount - 1, Math.ceil(scrollFraction * frameCount));

        requestAnimationFrame(() => {
            if (images[frameIndex] && images[frameIndex].complete) {
                context.clearRect(0, 0, canvas.width, canvas.height);
                context.drawImage(images[frameIndex], 0, 0);
            }
        });
    });
</script>