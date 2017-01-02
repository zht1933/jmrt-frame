<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="${ctx }/resources/cube/CmbCube.js" type="text/javascript"></script>
<script src="${ctx }/resources/cube/TreeCube.js" type="text/javascript"></script>
<script src="${ctx }/resources/cube/PReportDesignerForm.js" type="text/javascript"></script>
<script src="${ctx }/resources/cube/CReport.js" type="text/javascript"></script>
<script src="${ctx }/resources/cube/WinMembers.js" type="text/javascript"></script>
<script src="${ctx }/resources/cube/TreeDimension.js" type="text/javascript"></script>
<title>EXT示例</title>
<script>
	//zht 报表设计页面
	Ext.define('jwapp.framework.PReportDesigner',{
						extend : 'Ext.panel.Panel',
						initComponent : function() {
							var me = this, wEditor, _selected, _winMembers, mapMembers = new Ext.util.HashMap();
							var chkColTotle = Ext.create(
									'Ext.form.field.Checkbox', {
										boxLabel : '列合计'/**,
										listeners : {
											change : function(cmbox, newValue,
													oldValue, eOpts) {
												loadData();
											}
										}*/
									});
							var chkRowTotle = Ext.create(
									'Ext.form.field.Checkbox', {
										boxLabel : '行合计'/**,
										listeners : {
											change : function(cmbox, newValue,
													oldValue, eOpts) {
												loadData();
											}
										}*/
									});
							var chkType = Ext.create('Ext.form.field.Checkbox',
									{
										fieldLabel : '图表方式',
										labelAlign : 'right'/**,
										listeners : {
											change : function(cmbBox, newValue,
													oldValue, eOpts) {
												loadData();
											}
										}*/
									});
							//zht 立方体选择下拉列表
							var cmbCube = Ext.create('jwapp.framework.commons.CmbCube', {
										region : 'north',
										listeners : {
											select : function(combo, records,eOpts) {
												//treeColumns.store.load();
												//treeRows.store.load();
												//treeSlices.store.load();
												//zht 加载 获取到的立方体id 传给立方体树形列表
												//treeCube.loadData(records[0].get('id'));
											}
										}
									});

							//		zht 立方体树形列表
							var treeCube = Ext.create('jwapp.framework.commons.TreeCube',{
												region : 'center',
												viewConfig : {
													plugins : {
														ptype : 'treeviewdragdrop',
														enableDrag : true,
														enableDrop : true,
														allowContainerDrops : true
													},
													listeners : {
														nodedragover : function(
																targetNode,
																position,
																dragData) {
															//var rec = dragData.records[0];
															//return rec.get('dim') == targetNode.data.name;
														},
														drop : function(node,
																data,
																overModel,
																dropPosition,
																eOpts) {
															//loadData();
														}
													}
												}
											});
							//		zht 右侧工具条
							var tbMain = Ext.create('Ext.toolbar.Toolbar',{
												items : [
														{
															xtype : 'tbspacer',
															width : 20
														},
														chkColTotle,
														chkRowTotle,
														chkType,
														'->',
														{
															xtype : 'button',
															text : '执行',
															iconCls : 'refresh',
															handler : function() {
																loadData();
															}
														},
														{
															xtype : 'button',
															text : '导出',
															iconCls : 'excel',
															handler : function() {
																var form = Ext.create('Ext.form.Panel',{
																					standardSubmit : true
																				});
																form.getForm().submit({
																					method : 'POST',
																					//url : Ext.ctxpath + '/olap/reportExcel.do',
																					params : {
																						mdx : getMdx()
																					}
																				});
															}
														},
														{
															xtype : 'button',
															text : '保存',
															iconCls : 'save',
															handler : function() {
																if (!wEditor)
																	wEditor = Ext.create('jwapp.framework.PReportDesignerForm');
																var val = {
																	mdx : getMdx(),
																	sumcol : chkColTotle.getValue(),
																	sumrow : chkRowTotle.getValue()
																}
																wEditor.showInfo(val,function() {
																				});
															}
														} ]
											});
							//		zht 立方体表格
							var report = Ext.create('jwapp.framework.CReport',{
										region : 'center',
										autoScroll : true
									});
							//		zht 立方体选择容器（选择立方体、展示立方体）
							var cubePanel = Ext.create(
									'Ext.container.Container', {
										region : 'west',
										width : 160,
										layout : 'border',
										items : [ cmbCube, treeCube ]
									});
							//		zht 维度过滤器
							var mMembers = Ext.create('Ext.menu.Item',{
												text : '选择成员',
												handler : function() {
													if (!_winMembers)
														_winMembers = Ext.create('jwapp.framework.commons.WinMembers');//zht 立方体成员选择器
													_winMembers.showInfo(
																	cmbCube.getValue(),
																	_selected.get('dim'),
																	_selected.get('hie'),
																	_selected.get('name'),
																	function(ids) {
																		mapMembers.add(_selected.get('members'),'{'+ ids.join(',')+ '}');
																		loadData();
																	});
												}
											});
							var contextMenu = Ext.create('Ext.menu.Menu', {
								items : [ mMembers ]
							});
							//		zht 维度展示树
							var treeColumns = Ext.create('jwapp.framework.commons.TreeDimension',{
												region : 'north',
												height : 160,
												title : '列',
												split : true,
												listeners : {
													beforeitemcontextmenu : function(tree, record, item,index, e, eOpts) {
														e.stopEvent();
														if (record.get('type') == 1) {
															_selected = record;
															contextMenu.showAt(e.getXY());
															return false;
														}
													}
												},
												viewConfig : {
													plugins : {
														ptype : 'treeviewdragdrop',
														enableDrag : true,
														enableDrop : true,
														allowContainerDrops : true
													},
													listeners : {
														nodedragover : function(
																targetNode,
																position,
																dragData) {
															var rec = dragData.records[0];
															return rec.get('type') == 1;
														},
														drop : function(node,
																data,
																overModel,
																dropPosition,
																eOpts) {
															loadData();
														}
													}
												}
											})
							var treeRows = Ext.create('jwapp.framework.commons.TreeDimension',{
												region : 'center',
												title : '行',
												listeners : {
													beforeitemcontextmenu : function(
															tree, record, item,
															index, e, eOpts) {
														e.stopEvent();
														if (record.get('type') == 1) {
															_selected = record;
															contextMenu
																	.showAt(e
																			.getXY());
															return false;
														}
													}
												},
												viewConfig : {
													plugins : {
														ptype : 'treeviewdragdrop',
														enableDrag : true,
														enableDrop : true,
														allowContainerDrops : true
													},
													listeners : {
														nodedragover : function(
																targetNode,
																position,
																dragData) {
															var rec = dragData.records[0];
															return rec
																	.get('type') == 1;
														},
														drop : function(node,
																data,
																overModel,
																dropPosition,
																eOpts) {
															loadData();
														}
													}
												}
											})
							var treeSlices = Ext.create('jwapp.framework.commons.TreeDimension',{
												region : 'south',
												height : 160,
												title : '过滤',
												split : true,
												listeners : {
													beforeitemcontextmenu : function(
															tree, record, item,
															index, e, eOpts) {
														e.stopEvent();
														if (record.get('type') == 1) {
															_selected = record;
															contextMenu
																	.showAt(e
																			.getXY());
															return false;
														}
													}
												},
												viewConfig : {
													plugins : {
														ptype : 'treeviewdragdrop',
														enableDrag : true,
														enableDrop : true,
														allowContainerDrops : true
													},
													listeners : {
														nodedragover : function(
																targetNode,
																position,
																dragData) {
															var rec = dragData.records[0];
															return rec
																	.get('type') == 1;
														},
														drop : function(node,
																data,
																overModel,
																dropPosition,
																eOpts) {
															loadData();
														}
													}
												}
											});
							var selPanel = Ext.create(
									'Ext.container.Container', {
										region : 'center',
										layout : 'border',
										items : [ treeColumns, treeRows, treeSlices ]
									});
							var leftPanel = Ext.create(
									'Ext.container.Container', {
										region : 'west',
										width : 320,
										layout : 'border',
										items : [ cubePanel, selPanel ]
									});
							
							Ext.apply(me, {
								height : 450,
								renderTo : 'page-content',//本框架主显示内容div的id为:page-content
								tbar : tbMain,
								layout : 'border',
								items : [ leftPanel, report ]
							});

							//		zht 获取MDX语句函数
							var getMdx = function() {
								var colNodes = new Array(), rowNodes = new Array(), sliceNodes = new Array();
								treeColumns.getRootNode().eachChild(
										function(n) {
											colNodes.push(n.data);
											return true;
										})
								treeRows.getRootNode().eachChild(function(n) {
									rowNodes.push(n.data);
									return true;
								})
								treeSlices.getRootNode().eachChild(function(n) {
									sliceNodes.push(n.data);
									return true;
								})

								var mdx = 'select {'+ getColByNode(colNodes, 0,colNodes.length - 1)+ '} on columns,{'+ getColByNode(rowNodes, 0,rowNodes.length - 1)+ '} on rows'+ ' from '+ cmbCube.getValue()+ (sliceNodes.length > 0 ? ' where {'+ getColByNode(sliceNodes, 0,colNodes.sliceNodes - 1)+ '}' : '');
								return mdx;
							}
							var getColByNode = function(nodes, start, end) {
								if (start == end)
									return getMembers(nodes[start].members);
								var arr = new Array(), arrHj = new Array();
								arr.push(nodes[start].members);
								for (var i = start + 1; i <= end; i++) {
									if (nodes[start].dim != nodes[i].dim)
										return 'crossjoin('+ getCols(arr, arrHj) + ','+ getColByNode(nodes, i, end)+ ')';
									if ('(All)' == nodes[i].caption)
										arrHj.push(nodes[i].members);
									else
										arr.push(nodes[i].members);
								}
								return getCols(arr, arrHj);
							}
							var getCols = function(arr, arrHj) {
								var a = new Array();
								for ( var i in arr) {
									var m = getMembers(arr[i]);
									a.push(m);
								}
								var ret = (a.length > 1) ? 'Hierarchize({'+ a.join(',') + '})' : a[0];
								if (arrHj.length == 0)
									return '{' + ret + '}';
								return '{' + ret + ',' + arrHj.join(',') + '}';
							}
							var getMembers = function(m) {
								var o = mapMembers.get(m);
								if (o != null && o != '{}')
									return o;
								return m;
							}
							var loadData = function() {
								if (!treeColumns.getRootNode().hasChildNodes()
										|| !treeRows.getRootNode()
												.hasChildNodes())
									return;
								report.loadData('', getMdx(), chkColTotle
										.getValue(), chkRowTotle.getValue(),
										chkType.getValue());
							}
							var getHj = function(nodes) {
								var arr = new Array();
								for ( var i in nodes) {
									if (Ext.Array.indexOf(arr, '['+ nodes[i].dim + ']') == -1) arr.push('[' + nodes[i].dim + ']');
								}
								return ',(' + arr.join(',') + ')';
							}
							me.callParent();
						}
					});

	var MyPanel = new jwapp.framework.PReportDesigner();//创建自定义类Ext.ux.MyPanel对象MyPanel
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

