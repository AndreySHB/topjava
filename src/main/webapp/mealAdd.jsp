<%@ page contentType="text/html;charset=UTF-8" %>
<head>
    <title>Edit Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h3><a href="meals">Meals</a></h3>
<h2>Add Meal</h2>
<form method="POST" action='meals' name="addMeal">

    Date-Time
    <label>
        <input
                type="datetime-local" name="datetime"
        >
    </label>

    Description
    <label>
        <input
                type="text" name="description"
        >
    </label>

    Calories
    <label>
        <input
                type="number" name="calories"
        >
    </label>
    <input
            type="submit" value="submit"/>
</form>
</body>
