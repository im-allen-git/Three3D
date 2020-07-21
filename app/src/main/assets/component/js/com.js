window.onload = window.onresize = function() {

	var clientWidth = document.documentElement.clientWidth;

	document.getElementsByTagName("html")[0].style.fontSize =

		clientWidth / 750 * 100 + "px";
}

$(function() {
	// 开启弹窗
	$('.wrap').click(function(){
		$(this).siblings('.tc').show();
		return false;
	});
	// 取消设置, 关闭弹窗
	$('.tc  .close , .tc .cancel').click(function(){
		$(this).closest('.tc').hide();
		return false;
		// 阻止冒泡
		event.stopPropagation(); 
		//阻止默认事件 
		event.preventDefault() 
	});
	
	
	
	// 昵称修改后赋值
	$('.name_tc .sure').click(function(){
		var name_val = $('.name_val').val().trim();
		$('.name_text').text(name_val);
		$('.name_tc').hide();
		window.sessionStorage.setItem('userName',name_val);
	});
	// 清空修改的昵称
	$('.name_tc .del').click(function(){
		$('.name_tc .name_val').val('');
	});
	
	
	// edit sex 性别选择
	$('.sex_tc .sex').click(function() {
		$(this).addClass('active').siblings('span').removeClass('active');
	});
	// 修改性别后赋值
	$('.sex_tc .sure').click(function(){
		var sex_text = $('.sex_tc .sex.active').text().trim();
		$('.sex_text').text(sex_text);
		$('.sex_tc').hide();
		window.sessionStorage.setItem('sex',sex_text);
	});
	
	// 进餐人数赋值
	$('.num_tc .sure').click(function(){
		var num_val = $('.paramater .num_val').val().trim();
		$('.paramater .diner_num,.set .diner_num').text(num_val);
		$('.num_tc').hide();
		window.sessionStorage.setItem('personNum',num_val);
		
	});
	// 浪费比例赋值
	$('.waste_tc .sure').click(function(){
		$('.waste_tc').hide();
	});
	$('.set_wrap').click(function(){
		$('.paramater').show();
	})
	$('.back_set').click(function(){
		$('.paramater').hide();
	})
	
	
	// 系统生成二维码
	$('.erweima_wrap').click(function(){
		// 系统自动生成二维码
		url14 = $('.name_text').text(); //二维码内容（即该页面的路径）
		WeiXin(url14);
		$('.erweim_tc').show();
	});
	$('.close').click(function(){
		$('.erweim_tc').hide();
	});
	
})
// 系统生成二维码图片
//微信扫描二维码
function WeiXin(url14) {
	//每次先清空二维码容器
	$("#qrcode").html("");
	// var url4 = window.location.href;     //二维码内容（即该页面的路径）
	// url4 = 'A -- 测试二维码 -- B'; //二维码内容（即该页面的路径）
	var s = str2utf8(url14);

	/* 生成二维码 */
	$("#qrcode").qrcode({
		render: "canvas", //设置渲染方式
		width: 300, //设置宽度,默认生成的二维码大小是 256×256
		height:300, //设置高度
		typeNumber: -1, //计算模式
		background: "#ffffff", //背景颜色
		foreground: "#000", //前景颜色（粉色）
		correctLevel: 0,
		text: s //设置二维码内容
	});
}



// UCS-2 编码转 UTF-8 编码，防止中文乱码
function str2utf8(str) {
	// UCS-2和UTF8都是unicode的一种编码方式
	// js代码中使用的是UCS-2编码

	var code;
	var utf = "";

	for (var i = 0; i < str.length; i++) {
		code = str.charCodeAt(i); //返回每个字符的Unicode 编码

		if (code < 0x0080) {
			utf += str.charAt(i); //返回指定位置的字符
		} else if (code < 0x0800) {
			utf += String.fromCharCode(0xC0 | ((code >> 6) & 0x1F));
			utf += String.fromCharCode(0x80 | ((code >> 0) & 0x3F));
		} else if (code < 0x10000) {
			utf += String.fromCharCode(0xE0 | ((code >> 12) & 0x0F));
			utf += String.fromCharCode(0x80 | ((code >> 6) & 0x3F));
			utf += String.fromCharCode(0x80 | ((code >> 0) & 0x3F));
		} else {
			throw "不是UCS-2字符集"
		}

	}
	return utf;
}

