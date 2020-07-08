var localStlParam =[];
$( function () {
	getDefaultStl();
} );

function goPage( type ) {
	switch (type) {
		case 1:
			js.changeActive( "1" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
			break;

		case 4:
			js.changeActive( "4" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
			break;

		case 6:
			js.changeActive( "6" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页 6 index页面
			break;
	    case 7:
			js.changeActive( "7" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页 6 index页面 7 3d打印机 状态页 status
			break;
	}
}

function thisParamInfo( type ) {
	if (type == 0) {
		$( ".module_param,.module_param_bg" ).show();
        $(".outer_printbtn").hide();
	} else {
		$( ".module_param,.module_param_bg" ).hide();
		 $(".outer_printbtn").show();
	}
}

function getDefaultStl(){
	var data = js.getLocalStl();
	var slideHtml = ''
	for(var i in data){
		slideHtml += '<div class="swiper-slide"><div class="img_wrapper">'
		slideHtml += '<img src="../models/stl/localModules/hello_kitty.jpg" alt="">'
		slideHtml += '</div></div>'
	}
	$(".swiper-wrapper").html(slideHtml);
	var swiper = new Swiper( '.swiper-container', {
		spaceBetween: 0,
		freeMode: false,
		freeModeSticky: true,
		direction: 'vertical',
		navigation: {
			nextEl: '.swiper-button-next',
			prevEl: '.swiper-button-prev',
		},

	} );
}
