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
<div class="px-3 pt-3">
    <div class="row">
        <div class="col-xs-12 col-sm-4">
            <img src="${meeting.getPhotoPath()}" class="img-thumbnail"/>
        </div>
        <div class="col-xs-12 col-sm-8">
            <p class="text-center"><span class="border-bottom">${meeting.getName()}</span></p>
            <table class="table table-hover text-center">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Topic</th>
                    <th>Speaker</th>
                </tr>
                </thead>
                <c:if test="${meeting.getSpeakerTopics().size() > 0}">
                    <!-- topics with speakers -->
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
                    <!-- topics without speaker -->
                    <c:forEach items="${meeting.getFreeTopics()}" var="freeTopic">
                        <tr>
                            <td class="align-middle"><span class="count"></span></td>
                            <td class="align-middle">${freeTopic.getName()}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${sessionScope.user.getRoles().toString().contains('SPEAKER')}">
                                        <c:choose>
                                            <c:when test="${applicationList != null && applicationList.indexOf(freeTopic.getId().toString()) != -1}">
                                                <form action="remove-application" method="post">
                                                    <button name="application" class="become-speaker"
                                                            value="${freeTopic.getId()}" type="submit">Application Submitted
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <form action="apply-application" method="post">
                                                    <button name="application" class="become-speaker"
                                                            value="${freeTopic.getId()}" type="submit">Become speaker
                                                    </button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <span>Doesn't have speaker yet</span>
                                    </c:otherwise>
                                </c:choose>
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
