<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath }" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>请假申请管理</title>
</head>
<body>
	<form id="leaveBill" name="leaveBill" method="post">
		<table width="100%" border="1" align="center" cellpadding="0" cellspacing="0">
			<tr>
				<th><center><strong>新增/修改请假申请</strong></center></th>
			</tr>
			<tr>
				<td>
					<div align="left" class="STYLE21">
						<input type="hidden" name="id" value="${bill.id}"/> 
						<input type="hidden" name="u_id" value="${bill.user.id}"/> 
						请假天数:<input type="text" name="days" Style="width: 200px;" value="${bill.days}"/><br /> 
						请假原因:<input type="text" name="content" Style="width: 800px;" value="${bill.content}" /><br /> 
						备&emsp;&emsp;注:<textarea name="remark" cols="50" rows="5"  Style="width: 800px;" >${bill.remark}</textarea><br /> 
						<center><input type="button" value="提交" onclick="tj('leaveBill')"/> &nbsp; <input type="button" value="返回" onclick="fh()"/></center>
					</div>
				</td>
			</tr>
		</table>

	</form>
</body>
</html>
<script type="text/javascript">
	function tj(formId){
		var data = $("#" + formId).serialize();
		
		$.ajax({
            type : "POST",
			url : "${ctx}/leaveBill/insertLeaveBill.html",
			data : data,
            dataType : "json",
            success : function(resultdata) {
				layer.msg("保存成功！", {
					icon : 1
				});
				webside.common.loadPage("/leaveBill/home.html");
            }
        });
		
	}
	
	function fh(){
		webside.common.loadPage("/leaveBill/home.html");
	}
</script>

