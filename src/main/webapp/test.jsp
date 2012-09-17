<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<title>test jsp</title>
<body>
	hello jsp.
	<hr/>
	<c:set var="x" value="111"/>
	x===${x}<c:out value="${x}"/>
	<p>
		<c:forEach var="i" begin="1" end="10" step="1"> 
      		${i},
    	</c:forEach><p> 
	</p>
</body>
</html>