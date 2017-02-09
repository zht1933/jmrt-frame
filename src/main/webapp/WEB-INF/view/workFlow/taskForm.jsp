<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath }" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>任务办理</title>
</head>
<body>
 	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
		  <tr>
		  	<td>
		  		<form action="workflowAction_submitTask.action" id="leaveBill" name="leaveBill" method="POST">
			  		<div align="left" class="STYLE21">
			  			<!-- 任务ID -->
						<input type="hidden" name="taskId" value="${task.id}"/> 
			  			<!-- 请假单ID -->
						<input type="hidden" name="id" value="${leaveBill.id}"/> 
				 		请假天数:<input type="text" name="days" disabled="disabled" Style="width: 200px;" value="${leaveBill.days}"/>
				 		请假原因:<input type="text" name="content" disabled="disabled" Style="width: 545px;" value="${leaveBill.content}" /><br/>
				 		请假备注:<textarea name="remark" disabled="disabled" cols="50" rows="5"  Style="width: 800px;" >${leaveBill.remark}</textarea><br/>
				 		批&emsp;&emsp;注:<textarea name="comment" cols="50" rows="5"  Style="width: 800px;" ></textarea>
				 		<br/>
				 		<!-- 使用连线的名称作为按钮 -->
				 		<!-- 
				 		<s:if test="#outcomeList!=null && #outcomeList.size()>0">
				 			<s:iterator value="#outcomeList">
				 				<input type="submit" name="outcome" value="<s:property/>" class="button_ok"/>
				 		 -->		
				 				<center><input type="button" value="提交" onclick="tj('leaveBill')"/> &nbsp; <input type="button" value="返回" onclick="fh()"/></center>
				 		<!--
				 		 	</s:iterator>
				 		</s:if>
				 		 -->
			 		</div>
			 	</form>
		  	</td>
		  </tr>
	</table>
	<hr>
	<br>
	<!-- 
	<s:if test="#commentList!=null && #commentList.size()>0">
	 -->
			    <table width="100%" border="1" cellpadding="0" cellspacing="1" bgcolor="#a8c7ce" onmouseover="changeto()"  onmouseout="changeback()">
				  <tr>
				    <td height="30" align="center" colspan="3">
				    	<strong>历史批注信息</strong>
				    </td>
				  </tr>
			      <tr>
			        <td width="15%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">审批日期</span></div></td>
			        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">批注人</span></div></td>
			        <td width="75%" height="20" bgcolor="d3eaef" class="STYLE6"><div align="center"><span class="STYLE10">批注信息</span></div></td>
			      </tr>
			      <!-- 
			      <s:iterator value="#commentList">
			       -->
			      	<tr>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE6"><div align="center"><!-- <s:date name="time" format="yyyy-MM-dd HH:mm:ss"/> --></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center"><!-- <s:property value="userId"/> --></div></td>
				        <td height="20" bgcolor="#FFFFFF" class="STYLE19"><div align="center"><!-- <s:property value="fullMessage"/> --></div></td>
				    </tr> 
			      <!-- 
			      </s:iterator>
			       -->
			    </table>
	<!-- 
	</s:if>
	<s:else>
	 -->
		<table width="100%" border="1" align="center" cellpadding="0" cellspacing="0">
			  <tr>
			    <td height="30"><table width="100%" border="0" cellspacing="0" cellpadding="0">
			      <tr>
			        <td height="24" bgcolor="#F7F7F7"><table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <tr>
			            <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
			              <tr>
			                <td width="94%" valign="bottom" align="center"><span><b>暂时没有批注信息</b></span></td>
			              </tr>
			            </table></td>
			          </tr>
			        </table></td>
			      </tr>
			    </table></td>
			  </tr>
		</table>
		<!-- 
	</s:else>
	 	-->
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
		webside.common.loadPage("/act/actTask.html");
	}
</script>