<%--
  Created by IntelliJ IDEA.
  User: aleksandrtaranov
  Date: 20.01.2022
  Time: 17:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.language}" scope="session"/>
<fmt:setBundle basename="resources"/>
<html>
<link rel="stylesheet" type="text/css" href="/style/navbar.css">
<nav class="navbar navbar-expand navbar-light bg-light" style="border-bottom: 1px solid #dee2e6;">
    <a class="navbar-brand" href="/">Meetings</a>
    <button class="navbar-toggler" id="navbar" type="button" data-toggle="collapse" data-target="#navbar-content"
            aria-controls="navbarSupportedContent" aria-expanded="true" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbar-content">
        <!-- PROFILE -->
        <ul class="navbar-nav mx-auto navbar-account">
            <c:if test="${sessionScope.user != null}">
                <li class="nav-item dropdown">
                    <a class="nav-link" style="color: #000 !important; text-align: center;"
                       data-toggle="dropdown" role="button"
                       aria-haspopup="true"
                       aria-expanded="false">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                             fill="currentColor" class="bi bi-person-circle" viewBox="0 0 16 16">
                            <path d="M13.468 12.37C12.758 11.226 11.195 10 8 10s-4.757 1.225-5.468 2.37A6.987 6.987 0 0 0 8 15a6.987 6.987 0 0 0 5.468-2.63z"/>
                            <path fill-rule="evenodd" d="M8 9a3 3 0 1 0 0-6 3 3 0 0 0 0 6z"/>
                            <path fill-rule="evenodd"
                                  d="M8 1a7 7 0 1 0 0 14A7 7 0 0 0 8 1zM0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8z"/>
                        </svg>
                            <%-- show user nickname if his authorized --%>
                        <span>${sessionScope.user.getLogin()} </span>
                    </a>
                    <div class="dropdown-menu">
                        <a class="dropdown-item" href="/account/${sessionScope.user.getId()}"><fmt:message key="profile.profileButton"/></a>
                        <c:if test="${sessionScope.user.getRole().name() == 'MODERATOR'}">
                            <a class="dropdown-item" href="/moderator/create-meeting"><fmt:message key="moderator.createMeeting"/></a>
                        </c:if>
                        <a class="dropdown-item" href="/logout"><fmt:message key="logout"/></a>
                    </div>
                </li>
            </c:if>
            <c:if test="${sessionScope.user == null}">
                <li class="nav-item">
                    <a class="nav-button btn btn-outline-dark" href="/login"><fmt:message key="signInMenu.signInButton"/></a>
                </li>
            </c:if>
        </ul>
        <!-- /PROFILE -->
        <c:if test="${requestScope['javax.servlet.forward.request_uri'] == '/'}">
            <%-- SEARCH FORM --%>
            <form class="form-inline my-2 my-lg-0 navbar-query-form" method="post" action="search-meeting">
                <input name="query" class="form-control mr-sm-2" type="search" placeholder="<fmt:message key="navbar.query.inputQuery"/>"
                       aria-label="Search">
                <c:if test="${queryIsNotValid != null}">
                    <div title="${queryIsNotValid}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                             class="bi bi-exclamation-octagon-fill" viewBox="0 0 16 16">
                            <path d="M11.46.146A.5.5 0 0 0 11.107 0H4.893a.5.5 0 0 0-.353.146L.146 4.54A.5.5 0 0 0 0 4.893v6.214a.5.5 0 0 0 .146.353l4.394 4.394a.5.5 0 0 0 .353.146h6.214a.5.5 0 0 0 .353-.146l4.394-4.394a.5.5 0 0 0 .146-.353V4.893a.5.5 0 0 0-.146-.353L11.46.146zM8 4c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995A.905.905 0 0 1 8 4zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>
                        </svg>
                    </div>
                </c:if>
                <button class="btn btn btn-outline-dark my-2 my-sm-0" type="submit"><fmt:message key="navbar.search"/></button>
            </form>
            <%-- /SEARCH FORM --%>

            <%-- SORT FORM --%>
            <form class="form-inline my-2 my-lg-0 navbar-soring-form" method="post" action="sort-meeting">
                <select class="custom-select" name="sortMethod" onchange='if(this.value != 0) { this.form.submit(); }'>
                    <option value="" selected disabled hidden><fmt:message key="navbar.sort.choose"/></option>
                    <c:choose>
                        <c:when test="${sortMethod == 'name'}">
                            <option value="name"><fmt:message key="navbar.sort.by_name"/> <fmt:message key="navbar.sort.reverseOrder"/></option>
                        </c:when>
                        <c:otherwise>
                            <option value="name"><fmt:message key="navbar.sort.by_name"/></option>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${sortMethod == 'date'}">
                            <option value="date"><fmt:message key="navbar.sort.by_date"/> <fmt:message key="navbar.sort.reverseOrder"/></option>
                        </c:when>
                        <c:otherwise>
                            <option value="date"><fmt:message key="navbar.sort.by_date"/></option>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${sortMethod == 'participants'}">
                            <option value="participants"><fmt:message key="navbar.sort.byNumberOfParticipants"/> <fmt:message key="navbar.sort.reverseOrder"/></option>
                        </c:when>
                        <c:otherwise>
                            <option value="participants"><fmt:message key="navbar.sort.byNumberOfParticipants"/></option>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${sortMethod == 'topics'}">
                            <option value="topics"><fmt:message key="navbar.sort.byNumberOfTopics"/> <fmt:message key="navbar.sort.reverseOrder"/></option>
                        </c:when>
                        <c:otherwise>
                            <option value="topics"><fmt:message key="navbar.sort.byNumberOfTopics"/></option>
                        </c:otherwise>
                    </c:choose>
                </select>
            </form>
            <%-- /SORT FORM --%>
            <%-- DISPLAYED MEETINGS --%>
            <button type="button" class="btn btn-outline-dark" data-toggle="modal"
                    data-target="#chooseWhichMeetingToShow">
                <c:if test="${option != null}">
                    ${option}
                </c:if>
                <c:if test="${option == null}">
                    <fmt:message key="navbar.show"/>
                </c:if>
            </button>
            <div class="modal fade" id="chooseWhichMeetingToShow" data-backdrop="static" data-keyboard="false" tabindex="-1"
                 aria-labelledby="staticBackdropLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable" role="document">
                    <div class="modal-content">
                        <form action="/show-meetings" method="post">
                            <div class="modal-body">
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="radioButton" value="all" id="defaultCheck1">
                                    <label class="form-check-label" for="defaultCheck1">
                                        <fmt:message key="navbar.show.allMeetings"/>
                                    </label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="radioButton" value="goingOnNow" id="defaultCheck2">
                                    <label class="form-check-label" for="defaultCheck2">
                                        <fmt:message key="navbar.show.ongoingMeetings"/>
                                    </label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="radioButton" value="passed" id="defaultCheck3">
                                    <label class="form-check-label" for="defaultCheck3">
                                        <fmt:message key="navbar.show.pastMeetings"/>
                                    </label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="radioButton" value="notStarted" id="defaultCheck4">
                                    <label class="form-check-label" for="defaultCheck4">
                                        <fmt:message key="navbar.show.futureMeetings"/>

                                    </label>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal"><fmt:message key="close"/></button>
                                <button type="submit" class="btn btn-outline-dark"><fmt:message key="select"/></button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <%-- /DISPLAYED MEETINGS --%>
        </c:if>
        <%-- CHANGE LOCALE --%>
        <form class="form-inline ml-2 my-2 my-lg-0" id="locale" name="locale" onchange="submit()" method="post" action="/change-locale">
            <select class="custom-select locale" name="locale">
                <option value="" selected disabled hidden><fmt:message key="navbar.currentLanguage"/></option>
                <c:forEach items="${applicationScope.locales}" var="locale">
                    <c:if test="${locale.key != sessionScope.language}">
                    <option value="${locale.key}">${locale.value}</option>
                    </c:if>
                </c:forEach>
            </select>
        </form>
        <%-- /CHANGE LOCALE --%>
    </div>
</nav>
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
