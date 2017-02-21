<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Excel操作</title>
</head>
<body>
	<form id="uploadForm">
		<input type="file" id="ImportFileInput" name="myfile" class="hidden" />
		<div class="input-append">
			<label for="importFileName"> 上传原始文件：</label> <input type="text"
				class="input-large" id="importFileName" /> <a
				class="btn btn-primary btn-sm"
				onclick="$('#ImportFileInput').click();">选择文件</a> <a
				class="btn btn-danger btn-sm" onclick="tj()">上 传</a>
		</div>
	</form>
	<hr>
	<a class="btn btn-danger btn-sm" onclick="dr()">导入excel数据</a> 
	<a class="btn btn-primary btn-sm" href="excel/writeExcel4DB.html" target="_blank">导出excel数据</a>
	<table width="100%" border="1" align="left" cellpadding="0"
		cellspacing="0">
		<tr>
			<td>序号</td>
			<td>工作单位</td>
			<td>车间</td>
			<td>姓名</td>
			<td>学历</td>
			<td>现岗位</td>
			<td>晋升机车司机日期</td>
			<td>驾驶证类别</td>
			<td>身份证号</td>
			<td>驾驶证编号</td>
			<td>发证日期</td>
			<td>到期换发新证日期</td>
			<td>备注</td>
		</tr>
		<c:forEach var="item" items="${list }">
			<tr>
				<td>${item.ryjzxxb_xh }</td>
				<td>${item.ryjzxxb_gzdw }</td>
				<td>${item.ryjzxxb_cj }</td>
				<td>${item.ryjzxxb_xm }</td>
				<td>${item.ryjzxxb_xl }</td>
				<td>${item.ryjzxxb_xgw }</td>
				<td>${item.ryjzxxb_jsjcsjrq }</td>
				<td>${item.ryjzxxb_jszlb }</td>
				<td>${item.ryjzxxb_sfzh }</td>
				<td>${item.ryjzxxb_jszbh }</td>
				<td>${item.ryjzxxb_fzrq }</td>
				<td>${item.ryjzxxb_dqhfxzrq }</td>
				<td>${item.ryjzxxb_bz }</td>
			</tr>
		</c:forEach>
	</table>


</body>
</html>

<script type="text/javascript">
	$(document).ready(function(e) {
		$('body').on('change', $('#ImportFileInput'), function() {
			$("#importFileName").val($("#ImportFileInput").val());
		});
	});

	function tj() {
		var formData = new FormData($("#uploadForm")[0]);
		$.ajax({
			url : "${ctx}/excel/fileUpLoad.html",
			type : 'POST',
			data : formData, //此种格式用于文件上传
			async : false,
			cache : false,
			contentType : false,
			processData : false,
			dataType : 'json',//服务器返回的格式  
			success : function(data) {
				if (data.result == 'success') {
					$("#importFileName").val("");
					layer.msg("文件上传成功！", {
						icon : 1
					});
					//webside.common.loadPage("/act/actDeploy.html");//加载跳转页面
				} else {
					//coding  //文件上传失败
				}
			},
			error : function(data, status, e) {
				//coding    //文件上传发生异常情况
			}
		});
	}

	function dr() {
		$.ajax({
			type : "POST",
			url : "${ctx}/excel/readExcel2DB.html",
			dataType : "json",
			success : function(resultdata) {
				layer.msg("导入成功！", {
					icon : 1
				});
				webside.common.loadPage("/excel/excelHome.html");
			}
		});
	}
</script>


