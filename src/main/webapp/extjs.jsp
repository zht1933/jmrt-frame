<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EXT示例</title>
<script>
	
	Ext.define('Ext.ux.MyPanel', {//自定义Ext对象类Ext.ux.MyPanel
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;

			var pan = Ext.create('Ext.panel.Panel', {
				region : 'center',
				title : 'Hello',
				width : 400,
				autoHeight : true,
				html : 'World!'
			});

			Ext.apply(this, {
				height : 200,
				layout : 'border',
				renderTo : 'page-content',//本框架主显示内容div的id为:page-content
				items : [ pan ]
			});

			me.callParent();
		}
	});

	var MyPanel = new Ext.ux.MyPanel();//创建自定义类Ext.ux.MyPanel对象MyPanel
	ExtContainer.removeAll();//ExtContainer框架全局容器，移除容器内全部组件
	ExtContainer.add(MyPanel);//ExtContainer框架全局容器，向容器内添加对象MyPanel
	ExtContainer.doLayout();//ExtContainer框架全局容器重新渲染

	//注：全局窗口与全局容器不可同时使用。
	//ExtWin.removeAll();//ExtWin框架全局窗口，移除窗口内全部组件
	//ExtWin.add(MyPanel);//ExtWin框架全局窗口，向窗口内添加对象MyPanel
	//ExtWin.show();//ExtWin框架全局窗口显示	
	
</script>
</head>
<body>
</body>
</html>

