<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.zapstitch.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">


<title></title>
</head>
<body>




	<%
		if (null != request.getParameter("code")) {
	%>
	<p>Successfully Logged into our App using Gmail Credentials.
	<p>Generating a report may take some time depending on the number
		of email messages in the user's email account in last 3 months.</p>

	<button
		onclick="location.href = 'http://localhost:8080/Zapstitch/report.jsp?code=<%=request.getParameter("code")%>';">Click
		to generate Report</button>

	<%
		} else {
	%>

	<p>Gmail Login unsuccessful. Click here to try log in again.</p>
	<%
		}
	%>





</body>
</html>