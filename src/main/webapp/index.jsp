<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Homepage</title>
    <script type="text/javascript">
	    window.MY_APP_CONTEXT = "${pageContext.request.contextPath}";
	</script>
	<script src="${pageContext.request.contextPath}/js/home.js" defer></script>
	<script src="${pageContext.request.contextPath}/js/home-deals.js" defer></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/gsap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/ScrollTrigger.min.js"></script>
    <script src="https://unpkg.com/@studio-freight/lenis@1.0.42/dist/lenis.min.js"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
</head>
<body class="page-home">

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
            <jsp:include page="/WEB-INF/components/home-story.jsp" />
        </div>
    </div>

    <main class="home-container">
        <jsp:include page="/WEB-INF/components/home-goals.jsp" />
      
        <div class="card-div">
            <jsp:include page="/WEB-INF/components/home-deals.jsp" />
        </div>  
    </main>

    <jsp:include page="/WEB-INF/components/footer.jsp" />
     
</body>
</html>