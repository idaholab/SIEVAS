<%-- 
    Document   : home
    Created on : Jun 30, 2016, 9:39:20 AM
    Author     : monejh
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Scientific & Intelligence Exascale Visualization Analysis System (SIEVAS)</title>
    <link rel="stylesheet" href="<c:url value='/resources/angular2/login/menu_app/node_modules/bootstrap/dist/css/bootstrap.min.css'></c:url>">
    <script src="<c:url value='/resources/angular2/login/node_modules/core-js/client/shim.min.js'></c:url>"></script>
    <script src="<c:url value='/resources/angular2/login/node_modules/zone.js/dist/zone.js'></c:url>"></script>
    <script src="<c:url value='/resources/angular2/login/node_modules/reflect-metadata/Reflect.js'></c:url>"></script>
    <script src="<c:url value='/resources/angular2/login/node_modules/systemjs/dist/system.src.js'></c:url>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value='/resources/angular2/login/node_modules/primeui/themes/omega/theme.css'></c:url>" />
    <link rel="stylesheet" type="text/css" href="<c:url value='/resources/angular2/login/node_modules/primeui/primeui-ng-all.min.css'></c:url>" />
    <link rel="stylesheet" type="text/css" href="<c:url value='/resources/fontawesome/css/font-awesome.min.css'></c:url>" />
    <!-- 2. Configure SystemJS -->
    <script src="<c:url value='/resources/angular2/login/systemjs.config_menu.js'></c:url>"></script>
    <script>
      System.import('menu_app').catch(function(err){ console.error(err); });
    </script>
    <style>
        .ng-valid[required] {
          border-left: 5px solid #42A948; /* green */
        }

        .ng-invalid {
          border-left: 5px solid #a94442; /* red */
    }
        .ui-growl-message p { word-break:break-all;}
    </style>
    <!--<script>document.write('<base href="' + document.location + '" />');</script>-->
    <base href="/"/>
</head>
<body>

    <my-app>Loading...</my-app>
</body>
</html>
