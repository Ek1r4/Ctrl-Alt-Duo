<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accesso Negato - ReFrame</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            padding: 20px;
            box-sizing: border-box;
        }
        .error-container {
            background-color: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            text-align: center;
            max-width: 450px;
            width: 100%;
            /* Una riga rossa in alto per enfatizzare l'errore */
            border-top: 6px solid #e74c3c; 
        }
        .icon {
            font-size: 60px;
            margin-bottom: 10px;
        }
        h1 {
            color: #333;
            margin-top: 0;
            margin-bottom: 15px;
            font-size: 24px;
        }
        p {
            color: #666;
            margin-bottom: 30px;
            line-height: 1.5;
        }
        .btn-home {
            display: inline-block;
            padding: 12px 25px;
            background-color: #2c3e50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
            font-size: 16px;
            transition: background-color 0.3s;
        }
        .btn-home:hover {
            background-color: #1a252f;
        }
    </style>
</head>
<body>

    <div class="error-container">
        <div class="icon">🚫</div>
        <h1>Accesso Negato</h1>
        <p>Spiacenti, non hai le autorizzazioni necessarie per visualizzare questa pagina. L'area richiesta è riservata agli amministratori di ReFrame.</p>
        
        <a href="${pageContext.request.contextPath}/jsp/index.jsp" class="btn-home">Torna alla Home</a>
    </div>

</body>
</html>