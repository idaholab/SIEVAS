<%-- 
    Document   : login
    Created on : Jun 30, 2016, 9:36:24 AM
    Author     : monejh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Spring Security Example </title>
    </head>
    <body>
        <c:if test="${param.error != null}">
        <div>
            Invalid username and password.
        </div>
        </c:if>
        <c:if test="${param.logout != null}">
        <div>
            You have been logged out.
        </div>
        </c:if>
        <form method="POST">
            <div><label> User Name : <input type="text" name="username"/> </label></div>
            <div><label> Password: <input type="password"  name="password"/> </label></div>
            <div><input type="submit" value="Sign In"/></div>
            <input type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
        </form>
    </body>
</html>