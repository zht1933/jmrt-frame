<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath }" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看当前流程图</title>
</head>
<body>
	<input type="button" name="button" value="返 回" onclick="fh()"/><br>
	<!-- 1.获取到规则流程图 -->
	<img style="position: absolute; top: 35px; left: 0px;"
		src="${ctx}/act/viewImage.html?deploymentId=${deploymentId }&imageName=${imageName }">

	<!-- 2.根据当前活动的坐标，动态绘制DIV -->
	<div style="position: absolute;border:1px solid red;top:${acs.y+35 }px;left: ${acs.x }px;width: ${acs.width }px;height: ${acs.height }px; "></div>

</body>
</html>

<script type="text/javascript">
function fh(){
	webside.common.loadPage("/leaveBill/home.html");
}
</script>