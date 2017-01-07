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
	
Ext.define('jwapp.framework.PReportUniversal', {
	extend : 'Ext.panel.Panel',
	initComponent : function() {
		var me = this;
		var strID,strRecord;
		
		var chkType = Ext.create('Ext.form.field.Checkbox',{
			fieldLabel: '图表方式',
			labelAlign: 'right',
			 checked:true,
			listeners: {
				change: function(cmbBox, newValue, oldValue, eOpts ){
					var rec = getReportRec();
					if(rec){
						report.loadData(rec.id,'',rec.sumcol==1,rec.sumrow==1, chkType.getValue(),rec.moduleseparate);
						report.show();
					}
				}
			}
		});
		
		var treePanel = Ext.create('jwapp.framework.commons.treeUniversalReport', {
			region : 'west',
			split : true,
			width : 160,
			listeners: {
				select : function(tree, record, index, eOpts) {
					strID = record.getData().id;
					strRecord = record.getData();
					var rec = getReportRec();
					if(rec){
						report.loadData(rec.id,'',rec.sumcol==1,rec.sumrow==1, chkType.getValue(),rec.moduleseparate);
						report.show();
					}
				}
			}
		});
		
		var tbMain = Ext.create('Ext.toolbar.Toolbar', {
			items : [chkType, '->',{
				xtype : 'button',
				text : '执行',
				iconCls : 'refresh',
				handler : function() {
					if(strID!=null){
						var rec = getReportRec();
						if(rec)
							report.loadData(rec.id,'',rec.sumcol==1,rec.sumrow==1, chkType.getValue(),rec.moduleseparate);
						report.show();
					}else{
						return null;
					}
				}
			} ,{
				xtype : 'button',
				text : '导出',
				iconCls : 'excel',
				handler : function() {
					var form = Ext.create('Ext.form.Panel',{
						standardSubmit : true
					});
					form.getForm().submit({
						method : 'POST',
						url : Ext.ctxpath + '/olap/reportExcel.html',
						params : {reportid :strID}
					});
					report.show();
				}
			},{
				xtype : 'button',
				text : '删除',
				iconCls : 'delete',
				handler : function() {
					Ext.Ajax.request({
						  url: Ext.ctxpath+'/olap/deleteReport.html',
						   params : {reportid :strID},
						    success: function(response){
						    	var result = Ext.JSON.decode(response.responseText);
								if (result.success) {
									Ext.Msg.alert('提示','删除成功!');
									treePanel.getStore().reload();
									report.hide();
								} else  Ext.Msg.alert('错误','删除失败!');									
						    }
				    	});
					
				}
			}]
		});
//		zht 立方体表格
		var report = Ext.create('jwapp.framework.CReport',{
			region: 'center',
			autoScroll: true
		});
		
		Ext.apply(me, {
			height : 450,
			renderTo : 'page-content',//本框架主显示内容div的id为:page-content
			tbar : tbMain,
			layout: 'border',
			autoScroll: true,
			items: [treePanel,report]
		});
		
		var getReportRec = function(){
			return strRecord; 
		}
		
		me.callBack = function(params){
			var root = {
				name : '报表列表',
				seq: '.0.',
				id : params
			}
			treePanel.setRootNode(root) ;
		}
		
		me.callParent();
	}
});

	var MyPanel = new jwapp.framework.PReportUniversal();//创建自定义类Ext.ux.MyPanel对象MyPanel
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

