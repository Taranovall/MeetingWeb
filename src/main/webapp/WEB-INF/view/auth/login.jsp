<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Authorization</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <form action="login" method="post" class="auth-menu">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" >Авторизация</h5>
                </div>
                <div class="modal-body text-center" style="padding-bottom: 0">
                    <!-- ОБРАБОТКА ОШИБОК -->
                    <c:if test="${passwordError}"><p class="error alert alert-danger" role="alert">${message}</p></c:if>
                    <c:if test="${loginError}"><p class="error alert alert-danger" role="alert">${message}</p></c:if>
                    <!-- /ОБРАБОТКА ОШИБОК -->
                    <input type="text" name="login" placeholder="Логин" class="form-control mb-2">
                    <input type="password" name="password" placeholder="Пароль" class="form-control mb-2">
                    <button type="submit" class="btn btn-outline-dark mb-2">Авторизироваться</button>
                </div>
                <div class="modal-footer justify-content-center">
                    <a class="btn" href="registration"><u>Ещё нет аккаунта? Зарегестрируйтесь</u></a>
                </div>
            </div>
        </div>
    </form>
    <br>
</div>
</body>
</html>
