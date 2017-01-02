//zht 立方体树形列表
Ext.define('jwapp.framework.commons.TreeCube', {
	extend : 'Ext.tree.Panel',
	useArrows : true,
	initComponent : function() {
		var me = this;
		var store = Ext.create('Ext.data.TreeStore', {
			fields : [ {
				name : 'id'
			}, {
				name : 'caption'
			},{
				name : 'type'
			}, {
				name : 'name'
			}, {
				name : 'hie'
			}, {
				name : 'dim'
			}, {
				name : 'members'
			}, {
				name : 'leaf'
			}, {
				name : 'children'
			} ],
			proxy : {
				type : 'ajax',
				url : Ext.ctxpath + '/olap/getCubeDatas.do'
			},
			autoLoad: false
		});
		Ext.apply(me, {
			store: store,
			rootVisible : false,
			displayField : 'caption'
		});
		me.loadData = function(cubeid) {
			store.load({params:{cubeid:cubeid}});
		};
		me.callParent();
	}
});