<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath }" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>请假申请管理</title>
</head>
<body>
 	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
		  <tr>
		        <td height="20" bgcolor="#FFFFFF" class="STYLE10" colspan="8"><div align="left">
					<a href="#" onclick="jumpBill('/leaveBill/actLeaveBillFormInput.html')">添加请假申请</a>
				</div></td>
		  </tr> 
		  <tr>
		    <td><table width="100%" border="1" cellpadding="0" cellspacing="1" bgcolor="#a8c7ce" onmouseover="changeto()"  onmouseout="changeback()">
		      <tr>
		        <td width="5%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">ID</span></div></td>
		        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">请假人</span></div></td>
		        <td width="5%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">请假天数</span></div></td>
		        <td width="15%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">请假事由</span></div></td>
		        <td width="20%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">请假备注</span></div></td>
		        <td width="15%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">请假时间</span></div></td>
		        <td width="5%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">请假状态</span></div></td>
		        <td width="25%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">操作</span></div></td>
		      </tr>
		      <c:forEach var="item" items="${billList }">
		      		<tr>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE6"><div align="center">${item.id }</div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center">${item.user.userName }</div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center">${item.days }</div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center">${item.content }</div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center">${item.remark }</div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center">
						<fmt:formatDate value="${item.leaveDate }" type="date" dateStyle="long" />
						<fmt:formatDate value="${item.leaveDate }" type="time" /></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19">
				        	<div align="center">
									<c:choose>
										<c:when test="${item.state == '0'}">
											<c:out value="初始录入" />
										</c:when>
										<c:when test="${item.state == '1'}">
											<c:out value="审核中" />
										</c:when>
										<c:otherwise>
											<c:out value="审核完成" />
										</c:otherwise>
									</c:choose>
								</div>
			            </td>
				        <td height="20" bgcolor="#FFFFFF"><div align="center" class="STYLE21">
									<c:choose>
										<c:when test="${item.state == '0'}">
											<a href="#" onclick="jumpBill('/leaveBill/actLeaveBillFormInput.html?id=${item.id }')">修改</a>
											<a href="#" onclick="delLeaveBill(${item.id })">删除</a>
											<a href="#" onclick="jumpBill('/act/startProcess.html?id=${item.id }')">申请请假</a>
										</c:when>
										<c:when test="${item.state == '1'}">
											<a href="#" onclick="jumpBill('/act/viewHisComment.html?id=${item.id }')">查看审核记录</a>
											<a href="#" onclick="jumpBill('/act/viewFlowtImage.html?id=${item.id }')">流程图</a>
										</c:when>
										<c:otherwise>
											<a href="#" onclick="delLeaveBill(${item.id })">删除</a>
											<a href="#" onclick="jumpBill('/act/viewHisComment.html?id=${item.id }')">查看审核记录</a>
										</c:otherwise>
									</c:choose>
								</div></td>
				    </tr> 
				</c:forEach>
		    </table></td>
		  </tr>
	</table>
</body>
</html>

<script type="text/javascript">
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
</script>


