<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath }" />
<body>
	<!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
	<center>
		<div id="main" style="width: 600px; height: 400px;"></div>
	</center>
	<script type="text/javascript">
		// 基于准备好的dom，初始化echarts实例
		var myChart = echarts.init(document.getElementById('main'), 'shine');

		// 指定图表的配置项和数据
		var option = {
			title : {
				text : 'ECharts 示例'
			},
			tooltip : {},
			legend : {
				data : [ '劳时' ]
			},
			xAxis : {
				data : ['张三','李四']
			},
			yAxis : {},
			series : [ {
				name : '劳时',
				type : 'bar',
				data : ['67','143']
			} ]
		};

		// 使用刚指定的配置项和数据显示图表。
		myChart.setOption(option);
		myChart.showLoading();

		myajax = function() {
			//获得图表的options对象  
			var options = myChart.getOption();
			//alert(options)
			//通过Ajax获取数据  
			$.ajax({
				type : "post",
				async : false, //同步执行  
				url : "${ctx}/user/getEchars.html",
				dataType : "json", //返回数据形式为json  
				success : function(data) {
					options.xAxis[0].data = data.name;
					options.series[0].data = data.count;

					myChart.hideLoading();
					myChart.setOption(options);
				},
				error : function(data) {
					alert("图表请求数据失败啦!");  
					myChart.hideLoading();
				}
			});
		}

		myajax();//aja后台交互
	</script>
</body>

