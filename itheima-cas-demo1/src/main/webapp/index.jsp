<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
用户名：<%=request.getRemoteUser()%>
<a href="http://192.168.25.133:8981/cas/logout?service=http://www.baidu.com">退出登录</a>
</body>
</html>
