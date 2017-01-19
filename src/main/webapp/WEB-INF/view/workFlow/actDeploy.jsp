<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath }" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>工作流部署管理</title>
</head>
<body>

	<hr>
	<table width="100%" border="1" align="left" cellpadding="0"
		cellspacing="0">
		<tr align="left">
			<td align="center" colspan="4"><strong>部署信息管理列表</strong></td>
		</tr>
		<tr>
			<td width="10%" height="20" bgcolor="d3eaef"><div align="center">
					<span>ID</span>
				</div></td>
			<td width="60%" height="20" bgcolor="d3eaef"><div align="center">
					<span>流程名称</span>
				</div></td>
			<td width="20%" height="20" bgcolor="d3eaef"><div align="center">
					<span>发布时间</span>
				</div></td>
			<td width="10%" height="20" bgcolor="d3eaef"><div align="center">
					<span>操作</span>
				</div></td>
		</tr>

		<tr>
			<td height="20" bgcolor="#FFFFFF"><div align="center">id</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">name</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">deploymentTime</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">
					<a href="workflowAction_delDeployment.action?deploymentId=id">删除</a>
				</div></td>
		</tr>
	</table>
	<br />
	<hr>
	<br />
	<table width="100%" border="1" align="left" cellpadding="0"
		cellspacing="0">
		<tr align="left">
			<td align="center" colspan="8"><strong>流程定义信息列表</strong></td>
		</tr>
		<tr>
			<td width="12%" height="20" bgcolor="d3eaef"><div align="center">
					<span>ID</span>
				</div></td>
			<td width="18%" height="20" bgcolor="d3eaef"><div align="center">
					<span>名称</span>
				</div></td>
			<td width="10%" height="20" bgcolor="d3eaef"><div align="center">
					<span>流程定义的KEY</span>
				</div></td>
			<td width="10%" height="20" bgcolor="d3eaef"><div align="center">
					<span>流程定义的版本</span>
				</div></td>
			<td width="15%" height="20" bgcolor="d3eaef"><div align="center">
					<span>流程定义的规则文件名称</span>
				</div></td>
			<td width="15%" height="20" bgcolor="d3eaef"><div align="center">
					<span>流程定义的规则图片名称</span>
				</div></td>
			<td width="10%" height="20" bgcolor="d3eaef"><div align="center">
					<span>部署ID</span>
				</div></td>
			<td width="10%" height="20" bgcolor="d3eaef"><div align="center">
					<span>操作</span>
				</div></td>
		</tr>
		<tr>
			<td height="20" bgcolor="#FFFFFF"><div align="center">id</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">name</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">key</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">version</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">resourceName</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">diagramResourceName</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">deploymentId</div></td>
			<td height="20" bgcolor="#FFFFFF"><div align="center">
					<a target="_blank"
						href="workflowAction_viewImage.action?deploymentId=deploymentId&imageName=diagramResourceName">查看流程图</a>
				</div></td>
		</tr>
	</table>
	<br />
	<hr>
	<br />
	<!-- 发布流程 webside.common.addModel('/act/newdeploy.html')-->
	<form id="uploadForm">
		<table width="50%" border="1" align="left" cellpadding="0"
			cellspacing="0">
			<tr align="left">
				<td align="center" colspan="2"><strong>部署流程定义</strong></td>
			</tr>
			<tr>
				<td align="right">流程名称:</td>
				<td align="left"><input type="text" name="filename"
					width="200px" /></td>
			</tr>
			<tr>
				<td align="right">流程文件:</td>
				<td align="left"><input type="file" name="file" width="200px" /><input type="button" onclick="doUpload()" value="上传流程" /><br /></td>
			</tr>
		</table>
	</form>
</body>
</html>

<script type="text/javascript">
function doUpload() {  
    var formData = new FormData($( "#uploadForm" )[0]);  
    $.ajax({  
         url: "${ctx}/act/newdeploy.html", 
         type: 'POST',  
         data: formData,  
         async: false,  
         cache: false,  
         contentType: false,  
         processData: false,  
         success: function (returndata) {  
        	 layer.msg("流程部署成功！", {icon : 1});
             webside.common.loadPage("/act/actDeploy.html");
         }
    });  
} 
</script>

