<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>${meeting.getName()}</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/style/meetingInfo.css">
</head>
<body>
<jsp:include page="../component/navbar.jsp"></jsp:include>
<c:if test="${error != null}">
    <div class="alert alert-danger text-center" role="alert">
            ${error}
    </div>
</c:if>
<div class="px-3 pt-3">
    <div class="row">
        <div class="col-xs-12 col-sm-4">
            <div class="img">
                <img src="${meeting.getPhotoPath()}" class="img-thumbnail"/>
                <%--------------- EDIT BUTTON ---------------%>
                <c:if test="${sessionScope.user.getRole().name() == 'MODERATOR' && !meeting.isStarted()}">
                    <button type="button" class="edit-button" data-toggle="modal"
                            data-target="#editInfo"><fmt:message key="meeting.edit"/>
                    </button>
                </c:if>
            </div>
            <%--------------- EDIT BUTTON CONTENT ---------------%>
            <div class="modal fade" id="editInfo" data-backdrop="static" data-keyboard="false"
                 tabindex="-1"
                 aria-labelledby="staticBackdropLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <form action="/moderator/meeting/edit-meeting" method="post">
                            <div class="modal-body edit-modal-content">
                                <input type="time" name="meetingStartTime" class="form-control"
                                       value="${meeting.getTimeStart()}"
                                       placeholder="Select start time of the meeting"
                                       aria-describedby="inputStartTime">
                                <input type="time" name="meetingEndTime" class="form-control"
                                       value="${meeting.getTimeEnd()}"
                                       placeholder="Select end time of the meeting"
                                       aria-describedby="inputEndTime">
                                <input type="date" name="meetingDate" placeholder="Name" value="${meeting.getDate()}"
                                       class="form-control">
                                <input type="text" name="meetingPlace" placeholder="Name" value="${meeting.getPlace()}"
                                       class="form-control">
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close
                                </button>
                                <button class="btn btn-primary" type="submit">Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <%--------------- /EDIT BUTTON CONTENT ---------------%>
            <ul class="list-group pt-2">
                <li class="list-group-item"><small><fmt:message key="meeting.name"/>: ${meeting.getName()}</small>
                </li>
                <li class="list-group-item"><small><fmt:message key="meeting.date"/>: ${meeting.getDate()}</small>
                </li>
                <li class="list-group-item"><small><fmt:message key="meeting.time"/>: ${meeting.getTimeStart()} - ${meeting.getTimeEnd()}</small>
                </li>
                <li class="list-group-item"><small><fmt:message key="meeting.place"/>: ${meeting.getPlace()}</small>
                </li>
            </ul>
            <%--for speaker and moderator--%>
            <jsp:include page="../component/propose.jsp"></jsp:include>
            <%-- only for moderator --%>
            <jsp:include page="../component/markPresentUsers.jsp"></jsp:include>
            <%-- attendance percentage visible only for moderator if meeting is already started--%>
            <c:if test="${sessionScope.user.getRole().name() == 'MODERATOR' && meeting.getPercentageAttendance() != 0 && meeting.isStarted()}">
                <div class="percentage text-center"><fmt:message key="meeting.attendance"/>
                    <div class="progress">
                        <div class="progress-bar" role="progressbar"
                             style="width: ${meeting.getPercentageAttendance()}%;"
                             aria-valuenow="${meeting.getPercentageAttendance()}"
                             aria-valuemin="0" aria-valuemax="100">${meeting.getPercentageAttendance()}%
                        </div>
                    </div>
                </div>
            </c:if>
            <%-- only for user --%>
            <jsp:include page="../component/participate.jsp"></jsp:include>

        </div>
        <div class="col-xs-12 col-sm-8">
            <div class="text-center mb-2">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                     class="bi bi-people-fill" viewBox="0 0 16 16">
                    <path d="M7 14s-1 0-1-1 1-4 5-4 5 3 5 4-1 1-1 1H7zm4-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6z"/>
                    <path fill-rule="evenodd"
                          d="M5.216 14A2.238 2.238 0 0 1 5 13c0-1.355.68-2.75 1.936-3.72A6.325 6.325 0 0 0 5 9c-4 0-5 3-5 4s1 1 1 1h4.216z"/>
                    <path d="M4.5 8a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z"/>
                </svg>
                <span class="mr-2"><fmt:message key="meeting.participants"/>: ${meeting.getParticipants().size()}</span>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                     class="bi bi-megaphone-fill" viewBox="0 0 16 16">
                    <path d="M13 2.5a1.5 1.5 0 0 1 3 0v11a1.5 1.5 0 0 1-3 0v-11zm-1 .724c-2.067.95-4.539 1.481-7 1.656v6.237a25.222 25.222 0 0 1 1.088.085c2.053.204 4.038.668 5.912 1.56V3.224zm-8 7.841V4.934c-.68.027-1.399.043-2.008.053A2.02 2.02 0 0 0 0 7v2c0 1.106.896 1.996 1.994 2.009a68.14 68.14 0 0 1 .496.008 64 64 0 0 1 1.51.048zm1.39 1.081c.285.021.569.047.85.078l.253 1.69a1 1 0 0 1-.983 1.187h-.548a1 1 0 0 1-.916-.599l-1.314-2.48a65.81 65.81 0 0 1 1.692.064c.327.017.65.037.966.06z"/>
                </svg>
                <span><fmt:message key="meeting.speakers"/>: ${meeting.getSpeakerTopics().size()}</span>
            </div>
            <table class="table table-hover text-center">
                <thead>
                <tr>
                    <th>#</th>
                    <th><fmt:message key="meeting.topic"/></th>
                    <th><fmt:message key="meeting.speaker"/></th>
                </tr>
                </thead>
                <c:if test="${meeting.getSpeakerTopics().size() > 0}">
                    <%-- Topics with speaker --%>
                    <c:forEach items="${meeting.getSpeakerTopics().entrySet()}" var="entrySet">
                        <c:forEach items="${entrySet.getValue()}" var="topic">
                            <tr>
                                <td><span class="count"></span></td>
                                <td>${topic.getName()}</td>
                                <td><a href="/account/${entrySet.getKey().getId()}"
                                       class="href">${entrySet.getKey().getLogin()}</a></td>
                            </tr>
                        </c:forEach>
                    </c:forEach>
                </c:if>
                <c:if test="${meeting.getFreeTopics().size() > 0}">
                    <%-- Topics without speaker --%>
                    <c:forEach items="${meeting.getFreeTopics()}" var="freeTopic">
                        <tr>
                            <td class="align-middle"><span class="count"></span></td>
                            <td class="align-middle">${freeTopic.getName()}</td>
                            <td>
                                    <%------------------------ THIS WAY SPEAKER SEES ROWS WITHOUT SPEAKER ------------------------%>
                                <c:if test="${sessionScope.user.getRole().name() == 'SPEAKER'}">
                                    <c:choose>
                                        <c:when test="${sentApplicationList != null && sentApplicationList.indexOf(freeTopic.getId().toString()) != -1}">
                                            <form action="/speaker/meeting/remove-application" method="post">
                                                <button name="application" class="become-speaker"
                                                        value="${freeTopic.getId()}" type="submit"><fmt:message key="speaker.applicationSubmitted"/>
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${receivedApplicationList != null && receivedApplicationList.indexOf(freeTopic.getId().toString()) != -1}">
                                                    <div class="row response">
                                                        <div class="become-speaker justify-content-center"><fmt:message key="speaker.invite"/>
                                                        </div>
                                                        <form action="/speaker/meeting/accept-invitation" method="post">
                                                            <button class="yes" name="application"
                                                                    value="${freeTopic.getId()}" type="submit">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16" fill="currentColor"
                                                                     class="bi bi-check" viewBox="0 0 16 16">
                                                                    <path d="M10.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.267.267 0 0 1 .02-.022z"/>
                                                                </svg>
                                                            </button>
                                                        </form>
                                                        <form action="/speaker/meeting/cancel-invitation" method="post">
                                                            <button class="no" name="application"
                                                                    value="${freeTopic.getId()}" type="sumbit">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16" fill="currentColor" class="bi bi-x"
                                                                     viewBox="0 0 16 16">
                                                                    <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                                                                </svg>
                                                            </button>
                                                        </form>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <%-- if meeting is started button is disabled --%>
                                                    <form action="/speaker/meeting/apply-application" method="post">
                                                        <c:if test="${!meeting.isStarted()}">
                                                            <button name="application" class="become-speaker"
                                                                    value="${freeTopic.getId()}" type="submit"><fmt:message key="speaker.send_application"/>
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${meeting.isStarted()}">
                                                            <button disabled class="become-speaker"><fmt:message key="speaker.send_application"/>
                                                            </button>
                                                        </c:if>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                                    <%------------------------ THIS WAY MODERATOR SEES ROWS WITHOUT SPEAKER ------------------------%>
                                <c:if test="${sessionScope.user.getRole().name() == 'MODERATOR'}">
                                    <c:choose>
                                        <c:when test="${sentApplicationsBySpeaker.get(freeTopic).size() > 0}">
                                            <form action="/moderator/meeting/accept-application" method="post"
                                                  class="accept-application row">
                                                <select class="custom-select" id="inputGroupSelect"
                                                        name="speakerId">
                                                    <option selected value="none" }><fmt:message key="moderator.chooseSpeaker"/>
                                                    </option>
                                                    <c:forEach items="${sentApplicationsBySpeaker}"
                                                               var="sentApplicationMap">
                                                        <c:if test="${freeTopic.getId() == sentApplicationMap.getKey().getId()}">
                                                            <c:forEach
                                                                    items="${sentApplicationMap.getValue()}"
                                                                    var="speaker">
                                                                <option value="${speaker.getId()}">${speaker.getLogin()}</option>
                                                            </c:forEach>
                                                        </c:if>
                                                    </c:forEach>
                                                </select>
                                                <c:if test="${!meeting.isStarted()}">
                                                <button name="application" class="yes"
                                                        value="${freeTopic.getId()}" type="submit">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                         height="16" fill="currentColor"
                                                         class="bi bi-check" viewBox="0 0 16 16">
                                                        <path d="M10.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.267.267 0 0 1 .02-.022z"/>
                                                    </svg>
                                                </button>
                                                </c:if>
                                                <c:if test="${meeting.isStarted()}">
                                                    <button disabled class="yes">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                             height="16" fill="currentColor"
                                                             class="bi bi-check" viewBox="0 0 16 16">
                                                            <path d="M10.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.267.267 0 0 1 .02-.022z"/>
                                                        </svg>
                                                    </button>
                                                </c:if>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <span><fmt:message key="moderator.topicWithoutApplication"/></span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                                    <%------------------------ THIS WAY USER SEES ROWS WITHOUT SPEAKER ------------------------%>
                                <c:if test="${sessionScope.user.getRole().name() == 'USER' || sessionScope.user.getRole() == null}">
                                    <span><fmt:message key="user.topicWithoutSpeaker"/></span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
