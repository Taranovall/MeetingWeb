<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><fmt:message key="moderator.meetingCreating"/></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/style/createMeeting.css">
</head>
<body>
<jsp:include page="../component/navbar.jsp"></jsp:include>
<div class="container mt-5 mb-5 text-center">
    <h1><fmt:message key="moderator.meetingCreating"/></h1><br>
    <form action="/moderator/create-meeting" method="get" class="add">
        <c:if test="${error != null}">
            <div class="alert alert-danger text-center" role="alert">
                    ${error}
            </div>
        </c:if>
        <%-- meeting's name --%>
        <div class="field">
            <input type="text" name="name" placeholder="<fmt:message key="meeting.name"/>" class="form-control">
        </div>
        <%-- date when meeting starts --%>
        <div class="field">
            <input type="text" name="date" class="form-control" placeholder="<fmt:message key="meeting.datePlaceholder"/>"
                   onfocus="(this.type='date')">
        </div>
        <%-- time --%>
        <div class="field">
            <div class="input-group field mt-0">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="inputStartTime"><fmt:message key="meeting.startTime"/></span>
                    </div>
                    <input type="time" name="startTime" class="form-control" aria-describedby="inputStartTime">
                    <div class="input-group-prepend ml-2">
                        <span class="input-group-text" id="basic-inputEndTime"><fmt:message key="meeting.endTime"/></span>
                    </div>
                    <input type="time" name="endTime" class="form-control" aria-describedby="inputEndTime">
            </div>
        </div>
        <%-- place --%>
        <div class="field">
            <input type="text" name="place" placeholder="<fmt:message key="meeting.place"/>" class="form-control">
        </div>
        <%-- count of topics --%>
        <div class="field">
            <input type="text" name="countOfTopics" placeholder="<fmt:message key="meeting.countOfTopics"/>" class="form-control">
        </div>
        <button type="submit" class="btn btn-outline-dark"><fmt:message key="meeting.next"/></button>
    </form>
</div>
</body>
</html>
