<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath }" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>工作流任务管理</title>
</head>

<body>
	<table width="100%" border="1" align="left" cellpadding="0" cellspacing="0">
			<tr align="left">
				<td align="center" colspan="5"><strong>个人任务管理列表</strong></td>
			</tr>
			<tr>
				<td width="15%" height="20" bgcolor="d3eaef" class="STYLE6">
					<div align="center">
						<span class="STYLE10">任务ID</span>
					</div></td>
				<td width="25%" height="20" bgcolor="d3eaef" class="STYLE6">
					<div align="center">
						<span class="STYLE10">任务名称</span>
					</div></td>
				<td width="20%" height="20" bgcolor="d3eaef" class="STYLE6">
					<div align="center">
						<span class="STYLE10">创建时间</span>
					</div></td>
				<td width="20%" height="20" bgcolor="d3eaef" class="STYLE6">
					<div align="center">
						<span class="STYLE10">办理人</span>
					</div></td>
				<td width="20%" height="20" bgcolor="d3eaef" class="STYLE6">
					<div align="center">
						<span class="STYLE10">操作</span>
					</div></td>
			</tr>

			<c:forEach var="item" items="${list }">
					<tr>
						<td height="20" bgcolor="#FFFFFF" class="STYLE6"><div align="center">
								${item.id }
							</div></td>
						<td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center">
								${item.name }
							</div></td>
						<td height="20" bgcolor="#FFFFFF" class="STYLE19"><div
								align="center">
								<fmt:formatDate value="${item.createTime }" type="date" dateStyle="long" />
								<fmt:formatDate value="${item.createTime }" type="time" />
							</div></td>
						<td height="20" bgcolor="#FFFFFF" class="STYLE19"><div
								align="center">
								${item.assignee }
							</div></td>
						<td height="20" bgcolor="#FFFFFF"><div align="center"
								class="STYLE21">
								<a href="${ctx }/workflowAction_viewTaskForm.action?taskId=${item.id }">办理任务</a>
								<a target="_blank" href="workflowAction_viewCurrentImage.action?taskId=${item.id }">查看当前流程图</a>
							</div></td>
					</tr>
				</c:forEach>
		 	
		</table>
</body>
</html>

<script type="text/javascript">

	/**
	function jumpBill(url){
		webside.common.addModel(url);
	}
	
	function delLeaveBill(billID){
		$.ajax({
            type : "POST",
			url : "${ctx}/leaveBill/deleteLeaveBill.html?id="+billID,
            dataType : "json",
            success : function(resultdata) {
				layer.msg("删除成功！", {
					icon : 1
				});
				webside.common.loadPage("/leaveBill/home.html");
            }
        });		
	}
	*/
</script>

