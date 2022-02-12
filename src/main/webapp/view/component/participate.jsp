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
<%-- Button visible only for authorized and not authorized users --%>
<c:if test="${sessionScope.user.getRole().name() == 'USER' || sessionScope.user.getRole() == null}">
    <div class="text-center">
        <c:if test="${sessionScope.user.getRole().name() == 'USER'}">
            <c:choose>
                <c:when test="${!participating}">
                    <form action="participate" method="post">
                        <button type="submit" name="userId" value="${sessionScope.user.getId()}" class="btn btn-outline-dark mt-2">Participate</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <form action="stop-participating" method="post">
                        <button type="submit" name="userId" value="${sessionScope.user.getId()}" class="btn btn-outline-dark mt-2">Stop participating</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${sessionScope.user.getRole() == null}">
            <button disabled class="btn btn-outline-dark mt-2">Sign in to participate</button>
        </c:if>
    </div>
</c:if>
</html>
