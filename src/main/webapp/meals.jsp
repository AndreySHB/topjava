<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="ru">
<head>
    <title>Meals</title>
</head>
<style>
    TABLE {
        border-collapse: collapse; /* Отображать двойные линии как одинарные */
        border: 3px solid gray; /* Рамка вокруг таблицы */
    }

    TD, TH {
        padding: 4px; /*Поля вокруг содержимого ячеек */
        border: 3px solid gray; /* Рамка вокруг ячеек */
    }

</style>
<body>
<h3><a href="index.html">Home</a></h3>
<h2>Meals</h2>
<table>
    <tr>
        <th><c:out value="Date"/></th>
        <th><c:out value="Description"/></th>
        <th><c:out value="Calories"/></th>
    </tr>
    <c:forEach items="${requestScope.dishes}" var="dish">
        <c:if test="${dish.excess}">
            <tr style="color: red">
                <td><c:out value="${requestScope.DTF.format(dish.dateTime)}"/></td>
                <td><c:out value="${dish.description}"/></td>
                <td><c:out value="${dish.calories}"/></td>
            </tr>
        </c:if>
        <c:if test="${!dish.excess}">
            <tr style="color: green">
                <td><c:out value="${requestScope.DTF.format(dish.dateTime)}"/></td>
                <td><c:out value="${dish.description}"/></td>
                <td><c:out value="${dish.calories}"/></td>
            </tr>
        </c:if>
    </c:forEach>
</table>
</body>
</html>

