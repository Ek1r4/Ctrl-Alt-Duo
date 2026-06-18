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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
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
            <h1>spero che tutto il creato esploda nel modo più violento</h1>
        </div>
    </div>

    <main class="home-container">
        <jsp:include page="/WEB-INF/components/home-goals.jsp" />
      
        <div class="card-div">
            <h1>spero che tutto il creato esploda nel modo più violento</h1>
        </div>  
    </main>

    <jsp:include page="/WEB-INF/components/footer.jsp" />
     
</body>
</html>