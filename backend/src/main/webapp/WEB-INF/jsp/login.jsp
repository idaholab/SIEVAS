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
    <title>Scientific & Intelligence Exascale Visualization Analysis System (SIEVAS) Login</title>
    <link rel="stylesheet" href="<c:url value='/resources/angular2/login/node_modules/bootstrap/dist/css/bootstrap.min.css'></c:url>">
    <script src="<c:url value='/resources/angular2/login/node_modules/core-js/client/shim.min.js'></c:url>"></script>
    <script src="<c:url value='/resources/angular2/login/node_modules/zone.js/dist/zone.js'></c:url>"></script>
    <script src="<c:url value='/resources/angular2/login/node_modules/reflect-metadata/Reflect.js'></c:url>"></script>
    <script src="<c:url value='/resources/angular2/login/node_modules/systemjs/dist/system.src.js'></c:url>"></script>
    <!-- 2. Configure SystemJS -->
    <script src="<c:url value='/resources/angular2/login/systemjs.config.js'></c:url>"></script>
    <script>
      System.import('app').catch(function(err){ console.error(err); });
    </script>
    <style>
        .ng-valid[required] {
          border-left: 5px solid #42A948; /* green */
        }

        .ng-invalid {
          border-left: 5px solid #a94442; /* red */
    }
    </style>
</head>
<body>

    
    <my-app>Loading...</my-app>
</body>
</html>