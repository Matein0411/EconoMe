<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%-- Redirect de bienvenida a la vista de cuentas --%>
<%
    String context = request.getContextPath();
    response.sendRedirect(context + "/cuenta/VistaCuentas.jsp");
%>

