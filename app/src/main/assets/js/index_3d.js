var localStlParam ={};
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
        var X = $(".swiper-slide-active").find(".X").val();
        var Y = $(".swiper-slide-active").find(".Y").val();
        var Z = $(".swiper-slide-active").find(".Z").val();
        var material = $(".swiper-slide-active").find(".material").val();
        var size = $(".swiper-slide-active").find(".size").val();
        $("#XYZ").text(X + " " + Y + " " + Z)
        $("#moduleSize").text(size)
        $("#useMaterial").text(material)
        $("#printDuration").text()
		$( ".module_param,.module_param_bg" ).show();
        $(".outer_printbtn").hide();
	} else {
		$( ".module_param,.module_param_bg" ).hide();
		 $(".outer_printbtn").show();
	}
}

function getDefaultStl(){
	var localStl = js.getLocalStl();
	console.log(localStl)
    var data = eval('('+localStl+')')
	var slideHtml = ''
	for(var i in data){
		slideHtml += '<div class="swiper-slide"><div class="img_wrapper">'
		slideHtml += '<img src="'+data[i].localImg+'" alt="">'
		slideHtml += '<input type="hidden" class="X" value="'+data[i].length+'">'
		slideHtml += '<input type="hidden" class="Y" value="'+data[i].width+'">'
		slideHtml += '<input type="hidden" class="Z" value="'+data[i].height+'">'
		slideHtml += '<input type="hidden" class="material" value="'+data[i].material+'">'
		slideHtml += '<input type="hidden" class="size" value="'+data[i].size+'">'
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
