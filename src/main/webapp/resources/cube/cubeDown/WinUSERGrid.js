//zht 立方体下钻窗口
Ext.define('cube.cubeDown.WinUSERGrid', {
    extend: 'Ext.window.Window',
	title : '报表信息',
	modal : true,
    width: 800,
    height: 500,
	layout : 'fit',
    initComponent: function () {
        var me = this;
		var store = new Ext.data.Store({
			pageSize : 20,
			fields : [ {
				name : 'u_name'
			}, {
				name : 'u_description'
			},{
				name: 'u_creator_name',
			}],
			proxy : {
				type : 'ajax',
				url : Ext.ctxpath + '/olap/drillThrough.html',//查询下钻数据
				timeout : 100000,
				reader : {
					type : 'json',
					root : 'items',
					totalProperty : 'total'
				}
			},
			remoteFilter : true,
			autoLoad : true
		});
		var gridPanel = Ext.create('Ext.grid.Panel',{
			border : false,
			defaults : {
				width : 40
			},
			columns : [ {
				xtype : 'rownumberer',
				width : 50
			},  {
				header : '真实姓名',
				width : 70,
				dataIndex : 'u_name',
				style: {
					'text-align': 'center'
				}
			}, {
				header : '用户信息',
				width : 120,
				dataIndex : 'u_description',
				style: {
					'text-align': 'center'
				}
			}, {
				header : '创建人',
				align : 'center',
				layout : 'fit',
				dataIndex : 'u_creator_name'
			}],
			store : store,
			dockedItems : [ {
				xtype : 'pagingtoolbar',
				store : store,
				dock : 'bottom',
				displayInfo : true
			} ]
		})
		Ext.apply(me, {
		    closeAction: 'hide',
			layout : 'fit',
			items : [ gridPanel ]
		});
		var formatDate = function(value) {
			value = Ext.Date.format(value, 'Y-m-d');
			return value;
		}
		me.showInfo = function(reportid,mdx,ordinal,schema) {	
			store.filters.clear();
			store.currentPage=1;
			store.filter([{property:'reportid',value:reportid}
				,{property:'mdx',value:mdx}
				,{property:'schema',value:schema}
				,{property:'ordinal',value:ordinal}
			]);
			me.show();
		};
        me.callParent();
    }
});

