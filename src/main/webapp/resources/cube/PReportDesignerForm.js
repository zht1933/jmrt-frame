Ext.define('jwapp.framework.PReportDesignerForm', {
	extend : 'Ext.window.Window',
	title : '报表信息',
	modal : true,
	width : 280,
	height : 150,
	layout : 'fit',
	initComponent : function() {
		var me = this, _funCallback;
		var formPanel = Ext.create('Ext.form.Panel', {
			align : 'center',
			layout : 'form',
//            url: '',
			autoHeight : true,
			border : false,
			autoHeight : true,
			buttonAlign : 'center',
			bodyStyle : "padding:10px 10px 10px 10px",
			items : [ {
				align : 'right',
				layout : 'column',
				border : false,
				columnWidth : .5,
				items : [ {
					name : 'id',
					xtype : 'hidden'
				},{
					name : 'mdx',
					xtype : 'hidden'
				},{
					name : 'userid',
					xtype : 'hidden',
					value : Ext.curuserid
				},{
					name : 'sumrow',
					xtype : 'hidden'
				},{
					name : 'sumcol',
					xtype : 'hidden'
				},{
					name : 'moduleseparate',
					xtype : 'hidden'
				},{
					xtype: 'textfield',
					name : 'name',
					fieldLabel : '报表名称', 
					allowBlank : false,
					labelAlign : "right",
					labelWidth : 80,
					width : 230,
					style : "margin-top: 5px;padding-top:5px"
				} ]
			} ],
            buttons: [{
                text: '保存',
                formBind: true, //only enabled once the form is valid
                disabled: true,
                handler: function() {
                    var form = this.up('form').getForm();
                 
                    if(this.up('form').getForm().getValues().moduleseparate){
                    	form.url = Ext.ctxpath+'/olap/saveReportSeparate.do';
                    }else{
                    	form.url = Ext.ctxpath+'/olap/saveReport.do';
                    }
                    if (form.isValid()) {
                        form.submit({
                            success: function(form, action) {
                            	if(_funCallback) _funCallback();
                            	me.close();
                            	Ext.Msg.alert('提示','保存成功!');	
                            },
                            failure: function(form, action) {
                                Ext.Msg.alert('Failed', action.result.msg);
                            }
                        });
                    }
                }
            }, {
	            text: '取消',
	            handler: function() {
	                me.close();
	            }
            }]
		});

		Ext.apply(me, {
			layout : 'fit',
			items : [ formPanel ],
			closeAction : 'hide'
		});
		me.showInfo = function(val, funCallback) {
			_funCallback = funCallback;
			formPanel.getForm().reset();
			formPanel.getForm().setValues(val);
			
			me.show();
		}
		me.callParent();
	}
});