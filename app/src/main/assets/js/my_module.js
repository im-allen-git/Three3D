var selectedSlideObj, parentsObj,this_obj;
var touchStartX, touchStartY; //点击目标的xy坐标
var movedDir; //移动的距离x
var DLETET_LEFT = 83;
var Xflag =false;
var Yflag =false;
$( function () {
	var swiper = new Swiper('.swiper-container', {
		slidesPerView: 'auto',
		spaceBetween: 0,
		freeMode: false,
		freeModeSticky : true,
		resistance:true,
	});

	var contentModule = document.getElementById( "contentModule" );

} );
function goPage(type) {//type 1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
	if(type==1) {
		js.changeActive( "1" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
	}
	else if(type==2) {
		js.changeActive( "2" );
	}
	else if(type==3) {
		js.changeActive( "3" );
	}
	else if(type==4) {
		js.changeActive( "4" );
	}
}
function showCurrentModule(type){
	$(".swiper-wrapper").each(function(){
		$(this)[0].style = '';
	})
	$(".active_menu").removeClass("active_menu");
	switch (type) {
		case 0:
			$(".mine .sec_btn").addClass("active_menu");
			$(".mine_content").show();
			$(".bought_content,.local_content").hide();
			break;
		case 1:
			$(".bought .sec_btn").addClass("active_menu");
			$(".bought_content").show();
			$(".mine_content,.local_content").hide();
			break;
		case 2:
			$(".local .sec_btn").addClass("active_menu");
			$(".local_content").show();
			$(".mine_content,.bought_content").hide();
			break;
	}
	$("#contentModule").html();
}
