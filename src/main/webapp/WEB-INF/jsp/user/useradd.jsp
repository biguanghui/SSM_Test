<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fm" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="${pageContext.request.contextPath }/statics/calendar/WdatePicker.js"></script>
</head>
<body>
<fm:form  name="myform" method="post" modelAttribute="user">
     <fm:errors path="userCode"></fm:errors><br/>
    用户编码:<fm:input path="userCode"/><br/>

    <fm:errors path="userName"></fm:errors><br/>
    用户名称:<fm:input path="userName"/><br/>

    <fm:errors path="userPassword"></fm:errors><br/>
    用户密码:<fm:password path="userPassword"/><br/>

    <fm:errors path="birthday"></fm:errors><br/>
    用户生日:<fm:input path="birthday" Class="Wdate" readonly="readonly"
                   onclick="WdatePicker();" class="Wdate"/><br/>


    用户地址:<fm:input path="address"/><br/>
    联系电话:<fm:input path="phone"/><br/>
    用户角色:

    <fm:radiobutton path="userRole" value="1"/>系统管理员
    <fm:radiobutton path="userRole" value="2"/>经理
    <fm:radiobutton path="userRole" value="3" checked="checked"/>普通用户
    <br/>
    <input type="submit" value="保存"/>
</fm:form>
</body>
</html>
