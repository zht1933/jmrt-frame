//zht 立方体选择下拉列表
Ext.define('jwapp.framework.commons.CmbCube', {
	extend : 'Ext.form.field.ComboBox',
	initComponent : function() {
		var me = this;
		var store = new Ext.data.JsonStore({
			fields : [ {
				name : 'id'
			}, {
				name : 'name'
			}, {
				name : 'caption'
			} ],
			proxy : {
				type : 'ajax',
				//url : Ext.ctxpath + '/olap/getCubes.do',
				reader : {
					type : 'json',
					root : 'items',
					totalProperty : 'total'
				}
			}
		});
		Ext.apply(me, {
		    store: store,
			queryParam: 'queryparam',
			minChars: 2,
			triggerAction: 'all',
			valueField: 'id',
			displayField: 'caption',
			queryMode: 'remote',
			typeAhead: true,
			selectOnFocus: true,
			autoSelect: true,
			forceSelection: true,
	        listConfig : {
	            maxHeight : 800
	        }
		});
		me.callParent();
	}
});
