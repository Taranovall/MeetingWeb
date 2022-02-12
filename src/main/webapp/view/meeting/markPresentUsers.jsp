<%--
  Created by IntelliJ IDEA.
  User: aleksandrtaranov
  Date: 12.02.2022
  Time: 19:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Mark present users - ${meeting.getName()}</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<jsp:include page="../component/navbar.jsp"></jsp:include>
<div class="container mt-5 mb-5 text-center">
    <form action="/meeting/moderator/mark-present-users/${meeting.getId()}" method="post">
 <c:forEach items="${meeting.getParticipants()}" var="p">
     <div class="form-check">
         <input class="form-check-input" type="checkbox" value="${p.getId()}" id="defaultCheck1" name="presentUserId">
         <label class="form-check-label" for="defaultCheck1">
             ${p.getLogin()}
         </label>
     </div>
 </c:forEach>
        <button class="btn btn-outline-dark" type="submit">Submit</button>
    </form>
</div>
</body>
</html>
