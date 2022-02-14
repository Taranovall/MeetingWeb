<%--
  Created by IntelliJ IDEA.
  User: aleksandrtaranov
  Date: 09.02.2022
  Time: 16:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<%--content is visible only for moderator--%>
<c:if test="${sessionScope.user.getRole().name() == 'MODERATOR'}">
    <%-- the trigger button is located in propose.jsp--%>
    <%-- modal--%>
    <div class="modal fade" id="markUsers" data-backdrop="static" data-keyboard="false" tabindex="-1"
         aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable" role="document">
            <div class="modal-content">
                <form action="/meeting/moderator/mark-present-users/${meeting.getId()}" method="post">
                    <div class="modal-body">
                        <c:choose>
                            <c:when test="${meeting.getParticipants().size() > 0}">
                                <c:forEach items="${meeting.getParticipants()}" var="p">
                                    <div class="form-check">
                                        <c:if test="${presentUser.contains(p.getId())}">
                                            <input class="form-check-input" type="checkbox" value="${p.getId()}"
                                                   id="defaultCheck1"
                                                   name="presentUserId" checked>
                                        </c:if>
                                        <c:if test="${!presentUser.contains(p.getId())}">
                                            <input class="form-check-input" type="checkbox" value="${p.getId()}"
                                                   id="defaultCheck1"
                                                   name="presentUserId">
                                        </c:if>
                                        <label class="form-check-label" for="defaultCheck1">
                                                ${p.getLogin()}
                                        </label>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <p class="text-center mt-2">
                                    There's no participants
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-outline-dark">Submit</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>
</html>