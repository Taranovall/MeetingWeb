<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>${requestScope.user.getLogin()}</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/static/profile.css">
</head>
<body>
<jsp:include page="component/navbar.jsp"></jsp:include>
<div class="mx-3 pt-3">
    <div class="profile row border rounded mx-5">
        <div class="col-xs-12 col-sm-4">
            <img src="https://via.placeholder.com/200x400" class="img-thumbnail col"/>
            <div class="info col">
                <ul class="info list-group">
                    <li class="list-group-item"><small>Login: ${requestScope.user.getLogin()}</small>
                    </li>
                    <li class="list-group-item"><small>User ID: ${requestScope.user.getId()}</small>
                    </li>
                    <li class="list-group-item"><small>Role: ${requestScope.user.getRole().toString()}</small>
                    </li>
                    <li class="list-group-item"><small>Registration
                        date: ${requestScope.user.getRegistrationDate()}</small></li>
                </ul>
            </div>
        </div>
        <div class="col-xs-12 col-sm-8 data">

            <nav>
                <div class="nav nav-tabs" id="nav-tab" role="tablist">
                    <a class="nav-item nav-link active" id="nav-home-tab" data-toggle="tab" href="#nav-involved"
                       role="tab" aria-controls="nav-home" aria-selected="true">Involved in</a>
                    <%-- those nav item visible only if user opens his own account --%>
                    <c:if test="${UserOwnAccount}">
                        <a class="nav-item nav-link" id="nav-profile-tab" data-toggle="tab" href="#nav-notifications"
                           role="tab" aria-controls="nav-profile" aria-selected="false">Notifications</a>
                        <a class="nav-item nav-link" id="nav-contact-tab" data-toggle="tab" href="#nav-edit" role="tab"
                           aria-controls="nav-contact" aria-selected="false">Edit</a>
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
                                        <p class="mb-0 text-center"><small>Name: ${m.getName()}</small></p>
                                        <p class="mb-0 text-center"><small>Date: ${m.getDate()}</small>
                                        </p>
                                        <p class="mb-0 text-center"><small>Time: ${m.getTime()}</small>
                                        </p>
                                        <p class="mb-0 text-center"><small>Place: ${m.getPlace()}</small></p>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </c:if>

                </div>
                <%-- those nav item visible only if user opens his own account --%>
                <c:if test="${UserOwnAccount}">
                <div class="tab-pane fade" id="nav-notifications" role="tabpanel"
                     aria-labelledby="nav-notifications-tab">...
                </div>
                    <div class="tab-pane fade" id="nav-edit" role="tabpanel" aria-labelledby="nav-edit-tab">...</div>
                </c:if>
            </div>
        </div>
    </div>
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