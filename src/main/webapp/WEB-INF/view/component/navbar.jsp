<%--
  Created by IntelliJ IDEA.
  User: aleksandrtaranov
  Date: 20.01.2022
  Time: 17:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link rel="stylesheet" type="text/css" href="/static/navbar.css">
<nav class="navbar navbar-expand navbar-light bg-light" style="border-bottom: 1px solid #dee2e6;">
    <a class="navbar-brand" href="/">Meetings</a>
    <button class="navbar-toggler" id="navbar" type="button" data-toggle="collapse" data-target="#navbar-content"
            aria-controls="navbarSupportedContent" aria-expanded="true" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbar-content">
        <!-- PROFILE -->
        <ul class="navbar-nav mx-auto navbar-account">
            <c:if test="${user != null}">
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
                        <span>${user.getLogin()} </span>
                    </a>
                    <div class="dropdown-menu">

                    </div>
                </li>
            </c:if>
            <c:if test="${user == null}">
                <li class="nav-item">
                    <a class="nav-button btn btn-outline-dark" href="/login">Sign in</a>
                </li>
            </c:if>
        </ul>
        <!-- /PROFILE -->
        <!-- SEARCH FORM -->
        <form class="form-inline my-2 my-lg-0 navbar-query-form" method="post" action="search-meeting">
            <input name="query" class="form-control mr-sm-2" type="search" placeholder="Input query"
                   aria-label="Search">
            <c:if test="${queryIsNotValid != null}">
                <div title="${queryIsNotValid}">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                         class="bi bi-exclamation-octagon-fill" viewBox="0 0 16 16">
                        <path d="M11.46.146A.5.5 0 0 0 11.107 0H4.893a.5.5 0 0 0-.353.146L.146 4.54A.5.5 0 0 0 0 4.893v6.214a.5.5 0 0 0 .146.353l4.394 4.394a.5.5 0 0 0 .353.146h6.214a.5.5 0 0 0 .353-.146l4.394-4.394a.5.5 0 0 0 .146-.353V4.893a.5.5 0 0 0-.146-.353L11.46.146zM8 4c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995A.905.905 0 0 1 8 4zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>
                    </svg>
                </div>
            </c:if>
            <button class="btn btn btn-outline-dark my-2 my-sm-0" type="submit">Search</button>
        </form>
        <!-- /SEARCH FORM -->
        <!-- SORT FORM -->
        <form class="form-inline my-2 my-lg-0 navbar-soring-form" method="post" action="sort-meeting">
            <select class="custom-select" name="sortMethod" onchange='if(this.value != 0) { this.form.submit(); }'>
                <option value="" selected disabled hidden>Choose sorting method</option>
                <c:choose>
                    <c:when test="${sortMethod == 'name'}">
                        <option value="name">By name (reverse order)</option>
                    </c:when>
                    <c:otherwise>
                        <option value="name">By name</option>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${sortMethod == 'date'}">
                        <option value="date">By date (reverse order)</option>
                    </c:when>
                    <c:otherwise>
                        <option value="date">By date</option>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${sortMethod == 'participants'}">
                        <option value="participants">By number of participants (reverse order)</option>
                    </c:when>
                    <c:otherwise>
                        <option value="participants">By number of participants</option>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${sortMethod == 'topics'}">
                        <option value="topics">By number of topics (reverse order)</option>
                    </c:when>
                    <c:otherwise>
                        <option value="topics">By number of topics</option>
                    </c:otherwise>
                </c:choose>
            </select>
        </form>
        <!-- /SORT FORM -->
    </div>
</nav>
</html>
