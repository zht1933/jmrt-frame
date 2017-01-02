/*
 * @author guojun.feng
 * @date 2016.12.23
*/
;(function($,window,document,undefined) {
	function Carousel(ele,options){
		this.element = ele;
		this.defaults = {
			/* 节点绑定 */
            navBoxCls: 'navBox',       			// 轮播内容列表的class
            navBtnCls: 'navBtn',           		// 轮播导航列表的class
            prevBtnCls: 'prevBtn',         		// 向前一步的class
            nextBtnCls: 'nextBtn',         		// 向后一步的class		
     		btnPos: 'center',					// 导航按钮位置
            /* 动画相关 */	
            triggerType: 'click',       		// 导航触发事件:"mouse"表鼠标移入时触发,"click"表示鼠标点击时触发
            steps: 1,                   		// 移动帧数,'auto'自动移动至下个没有显示完整的帧
            direction: 'x',             		// 轮播的方向
            inEndEffect: 'switch',      		// "switch"表示来回切换,"cycle"表示循环,"none"表示无效果
            activeIndex: 0,            	 		// 默认选中帧的索引
            auto: true,                			// 是否自动播放
            immediately: false,         		// 悬浮是否立即停止
            animate: true,              		// 是否使用动画滑动
            animateType:'move',          		// 切换时的动画效果
            delay: 3000,                		// 自动播放时停顿的时间间隔
            duration: 500              			// 轮播的动画时长
		};
		this.options = $.extend({},this.defaults,options);
	};
	Carousel.prototype = {
		_init: function(){

			var _this = this,
				$this = _this.element,
				index = _this.options.activeIndex,
				length = $this.children('li').length,
				navBox = $('<div class="'+ _this.options.navBoxCls +'"></div>');

			$this.data('eleOptions',{});
			$this.data('eleOptions').curIndex = index;

			$this.parent().css('position','relative');
			// 克隆第一张图片复制到列表最后
            if(_this.options.animateType == 'move'){ $this.children('li').eq(0).clone(true).appendTo($this); }
			// 创建导航按钮
			for(var i = 0; i < length;i++){

				var navBtn = $('<a href:"javascript:;" class="'+ _this.options.navBtnCls +'"></a>');

				// 向导航按钮添加事件
				navBtn.on(_this.options.triggerType,function(){
					$(this).addClass('active').siblings().removeClass('active');
				})

				i == 0 && navBtn.addClass('active');

				// 添加点击事件
				_this._click(navBtn);
				navBox.append(navBtn);
			}

			if(_this.options.animateType == 'move'){
				// 创建左右移动按钮
				var prev = $('<div class="btn btn_l">&lt;</div>').on('click',function(){_this._prev()});
	        	var next = $('<div class="btn btn_r">&gt;</div>').on('click',function(){_this._next()});
	        	$this.parent().append(prev).append(next);
			}

			
			// 添加导航按钮组
			$this.append(navBox);

			if(_this.options.btnPos == 'center'){
				navBox.css('margin-left','-'+navBox.outerWidth()/2+'px');
			}

			$this.data('eleOptions').navBox = navBox;

			// 开始轮播
			_this._start();
			
			return $this;

		},
		_start: function(){
			var _this = this,
			 	$this = _this.element,
			 	looper = setInterval(function(){
			 		$this.data('eleOptions').curIndex++;
			 		if(_this.options.animateType == 'move'){
			 			_this._move($this.data('eleOptions').curIndex);
			 		}
			 		else{
			 			_this._fade($this.data('eleOptions').curIndex);
			 		}
			 	},_this.options.delay);	// 开始定时器
			
			$this.data('eleOptions').looper = looper;
		},
		_stop: function(){
			var _this = this;
			var $this = _this.element;
			var looper = $this.data('eleOptions').looper;
			clearInterval(looper);
		},
		_click: function(obj){
			var _this = this,
				$this = _this.element,
				animateType = _this.options.animateType;

			obj.on(_this.options.triggerType,function(){	

				var index = $(this).index();
				obj.addClass('active').siblings().removeClass('active');
				// 轮播
				switch(animateType){
					case 'move': 
						_this._move(index)
					break;
					case 'fade': 
						_this._fade(index)
					break;
					default:
						_this._fade(index)
					break;
				}
			})
		},
		_move: function(index){

			var _this = this,
				$this = _this.element,
				widthAccord = $this.children('li').width(),
				length = $this.children('li').length,
				navBox = $this.data('eleOptions').navBox;
			
			var i = $this.data('eleOptions').curIndex = index;
		 	if (i == length) {
                $this.css({ 'margin-left': 0 });
                i = $this.data('eleOptions').curIndex = 1;
            }
            if (i == -1) {
                $this.css({ 'margin-left': -(length - 1) * widthAccord });
                i = $this.data('eleOptions').curIndex = length - 2;
            }

            $this.stop().animate({ 'margin-left': -i * widthAccord }, _this.options.duration);

            if (i == length - 1) {
            	navBox.children(_this.options.navBtn).eq(0).addClass('active').siblings().removeClass('active');
            } else {
            	navBox.children(_this.options.navBtn).eq(i).addClass('active').siblings().removeClass('active');
            }

		},
		_fade: function(index){
			var _this = this,
			    $this = _this.element,
			    length = $this.children('li').length,
			    navBox = $this.data('eleOptions').navBox,
			    i = $this.data('eleOptions').curIndex = index;

			if (i == length) {
                i = $this.data('eleOptions').curIndex = 0;
            }

			$this.children('li').fadeOut(_this.options.duration).eq(i).fadeIn(_this.options.duration);
			navBox.children(_this.options.navBtn).eq(i).addClass('active').siblings().removeClass('active');
		},
		_prev: function(){
			var _this = this;
			$this = _this.element;
            /*向右按钮*/
            $this.data('eleOptions').curIndex--;
            _this._move($this.data('eleOptions').curIndex);
		},
		_next: function(){
			var _this = this;
			$this = _this.element;
			/*向左按钮*/
            $this.data('eleOptions').curIndex++;
            _this._move($this.data('eleOptions').curIndex);
		}
	};
	$.fn.carousel = function(options,param){
		if(typeof(arguments[0]) == 'string'){
			return (new Carousel(this))['_'+arguments[0]](param);
		}
		else {
			return (new Carousel(this,options))._init();
		}
	}
})($,window,document)


