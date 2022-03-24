<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><fmt:message key="moderator.meeting_creating"/></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/style/createMeeting.css">
</head>
<body>
<jsp:include page="../component/navbar.jsp"></jsp:include>
<div class="container mt-5 mb-5 text-center">
    <h1><fmt:message key="moderator.meeting_creating"/></h1><br>
    <form action="/moderator/create-meeting" method="post" class="add" enctype="multipart/form-data">
        <c:if test="${error != null}">
            <div class="alert alert-danger text-center" role="alert">
                    ${error}
            </div>
        </c:if>
        <c:forEach var="i" begin="1" end="${countOfTopics}">
            <div class="field">
                <div class="input-group mb-3">
                    <input type="text" name="topicName" placeholder="<fmt:message key="meeting.topic"/>" class="form-control">
                    <select class="custom-select" id="inputGroupSelect" name="speakerName">
                        <option selected value="none" }><fmt:message key="meeting.do_not_invite"/></option>
                        <c:forEach items="${speakers}" var="s">
                            <option value="${s.getLogin()}">${s.getLogin()}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </c:forEach>
        <div class="field">
            <div class="custom-file">
                <input id="customFile" type="text" placeholder="sdfsdf" name="photo" class="custom-file-input" accept="image/*" onfocus="(this.type='file')">
                <label class="custom-file-label" for="customFile" ><fmt:message key="meeting.choose_img"/></label>
            </div>
        </div>
        <button type="submit" class="btn btn-outline-dark"><fmt:message key="submit"/></button>
    </form>
</div>
</body>
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"
        integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"
        integrity="sha384-9/reFTGAW83EW2RDu2S0VKaIzap3H66lZH81PoYlFhbGU+6BZp6G7niu735Sk7lN"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.min.js"
        integrity="sha384-w1Q4orYjBQndcko6MimVbzY0tgp4pWB4lZ7lr30WKz0vr/aWKhXdBNmNb5D92v7s"
        crossorigin="anonymous"></script>
</html>
