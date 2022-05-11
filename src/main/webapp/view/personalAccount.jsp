<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>${requestScope.user.getLogin()}</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/style/profile.css">
</head>
<body>
<jsp:include page="component/navbar.jsp"></jsp:include>
<c:if test="${error != null}">
    <div class="alert alert-danger text-center" role="alert">
            ${error}
    </div>
</c:if>
<div class="mx-3 pt-3">
    <div class="profile row border rounded mx-5">
        <div class="col-xs-12 col-sm-4">
            <img src="https://via.placeholder.com/200x400" class="img-thumbnail col"/>
            <div class="info col">
                <ul class="info list-group">
                    <li class="list-group-item"><small><fmt:message key="profile.login"/>: ${requestScope.user.getLogin()}</small>
                    </li>
                    <li class="list-group-item"><small><fmt:message key="profile.userId"/>: ${requestScope.user.getId()}</small>
                    </li>
                    <li class="list-group-item"><small><fmt:message key="profile.role"/>: ${requestScope.user.getRole().toString()}</small>
                    </li>
                    <li class="list-group-item"><small><fmt:message key="profile.registration_date"/>: ${requestScope.user.getRegistrationDate()}</small></li>
                    <c:if test="${requestScope.user.getEmail() != null}">
                        <li class="list-group-item"><small><fmt:message key="profile.email"/>: ${requestScope.user.getEmail()}</small></li>
                    </c:if>
                </ul>
            </div>
        </div>
        <div class="col-xs-12 col-sm-8 data">

            <nav>
                <div class="nav nav-tabs" id="nav-tab" role="tablist">
                    <%-- this nav item visible only if user has role speaker --%>
                    <c:if test="${requestScope.user.getRole().name() == 'SPEAKER'}">
                    <a class="nav-item nav-link active" id="nav-home-tab" data-toggle="tab" href="#nav-involved"
                       role="tab" aria-controls="nav-home" aria-selected="true"><fmt:message key="profile.meetings"/></a>
                    </c:if>
                    <%-- those nav items visible only if user opens his own account --%>
                    <c:if test="${UserOwnAccount}">
                        <a class="nav-item nav-link" data-toggle="tab" href="#nav-notifications"
                           role="tab" aria-controls="nav-notifications-tab" aria-selected="false"><fmt:message key="profile.notifications"/></a>
                        <a class="nav-item nav-link" data-toggle="tab" href="#nav-edit" role="tab"
                           aria-controls="nav-edit-tab" aria-selected="false"><fmt:message key="profile.settings"/></a>
                    </c:if>
                </div>
            </nav>
            <div class="tab-content" id="nav-tabContent">
                <div class="tab-pane fade show active info-tab scroll" id="nav-involved" role="tabpanel"
                     aria-labelledby="nav-invitations-tab">
                    <c:if test="${meetingsSpeakerIsInvolvedIn.size() > 0}">
                        <c:forEach items="${meetingsSpeakerIsInvolvedIn}" var="m">
                            <a href="/meeting/${m.getId()}" class="row profile-meeting">
                                <div class="col-sm-3">
                                    <img src="${m.getPhotoPath()}" class="img-thumbnail"/>
                                </div>
                                <div class="col-sm-9">
                                    <div class="align-middle">
                                        <p class="mb-0 text-center"><small><fmt:message key="meeting.name"/>: ${m.getName()}</small></p>
                                        <p class="mb-0 text-center"><small><fmt:message key="meeting.date"/>: ${m.getDate()}</small>
                                        </p>
                                        <p class="mb-0 text-center"><small><fmt:message key="meeting.time"/>: ${m.getTimeStart()}
                                            - ${m.getTimeEnd()}</small>
                                        </p>
                                        <p class="mb-0 text-center"><small><fmt:message key="meeting.place"/>: ${m.getPlace()}</small></p>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </c:if>
                </div>
                <%-- those nav items visible only if user opens his own account --%>
                <c:if test="${UserOwnAccount}">
                    <div class="tab-pane fade" id="nav-notifications" role="tabpanel"
                         aria-labelledby="nav-notifications-tab">...
                    </div>
                    <div class="tab-pane fade" id="nav-edit" role="tabpanel" aria-labelledby="nav-edit-tab">
                        <c:if test="${sessionScope.user.getEmail() == null}">
                            <form class="form-inline pt-2" action="set-email" method="post">
                                <div class="form-group mx-sm-3 mb-2">
                                    <label for="input-mail" class="sr-only">Email</label>
                                    <input type="email" class="form-control" id="input-mail" placeholder="Email"
                                           name="email">
                                </div>
                                <button type="submit" class="btn btn-primary mb-2"><fmt:message key="profile.confirm_email"/></button>
                            </form>
                        </c:if>
                        <c:if test="${sessionScope.user.getEmail() != null}">
                            <div class="form-group mx-sm-3 mb-2 pt-2">
                                <input readonly type="email" value="${sessionScope.user.getEmail()}"
                                       class="form-control" placeholder="Email" name="email">
                            </div>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>
</body>
</html>