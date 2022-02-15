<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Users</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Users</h2>
<form method="get" action="users">
    <dl>
        <dt>Select User:</dt>
        <dd><input type="number"  name="userId" required></dd>
    </dl>
    <button type="submit">Select</button>
</form>
</body>
</html>