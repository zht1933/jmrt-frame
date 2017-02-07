<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>上传文件</title>
</head>
<body>
<form id="uploadForm">
	  <input type ="file" id="ImportFileInput" name= "myfile" class="hidden"/>  
      <div class ="input-append">  
             <label for ="importFileName"> 上传原始文件：</label >  
             <input type ="text" class="input-large" id= "importFileName" />  
             <a class ="btn btn-primary btn-sm" onclick= "$('#ImportFileInput').click();" >选择文件</a>  
             <a class ="btn btn-danger btn-sm" onclick= "tj()" >上 传</a> 
      </div > 
</form>
</body>
</html>

<script type="text/javascript">
$(document).ready(function(e) {  
    $('body').on('change',$('#ImportFileInput'),function(){  
          $( "#importFileName").val($( "#ImportFileInput").val()); 
    });  
}); 
    
function tj(){
	var formData = new FormData($("#uploadForm")[0]);
	$.ajax({
		url : "${ctx}/user/fileUpLoad.html",
		type : 'POST',
		data : formData, //此种格式用于文件上传
		async : false,
		cache : false,
		contentType : false,
		processData : false,
	    dataType: 'json',//服务器返回的格式  
		success: function(data){  
	        if(data.result=='success'){  
	        	$( "#importFileName").val(""); 
				layer.msg("文件上传成功！", {
					icon : 1
				});
				//webside.common.loadPage("/act/actDeploy.html");//加载跳转页面
	        }else{  
	            //coding  //文件上传失败
	        }  
	    },  
	    error: function (data, status, e){  
	        //coding    //文件上传发生异常情况
	    }  
	});
}
</script>


