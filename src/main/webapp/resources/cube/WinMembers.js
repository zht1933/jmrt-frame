//zht 立方体成员选择器
Ext.define('jwapp.framework.commons.WinMembers', {
	extend : 'Ext.window.Window',
	initComponent : function() {
		var me = this, _callback;
		var bOk = Ext.create('Ext.button.Button', {
			text : '保存',
			handler : function() {
				var form = formPanel.getForm();
				if (form.isValid()) {
					form.submit({
						success : function(form, action) {
							me.close();
						},
						failure : function(form, action) {
							Ext.Msg.alert('失败', action.result.msg);
						}
					});
				}
			}
		});
		var bCancel = Ext.create('Ext.button.Button', {
			text : '取消',
			handler : function() {
				me.close();
			}
		});
		var store = new Ext.data.Store({
			pageSize : 20,
			fields : [ {
				name : 'name'
			}, {
				name : 'caption'
			} ],
			proxy : {
				type : 'ajax',
				url : Ext.ctxpath + '/olap/getMembers.html',//查询维度成员
				reader : {
					type : 'json',
					root : 'items',
					totalProperty : 'total'
				}
			},
			remoteFilter : true,
			autoLoad : false
		});
		var gridPanel = Ext.create('Ext.grid.Panel', {
			region : 'center',
			border : false,
			selType: 'checkboxmodel',
			columnLines: true,
			columns : [ {
				xtype : 'rownumberer',
				width : 40
			}, {
				header : '名称',
				width : 100,
				dataIndex : 'caption'
			}],
			store : store
		});	
		Ext.apply(me, {
			title : '选择成员',
			layout : 'fit',
			closeAction : 'hide',
			width: 420,
			height: 320,
			items : [ gridPanel ],
			buttons: [{ 
				text: '确认', 
				handler: function(){
					if(_callback){
						var arr = new Array();
						var datas = gridPanel.getSelectionModel().getSelection();
						for(var i in datas){
							arr.push(datas[i].get('name'));
						}
						_callback(arr);
					}
					me.close();
				}
			},{ 
				text: '取消', 
				handler: function(){
					me.close();
				}
			}]
		});
		me.showInfo = function(cubename, dimensionname, hierarchyname, levelname, callback){
			_callback = callback;
			store.load({
				params:{
					cubename: cubename, 
					dimensionname: dimensionname, 
					hierarchyname: hierarchyname,
					levelname: levelname
				}
			});
			me.show();
		};
		me.callParent();
	}
});