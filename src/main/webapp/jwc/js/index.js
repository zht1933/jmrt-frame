$(function(){
	$('#carousel').carousel({
		triggerType: 'mouseover',
		animateType:'fade',
		duration: 1000
	});
	$('.accordion_title, .tab_item').on('click',function(){
		$(this).addClass('active').siblings().removeClass('active');
	});
	$('.info_left li').on('click',function(){
		$(this).addClass('active').children('.content').slideDown();
		$(this).siblings().removeClass('active').children('.content').slideUp();
	})
})