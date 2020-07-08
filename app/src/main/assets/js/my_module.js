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
	var stlListHTML = '';
	if(data) {
		var stlList = eval('('+data+')');
		for (var i in stlList) {
			stlListHTML += '<div class="each_module "><div class="each_module_wrapper clearfix swiper-container"><div class="swiper-wrapper">';
            stlListHTML += '<div class="swiper-slide">';
            stlListHTML += '<div class="col-xs-3"><img src="'+stlList[i].localImg+'" alt=""></div>';
            stlListHTML += '<div class="col-xs-9">';
            var name  =stlList[i].sourceStlName.split(".stl")[0];
            stlListHTML += '<div class="module_name">'+name+'</div>';
            stlListHTML += '<div class="module_time"><div class="info">创建时间: <span class="this_createTime">'+stlList[i].createTime+'</span></div></div>';
            stlListHTML += '<div class="module_size"><div class="info">打印尺寸(mm): <span class="this_createTime">X:15 Y:25 Z:30</span></div></div>';
            stlListHTML += '<div class="img_wrapper showHide first_child"><img src="../img/3dPrinting/btn_print.png" alt=""></div>';
            stlListHTML += '</div></div>';
            stlListHTML += '<div class="swiper-slide delete_slide" onclick="deleteThisModule(this,\''+stlList[i].realStlName+'\')"><div class="delete">删除</div></div>';
            stlListHTML += '</div></div></div>';
		}
	}
	else{
		stlListHTML+='<div class="no_module">您还没有创建模型哦<br><span onclick=" goPage(4) ">点击这里创建模型</span></div>'
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

function deleteThisModule(obj,name){
	var e = event || window.event || arguments.callee.caller.arguments[0];
	if ( e && e.stopPropagation ){
		e.stopPropagation();
	}else{ //ie
		window.event.cancelBubble = true;
	}
    $.dialog({
        type: 'confirm',
        showTitle: false,
        overlayClose:true,
        contentHtml: '<p class="red_note" style="word-break: break-word;">Are you sure you want to delete this module?</p>',
        buttonText : {//按钮文本内容
            ok : 'OK',
            cancel:'Cancel'
        },
        buttonClass : {
            ok:'removeThis_ok',
            cancel:'removeThis_cancel'
        },
        onClickOk : function(){
            var allModule = $(obj).parents(".module_content");
            var allModuleLength = $(obj).parents(".module_content").find(".each_module");
            var eachModule = $(obj).parents(".each_module");
            $("#loading_data").show();
            var deletedSuccFlag = js.deleteStl(name);
            if(deletedSuccFlag){
                if(allModuleLength.length>1){
                    eachModule.remove();
                }
                else{
                    if(allModule.hasClass("mine_content")){
                        var stlListHTML='<div class="no_module">您还没有创建模型哦<br><span onclick=" goPage(4) ">点击这里创建模型</span></div>'
                        $(".mine_content").html(stlListHTML);
                    } else if(allModule.hasClass("mine_content")){
                        var stlListHTML='<div class="no_module">您还没有购买哦，<span onclick=" goPage(2) ">点击这里浏览</span></div>'
                        $(".bought_content").html(stlListHTML);
                    }
                    else if(allModule.hasClass("local_content")){
                        var stlListHTML='<div class="no_module">您还没有本地模型哦</div>'
                        $(".bought_content").html(stlListHTML);
                    }
                }
            }
            else{
                $(".note_error").show();
                setTimeout(function(){
                    $(".note_error").hide();
                },1500)
            }
            $("#loading_data").hide();
        },
        onClickCancel : function(){

        }
    });

}
