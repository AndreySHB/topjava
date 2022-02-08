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
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
    </tr>
    <c:forEach items="${meals}" var="meal">
        <tr style="color: ${meal.excess ? "red" : "green"}">
            <td>${FORMATTER.format(meal.dateTime)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="meals?action=edit&mealId=${meal.id}">Update</a></td>
            <td><a href="meals?action=delete&mealId=${meal.id}">Delete</a></td>
        </tr>

    </c:forEach>
</table>
<p><a href="meals?action=add">Add Meal</a></p>
</body>
</html>

