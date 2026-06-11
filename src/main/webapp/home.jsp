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
    <jsp:include page="/WEB-INF/components/header.jsp" />

    <main class="home-container">
        <jsp:include page="/WEB-INF/components/home-hero.jsp" />
        </main>
</body>
</html>