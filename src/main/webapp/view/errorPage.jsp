<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="ru">
<head>
    <title>HTTP Status 500 – Internal Server Error</title>
    <link rel="stylesheet" type="text/css" href="/style/errorPage.css">
</head>
<body>
<h1>HTTP Status 500 – Internal Server Error</h1>
<hr class="line"/>
<p><b>Type</b> ${requestScope.type}</p>
<p><b>Message</b> ${requestScope.message}</p>
<p><b>Description</b> The server encountered an unexpected condition that prevented it from fulfilling the request.</p>
<hr class="line"/>
</body>
</html>