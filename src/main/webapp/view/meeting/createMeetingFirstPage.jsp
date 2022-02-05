<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create meeting</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/static/createMeeting.css">
</head>
<body>
<div class="container mt-5 mb-5 text-center">
    <h1>Create new meeting</h1><br>
    <form action="create-meeting" method="get" class="add">
        <div class="field">
            <input type="text" name="name" placeholder="Meeting's name" class="form-control">
        </div>
        <div class="field">
        <div class="input-group field">
            <input type="date" name="date" class="form-control" placeholder="Select date">
            <input type="time" name="time" class="form-control" placeholder="Select time">
        </div>
        </div>
        <div class="field">
            <input type="text" name="place" placeholder="Place" class="form-control">
        </div>
        <div class="field">
            <input type="text" name="countOfTopics" placeholder="Count of topics" class="form-control">
        </div>
        <button type="submit" class="btn btn-outline-dark">Next</button>
    </form>
</div>
</body>
</html>
