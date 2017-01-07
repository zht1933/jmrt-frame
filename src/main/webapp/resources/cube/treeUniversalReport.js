Ext.define('jwapp.framework.commons.treeUniversalReport', {
	extend : 'Ext.tree.Panel',
	useArrows : true,
	initComponent : function() {
		var me = this;
		
		var store = Ext.create('Ext.data.TreeStore', {
			fields : [ {
				name : 'id'
			}, {
				name : 'name'
			}, {
				name : 'sumcol'
			}, {
				name : 'sumrow'
			}, {
				name : 'moduleseparate'
			}, {
				name : 'parentid'
			}, {
				name : 'seq'
			} ],
			nodeParam: 'id',
			root : {
				name : '报表列表',
				seq: '.0.',
				id : me.id
			},
			proxy : {
				type : 'ajax',
				url : Ext.ctxpath + '/olap/getUniversalReports.html',
				reader : {
					type : 'json',
					root : 'items',
					orglevel : 0,
					totalProperty : 'total',
				}
			}
		});
		Ext.apply(me, {
			title: '报表列表',
			store: store,			
			rootVisible : false,
			displayField : 'name',
		});
		
		    me.callParent();
	}
});