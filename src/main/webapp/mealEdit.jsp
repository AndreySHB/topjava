<%@ page contentType="text/html;charset=UTF-8" %>
<head>
    <title>Edit Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h3><a href="meals">Meals</a></h3>
<h2>Edit Meal</h2>
<form method="POST" action='meals' name="editMeal">
    <table>
        <tr>
            <td>
                Id
                <label>
                    <input
                            type="number" name="id"
                            value=${meal.id}>
                </label>
            </td>
        </tr>
        <tr>
            <td>
                Date-Time
                <label>
                    <input
                            type="datetime-local" name="datetime"
                            value=${meal.dateTime}>
                </label>
            </td>
        </tr>
        <tr>
            <td>
                Description
                <label>
                    <input
                            type="text" name="description"
                            value="${meal.description}">
                </label>
            </td>
        </tr>
        <tr>
            <td>
                Calories
                <label>
                    <input
                            type="number" name="calories"
                            value=${meal.calories}>
                </label>
                <input
                        type="submit" value="Accept"/>
            </td>
        </tr>
    </table>

</form>
</body>
