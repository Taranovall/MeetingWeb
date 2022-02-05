<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>${requestScope.user.getLogin()}</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/static/meetingInfo.css">
</head>
<body>
<jsp:include page="component/navbar.jsp"></jsp:include>
<div class="px-3 pt-3">

    <div class="col-xs-12 col-sm-8">
        <p class="text-center"><span class="border-bottom">${requestScope.user.getLogin()}</span></p>


    </div>
</div>
</body>
</html>