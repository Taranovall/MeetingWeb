<%--
  Created by IntelliJ IDEA.
  User: aleksandrtaranov
  Date: 09.02.2022
  Time: 16:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<%-- user cannot see this button --%>
<c:if test="${sessionScope.user.getRole().name() != 'USER' && sessionScope.user.getRole() != null}">
    <div class="text-center">
        <%-- visible only if meeting hasn't started --%>
        <c:if test="${!meeting.isStarted()}">
            <button type="button" class="btn btn-outline-dark mt-2" data-toggle="modal"
                    data-target="#TopicProposing">
                    <%-- This way see button's content speaker --%>
                <c:if test="${sessionScope.user.getRole().name() == 'SPEAKER'}">
                    <fmt:message key="speaker.proposeTopic"/>
                </c:if>
                    <%-- This way see button's content moderator --%>
                <c:if test="${sessionScope.user.getRole().name() == 'MODERATOR'}">
                    <fmt:message key="moderator.proposedTopics"/>
                </c:if>
            </button>
        </c:if>
            <%-- this button only for moderator to mark present users --%>
        <c:if test="${sessionScope.user.getRole().name() == 'MODERATOR' && meeting.isStarted()}">
            <button type="button" class="btn btn-outline-dark mt-2" data-toggle="modal"
                    data-target="#markUsers">
                <fmt:message key="moderator.markPresentUsers"/>
            </button>
        </c:if>
    </div>
    <div class="modal fade propose" id="TopicProposing" data-backdrop="static" data-keyboard="false" tabindex="-1"
         aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                    <%---------------------- This way see modal content speaker---------------------%>
                <c:if test="${sessionScope.user.getRole().name() == 'SPEAKER'}">
                <form action="/speaker/meeting/propose-topic" method="post">
                    <div class="modal-body">
                        <input type="text" name="topicName" placeholder="Name" class="form-control">
                    </div>
                    </c:if>
                        <%---------------------- This way see modal content moderator---------------------%>
                    <c:if test="${sessionScope.user.getRole().name() == 'MODERATOR'}">
                    <c:choose>
                        <c:when test="${proposedTopics.size() != 0}">
                            <div class="modal-body">
                                <div class="row">
                                    <table class="table table-hover mb-0">
                                        <thead>
                                        <tr>
                                            <th scope="col"><fmt:message key="meeting.speaker"/></th>
                                            <th scope="col"><fmt:message key="meeting.topic"/></th>
                                            <th></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${proposedTopics}" var="map">
                                            <tr>
                                                <td class="align-middle">${map.getValue().getLogin()}</td>
                                                <td class="align-middle">${map.getKey().getName()}</td>
                                                <td>
                                                    <div class="row">
                                                            <%-- Accept speaker's topic --%>
                                                        <form action="/moderator/meeting/accept-proposition" method="post">
                                                                <%-- Input contains speaker ID value --%>
                                                            <input type="text" hidden value="${map.getValue().getId()}"
                                                                   name="speakerId">
                                                            <button class="btn btn-outline-dark mr-2" name="application"
                                                                    value="${map.getKey().getId()}" type="submit">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16" fill="currentColor"
                                                                     class="bi bi-check" viewBox="0 0 16 16">
                                                                    <path d="M10.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.267.267 0 0 1 .02-.022z"/>
                                                                </svg>
                                                            </button>
                                                        </form>
                                                            <%-- Cancel speaker's topic --%>
                                                        <form action="/moderator/meeting/cancel-proposition" method="post">
                                                                <%-- Input contains speaker ID value --%>
                                                            <input type="text" hidden value="${map.getValue().getId()}"
                                                                   name="speakerId">
                                                            <button class="btn btn-outline-dark" name="application"
                                                                    value="${map.getKey().getId()}" type="sumbit">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16" fill="currentColor" class="bi bi-x"
                                                                     viewBox="0 0 16 16">
                                                                    <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                                                                </svg>
                                                            </button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-center mt-2">
                                <fmt:message key="meeting.noProposedTopics"/>
                            </p>
                        </c:otherwise>
                    </c:choose>
                    </c:if>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal"><fmt:message key="close"/>
                        </button>
                            <%-- Button for speaker to submit the form --%>
                        <c:if test="${sessionScope.user.getRole().name() == 'SPEAKER'}">
                        <button class="btn btn-outline-dark" type="submit"><fmt:message key="submit"/></button>
                </form>
                </c:if>
            </div>
        </div>
    </div>
    </div>
</c:if>
</html>
