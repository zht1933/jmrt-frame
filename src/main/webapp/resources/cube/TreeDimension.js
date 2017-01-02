//		zht 维度展示树
Ext.define('jwapp.framework.commons.TreeDimension', {
	extend : 'Ext.tree.Panel',
	initComponent : function() {
		var me = this;
	    var store = new Ext.data.TreeStore({
			fields : [ {
				name : 'id'
			}, {
				name : 'caption'
			},{
				name : 'type'
			}, {
				name : 'name'
			}, {
				name : 'leaf'
			}, {
				name : 'hie'
			}, {
				name : 'dim'
			}, {
				name : 'members'
			}, {
				name : 'children'
			} ],
	        root: {
	        	caption: '已选'
	        }
	    });
		Ext.apply(me, {
			store: store,
			rootVisible : true,
			displayField : 'caption',
		});
		me.callParent();
	}
})