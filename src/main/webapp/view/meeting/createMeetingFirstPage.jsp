<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create meeting</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/style/createMeeting.css">
</head>
<body>
<jsp:include page="../component/navbar.jsp"></jsp:include>
<div class="container mt-5 mb-5 text-center">
    <h1>Create new meeting</h1><br>
    <form action="create-meeting" method="get" class="add">
        <c:if test="${error != null}">
            <div class="alert alert-danger text-center" role="alert">
                    ${error}
            </div>
        </c:if>
        <%-- meeting's name --%>
        <div class="field">
            <input type="text" name="name" placeholder="Meeting's name" class="form-control">
        </div>
        <%-- date when meeting starts --%>
        <div class="field">
            <input type="date" name="date" class="form-control" placeholder="Select date">
        </div>
        <%-- time --%>
        <div class="field">
            <div class="input-group field mt-0">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="inputStartTime">Meeting's start time</span>
                    </div>
                    <input type="time" name="startTime" class="form-control"
                           placeholder="Select start time of the meeting" aria-describedby="inputStartTime">
                    <div class="input-group-prepend ml-2">
                        <span class="input-group-text" id="basic-inputEndTime">Meeting's end time</span>
                    </div>
                    <input type="time" name="endTime" class="form-control" placeholder="Select end time of the meeting"
                           aria-describedby="inputEndTime">
            </div>
        </div>
        <%-- place --%>
        <div class="field">
            <input type="text" name="place" placeholder="Place" class="form-control">
        </div>
        <%-- count of topics --%>
        <div class="field">
            <input type="text" name="countOfTopics" placeholder="Count of topics" class="form-control">
        </div>
        <button type="submit" class="btn btn-outline-dark">Next</button>
    </form>
</div>
</body>
</html>
