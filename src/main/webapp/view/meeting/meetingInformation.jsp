<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>${meeting.getName()}</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/static/meetingInfo.css">
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
                <c:if test="${sessionScope.user.getRole().name() == 'MODERATOR'}">
                    <button type="button" class="edit-button" data-toggle="modal"
                            data-target="#editInfo">Edit
                    </button>
                </c:if>
            </div>
            <%--------------- EDIT BUTTON CONTENT ---------------%>
            <div class="modal fade" id="editInfo" data-backdrop="static" data-keyboard="false"
                 tabindex="-1"
                 aria-labelledby="staticBackdropLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <form action="edit-meeting" method="post">
                            <div class="modal-body edit-modal-content">
                                <input type="time" name="meetingTime" placeholder="Name" value="${meeting.getTime()}"
                                       class="form-control">
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
                <li class="list-group-item"><small>Name: ${meeting.getName()}</small>
                </li>
                <li class="list-group-item"><small>Date: ${meeting.getDate()}</small>
                </li>
                <li class="list-group-item"><small>Time: ${meeting.getTime()}</small>
                </li>
                <li class="list-group-item"><small>Place: ${meeting.getPlace()}</small>
                </li>
            </ul>
            <jsp:include page="../component/propose.jsp"></jsp:include>
        </div>
        <div class="col-xs-12 col-sm-8">
            <table class="table table-hover text-center">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Topic</th>
                    <th>Speaker</th>
                </tr>
                </thead>
                <c:if test="${meeting.getSpeakerTopics().size() > 0}">
                    <%-- Topics with speaker --%>
                    <c:forEach items="${meeting.getSpeakerTopics().entrySet()}" var="entrySet">
                        <c:forEach items="${entrySet.getValue()}" var="topic">
                            <tr>
                                <td><span class="count"></span></td>
                                <td>${topic.getName()}</td>
                                <td>${entrySet.getKey().getLogin()}</td>
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
                                            <form action="remove-application" method="post">
                                                <button name="application" class="become-speaker"
                                                        value="${freeTopic.getId()}" type="submit">Application
                                                    Submitted
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${receivedApplicationList != null && receivedApplicationList.indexOf(freeTopic.getId().toString()) != -1}">
                                                    <div class="row response">
                                                        <div class="become-speaker justify-content-center">You've
                                                            been invited to be a speaker
                                                        </div>
                                                        <form action="accept-invitation" method="post">
                                                            <button class="yes" name="application"
                                                                    value="${freeTopic.getId()}" type="submit">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16" fill="currentColor"
                                                                     class="bi bi-check" viewBox="0 0 16 16">
                                                                    <path d="M10.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.267.267 0 0 1 .02-.022z"/>
                                                                </svg>
                                                            </button>
                                                        </form>
                                                        <form action="cancel-invitation" method="post">
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
                                                    <form action="apply-application" method="post">
                                                        <button name="application" class="become-speaker"
                                                                value="${freeTopic.getId()}" type="submit">Become
                                                            speaker
                                                        </button>
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
                                            <form action="accept-application" method="post"
                                                  class="accept-application row">
                                                <select class="custom-select" id="inputGroupSelect"
                                                        name="speakerId">
                                                    <option selected value="none" }>Choose speaker for this
                                                        topic
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
                                                <button name="application" class="yes"
                                                        value="${freeTopic.getId()}" type="submit">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                         height="16" fill="currentColor"
                                                         class="bi bi-check" viewBox="0 0 16 16">
                                                        <path d="M10.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.267.267 0 0 1 .02-.022z"/>
                                                    </svg>
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <span>Doesn't have application yet</span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                                    <%------------------------ THIS WAY USER SEES ROWS WITHOUT SPEAKER ------------------------%>
                                <c:if test="${sessionScope.user.getRole().name() == 'USER'}">
                                    <span>Doesn't have speaker yet</span>
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
