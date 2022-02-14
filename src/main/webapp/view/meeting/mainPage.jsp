<%--
  Created by IntelliJ IDEA.
  User: aleksandrtaranov
  Date: 20.01.2022
  Time: 17:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/style/meetings.css">
</head>
<body>
<jsp:include page="../component/navbar.jsp"></jsp:include>
<div id="meetingList">
    <div class="row">
        <c:forEach items="${meetings}" var="m">
            <div class="col-xs-12 col-sm-6 col-md-3">
                <a class="card meeting" href="/meeting/${m.getId()}">
                    <div class="imageWrap image">
                        <div class="card-img-top">
                            <div class="amount amount-user" title="Количество участников">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                                     class="bi bi-people-fill" viewBox="0 0 16 16">
                                    <path d="M7 14s-1 0-1-1 1-4 5-4 5 3 5 4-1 1-1 1H7zm4-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6z"/>
                                    <path fill-rule="evenodd"
                                          d="M5.216 14A2.238 2.238 0 0 1 5 13c0-1.355.68-2.75 1.936-3.72A6.325 6.325 0 0 0 5 9c-4 0-5 3-5 4s1 1 1 1h4.216z"/>
                                    <path d="M4.5 8a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z"/>
                                </svg>
                                <span>${m.getParticipants().size() + m.getSpeakerTopics().size()}</span>
                            </div>
                            <div class="amount amount-topic" title="Количество тем">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-pen" viewBox="0 0 16 16">
                                    <path d="m13.498.795.149-.149a1.207 1.207 0 1 1 1.707 1.708l-.149.148a1.5 1.5 0 0 1-.059 2.059L4.854 14.854a.5.5 0 0 1-.233.131l-4 1a.5.5 0 0 1-.606-.606l1-4a.5.5 0 0 1 .131-.232l9.642-9.642a.5.5 0 0 0-.642.056L6.854 4.854a.5.5 0 1 1-.708-.708L9.44.854A1.5 1.5 0 0 1 11.5.796a1.5 1.5 0 0 1 1.998-.001zm-.644.766a.5.5 0 0 0-.707 0L1.95 11.756l-.764 3.057 3.057-.764L14.44 3.854a.5.5 0 0 0 0-.708l-1.585-1.585z"/>
                                </svg>
                                <span>${m.getFreeTopics().size() + m.getSpeakerTopics().size()}</span>
                            </div>
                            <c:if test="${m.isGoingOnNow()}">
                                <div class="going-on-now" title="Количество тем">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-cast" viewBox="0 0 16 16">
                                        <path d="m7.646 9.354-3.792 3.792a.5.5 0 0 0 .353.854h7.586a.5.5 0 0 0 .354-.854L8.354 9.354a.5.5 0 0 0-.708 0z"/>
                                        <path d="M11.414 11H14.5a.5.5 0 0 0 .5-.5v-7a.5.5 0 0 0-.5-.5h-13a.5.5 0 0 0-.5.5v7a.5.5 0 0 0 .5.5h3.086l-1 1H1.5A1.5 1.5 0 0 1 0 10.5v-7A1.5 1.5 0 0 1 1.5 2h13A1.5 1.5 0 0 1 16 3.5v7a1.5 1.5 0 0 1-1.5 1.5h-2.086l-1-1z"/>
                                    </svg>
                                    <span>Going on now</span>
                                </div>
                            </c:if>
                        <img src="${m.getPhotoPath()}"/>
                        </div>
                    </div>
                    <p class="name">${m.getName()}</p>
                    <ul class="info list-group">
                        <li class="list-group-item"><small>Date: ${m.getDate()}</small>
                        </li>
                        <li class="list-group-item"><small>Time: ${m.getTimeStart()} - ${m.getTimeEnd()}</small>
                        </li>
                        <li class="list-group-item"><small>Place: ${m.getPlace()}</small>
                        </li>
                    </ul>
                </a>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>
