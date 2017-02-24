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
	<table width="100%" border="1" align="left" >
		<tr>
			<td align="center"><strong>序号</strong></td>
			<td align="center"><strong>工作单位</strong></td>
			<td align="center"><strong>车间</strong></td>
			<td align="center"><strong>姓名</strong></td>
			<td align="center"><strong>学历</strong></td>
			<td align="center"><strong>现岗位</strong></td>
			<td align="center"><strong>晋升机车司机日期</strong></td>
			<td align="center"><strong>驾驶证类别</strong></td>
			<td align="center"><strong>身份证号</strong></td>
			<td align="center"><strong>驾驶证编号</strong></td>
			<td align="center"><strong>发证日期</strong></td>
			<td align="center"><strong>到期换发新证日期</strong></td>
			<td align="center"><strong>备注</strong></td>
		</tr>
		<c:forEach var="item" items="${list }">
			<tr>
				<td align="center">${item.ryjzxxb_xh }</td>
				<td align="center">${item.ryjzxxb_gzdw }</td>
				<td align="center">${item.ryjzxxb_cj }</td>
				<td align="center">${item.ryjzxxb_xm }</td>
				<td align="center">${item.ryjzxxb_xl }</td>
				<td align="center">${item.ryjzxxb_xgw }</td>
				<td align="center">${item.ryjzxxb_jsjcsjrq.replace("00:00:00.0","") }</td>
				<td align="center">${item.ryjzxxb_jszlb }</td>
				<td align="center">${item.ryjzxxb_sfzh }</td>
				<td align="center">${item.ryjzxxb_jszbh }</td>
				<td align="center">${item.ryjzxxb_fzrq.replace("00:00:00.0","") }</td>
				<td align="center">${item.ryjzxxb_dqhfxzrq.replace("00:00:00.0","") }</td>
				<td align="center">${item.ryjzxxb_bz }</td>
			</tr>
		</c:forEach>
			<tr>
				<td colspan="13" align="right"><strong>
					<span id="spanFirst"><a href="#" onclick="fy(1,20,${page.pages })">首页</a></span> 
					<span id="spanPre"><a href="#" onclick="fy(${page.pageNum-1 },20,${page.pages })">上一页</a></span> 
					<span id="spanNext"><a href="#" onclick="fy(${page.pageNum+1 },20,${page.pages })">下一页</a></span> 
					<span id="spanLast"><a href="#" onclick="fy(${page.pages },20,${page.pages })">尾页</a></span> 
					第<span id="spanPageNum"><font color="red">${page.pageNum }</font></span>页/共<span id="spanTotalPages"><font color="red">${page.pages }</font></span>页/共<span id="spanTotalRecord"><font color="red">${page.total }</font></span>条记录
					</strong> 
				</td>
			</tr>
	</table>

</body>
</html>

<script type="text/javascript">
	function fy(size,count,pages){
		if(size<1){
			size=1
		}
		if(size>pages){
			size=pages
		}
		webside.common.loadPage("/excel/excelHome.html?size="+size+"&count="+count);
	}

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


