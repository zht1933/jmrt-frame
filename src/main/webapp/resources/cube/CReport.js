//zht 立方体表格
Ext.define('jwapp.framework.CReport', {
	extend : 'Ext.Component',
	initComponent : function() {
		var me = this;
		me.loadData = function(id, mdx, sumCol, sumRow, isChart,moduleseparate){
			if(!isChart)
				isChart = false;
//			Jjm.system.startprocess();//zht 开始系统处理
			var url;
			if(moduleseparate == null || moduleseparate == undefined){
				 url= Ext.ctxpath + '/olap/reportHtml.html';//zht 获取立方体分析的表格数据
            }else{
            	url= Ext.ctxpath + '/olap/reportHtmlSeparate.html?schema='+moduleseparate;
            }
			Ext.Ajax.request({
				url : url,
			    loadMask: 'loading...',
			    params: {
			    	reportid: id,
			    	mdx: mdx,
			    	sumCol: sumCol,
			    	sumRow: sumRow,
			    	isChart: isChart
			    },
			    timeout: 300000,
			    success: function(response){
			        var text = response.responseText;
			        me.update(text, true, function(){
						drillThrough = function(ordinal){
							var win = Ext.create(getDrillThroughWindow());
//							var win = Ext.create('cube.cubeDown.WinUSERGrid');
//							alert(win)
							win.showInfo(id, mdx, ordinal);
						}
						if(isChart){
				         	var myChart = echarts.init(me.getEl().dom, 'shine');
					        if (echartOption) {  
					        	myChart.setOption(echartOption, true);  
					        }
				        }
			        });
//			        Jjm.system.endprocess();//zht 结束系统处理
			    }
			});
		}
		me.callParent();
	}
})