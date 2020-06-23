var selectedSlideObj, parentsObj,this_obj;
var touchStartX, touchStartY; //点击目标的xy坐标
var movedDir; //移动的距离x
var DLETET_LEFT = 83;
var Xflag =false;
var Yflag =false;
$( function () {
	getLocalAppSTL();
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

function thisParamInfo( type ) {
	if (type == 0) {
		$( ".module_param,.module_param_bg" ).show();
	} else {
		$( ".module_param,.module_param_bg" ).hide();
	}
}
function getLocalAppSTL(){
	var data = js.getStlList() || null;
	console.log(data)
	var stlListHTML = '';
	if(data) {
		var stlList = eval('('+data+')');
		for (var i in stlList) {
			stlListHTML += '<div class="each_module "><div class="each_module_wrapper clearfix swiper-container"><div class="swiper-wrapper">';
			stlListHTML += '<div class="swiper-slide">';
			stlListHTML += '<div class="col-xs-3"><img src="'+stlList[i].localImg+'" alt=""></div>';
			stlListHTML += '<div class="col-xs-6"><div class="row clearfix">';
			var name  =stlList[i].sourceStlName.split(".stl")[0];
			stlListHTML += '<div class="col-xs-12 module_name">'+name+'</div>';
			stlListHTML += '<div class="col-xs-12 module_time"><div class="info">创建时间: <span class="this_createTime">'+stlList[i].createTime+'</span></div></div>';
			stlListHTML += '<div class="col-xs-12 module_size"><div class="info">打印尺寸(mm): <span class="this_createTime">X:15 Y:25 Z:30</span></div></div>';
			stlListHTML += '</div></div>';
			stlListHTML += '<div class="col-xs-3" onclick="thisParamInfo(0,this)"><div class="img_wrapper showHide first_child"><img src="../img/3dPrinting/btn_print.png" alt=""></div></div>';
			stlListHTML += '</div>';
			stlListHTML += '<div class="swiper-slide delete_slide" onclick="thisParamInfo(0,this)"><div class="delete">删除</div></div>';
			stlListHTML += '</div></div></div>';
		}
	}
	else{
		stlListHTML+='<div class="no_module">您还没有创建模型哦，<span onclick=" goPage(4) ">点击这里创建模型</span></div>'
	}
	$(".mine_content").html(stlListHTML)
	var swiper = new Swiper('.swiper-container', {
    		slidesPerView: 'auto',
    		spaceBetween: 0,
    		freeMode: false,
    		freeModeSticky : true,
    		resistance:true,
    	});

}