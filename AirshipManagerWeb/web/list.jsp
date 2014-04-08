<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <body>

        <table border="1">
            <thead>
                <tr>
                    <th>názov</th>
                    <th>kapacita</th>
                    <th>cena</th>
                </tr>
            </thead>
            <c:forEach items="${airships}" var="airship">
                <tr>
                    <td><c:out value="${airship.name}"/></td>
                    <td><c:out value="${airship.capacity}"/></td>
                    <td><c:out value="${airship.pricePerDay}"/></td>
                    <td><form method="post" action="${pageContext.request.contextPath}/airships/delete?id=${airship.id}"
                              style="margin-bottom: 0;"><input type="submit" value="Zmazať"></form></td>
<!--                    <td><form method="post" action="${pageContext.request.contextPath}/airships/update?id=${airship.id}"
                              style="margin-bottom: 0;"><input type="submit" value="Upraviť"></form></td>-->
                </tr>
            </c:forEach>
        </table>

        <h2>Zadaj vzducholoď</h2>
        <c:if test="${not empty chyba}">
            <div style="border: solid 1px red; background-color: red; padding: 10px">
                <c:out value="${chyba}"/>
            </div>
        </c:if>
        <form action="${pageContext.request.contextPath}/airships/add" method="post">
            <table>
                <tr>
                    <th>názov vzducholode:</th>
                    <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
                </tr>
                <tr>
                    <th>kapacita:</th>
                    <td><input type="text" name="capacity" value="<c:out value='${param.capacity}'/>"/></td>
                </tr>
                <tr>
                    <th>cena:</th>
                    <td><input type="text" name="price" value="<c:out value='${param.price}'/>"/></td>
                </tr>
            </table>
            <input type="Submit" value="Zadať" />
        </form>

    </body>
</html>