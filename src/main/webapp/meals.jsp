<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals</h2>
    <a href="meals?action=create<%--&startDate=${param.startDate}&endDate=${param.endDate}&startTime=${param.startTime}&endTime=${param.endTime}--%>">Add Meal</a>

    <form method="get" action="meals">
        <dl>
            <dt>StartDate:</dt>
            <dd><input type="date" value="${param.startDate}" name="startDate"></dd>
        </dl>
        <dl>
            <dt>EndDate:</dt>
            <dd><input type="date" value="${param.endDate}" name="endDate"></dd>
        </dl>
        <dl>
            <dt>StartTime:</dt>
            <dd><input type="time" value="${param.startTime}" name="startTime"></dd>
        </dl>
        <dl>
            <dt>EndTime:</dt>
            <dd><input type="time" value="${param.endTime}" name="endTime"></dd>
        </dl>
        <button type="submit">Отфильтровать</button>
    </form>

    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td>
                    <a href="meals?action=update&id=${meal.id}<%--&startDate=${param.startDate}&endDate=${param.endDate}&startTime=${param.startTime}&endTime=${param.endTime}--%>">Update</a>
                </td>
                <td>
                    <a href="meals?action=delete&id=${meal.id}&startDate=${param.startDate}&endDate=${param.endDate}&startTime=${param.startTime}&endTime=${param.endTime}">Delete</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>