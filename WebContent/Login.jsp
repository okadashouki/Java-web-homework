<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>BankLogin</title>
	<script type="text/javascript">
		
	</script>
</head>
<body>

	<c:if test="${not empty requestScope.loginMsg}">
		系統回應：<p style="color:blue;">${requestScope.loginMsg}</p>
	</c:if>
	<form action="LoginAction.do" method="post">
		<input type="hidden" name="action" value="login"/>
	    ID:<input type="text" name="id"/> <br/><br/>
	    PWD:<input type="password" name="pwd"/> <br/><br/>
	    <input type="submit"/>
	</form>
</body>
</html>