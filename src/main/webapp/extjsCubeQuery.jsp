<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="${ctx }/resources/cube/CReport.js" type="text/javascript"></script>
<script src="${ctx }/resources/cube/treeUniversalReport.js" type="text/javascript"></script>
<title>EXT示例</title>
<script>
	
Ext.define('jwapp.framework.PMdxQuery', {
	extend : 'Ext.panel.Panel',
	initComponent : function() {
		var me = this;
		var tbMain = Ext.create('Ext.toolbar.Toolbar', {
			items : [{ xtype: 'tbspacer', width: 20 }, '->',{
				xtype : 'button',
				text : '执行',
				iconCls : 'refresh',
				handler : function() {
					report.loadData('', txtMdx.getValue(), 0, 0, 0);
				}
			} ,{
				xtype : 'button',
				text : '保存',
				iconCls : 'save',
				handler : function() {
					Ext.Msg.prompt('输入', '报表名称:', function(btn, text){
					    if (btn == 'ok'){
							Ext.Ajax.request({
								  url: Ext.ctxpath+'/olap/saveReport.html',
								    params: {
								    	name: text,
								    	mdx: txtMdx.getValue(),
								    	sumcol: 0,
								    	sumrow: 0
								    },
								    success: function(response){
										var result = Ext.JSON.decode(response.responseText);
										if (result.success) {
											Ext.Msg.alert('提示','保存成功!');	
										} else
										    Ext.Msg.alert('错误','保存失败!');								
								    }
						    	});
					    }
					});
				}			
			}]
		});
		var txtMdx = Ext.create('Ext.form.field.TextArea');
		var formPanel = Ext.create('Ext.form.Panel',{
			region: 'north',
			height: 120,
			layout: 'fit',
			items: txtMdx,
			split: true
		});
		var report = Ext.create('jwapp.framework.CReport',{
			region: 'center'
		});
		Ext.apply(me, {
			height : 450,
			renderTo : 'page-content',//本框架主显示内容div的id为:page-content
			tbar : tbMain,
			layout: 'border',
			autoScroll: true,
			items: [formPanel, report]
		});
		me.callParent();
	}
});

	var MyPanel = new jwapp.framework.PMdxQuery();//创建自定义类Ext.ux.MyPanel对象MyPanel
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

