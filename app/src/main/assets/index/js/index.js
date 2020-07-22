
// cookie
// param name : 表示cookie的名称，必填
// param subName : 表示子cookie的名称，必填
// param value : 表示子cookie的值，必填
// param expires : 表示cookie的过期时间，可以不填
// param domain : 表示cookie的域名，可以不填
// param path : 表示cookie的路径，可以不填
// param secure : 表示cookie的安全标志，可以不填
var cookieObject= {
    setCookie: function(name, value, expiry, path, domain, secure) {
        var nameString = "ck_" + name + "=" + value;
        var expiryString = "";
        if(expiry != null) {
            try {
                expiryString = "; expires=" + expiry.toUTCString();
            }
            catch(e) {
                if(expiry) {
                    var lsd = new Date();
                    lsd.setTime(lsd.getTime() + expiry * 1000);
                    expiryString = "; expires=" + lsd.toUTCString();
                }
            }
        } else {
            var ltm = new Date();
            expiryString = "; expires=" + ltm.toUTCString();
        }
        var pathString = (path == null) ? " ;path=/" : " ;path = " + path;
        var domainString = (domain == null) ? "" : " ;domain = " + domain;
        var secureString = (secure) ? ";secure=" : "";
        document.cookie = nameString + expiryString + pathString + domainString + secureString;
    },
    getCookie: function(name) {
        var i, aname, value, ARRcookies = document.cookie.split(";");
        for(i = 0; i < ARRcookies.length; i++) {
            aname = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
            value = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
            aname = aname.replace(/^\s+|\s+$/g, "");
            if(aname == "ck_" + name) {
                return(value);
            }
        }
        return '';
    },
    removeCookie:function(name){
        this.setCookie(name,'',-1);
    }
};
var storageObject= {
    localGetItem: function(key) { //假如浏览器支持本地存储则从localStorage,sessionStorage，否则乖乖用Cookie
        return window.localStorage ? localStorage.getItem(key) : cookieObject.getCookie(key);
    },
    localSetItem: function(key, val) {
        return window.localStorage ? localStorage.setItem(key, val) : cookieObject.setCookie(key, val);
    },
    localRemoveItem:function(key){
        return window.localStorage ? localStorage.removeItem(key) : cookieObject.removeCookie(key);
    },
    sessionGetItem:function(key){
        return window.sessionStorage ? sessionStorage.getItem(key) : cookieObject.shopcarGetCookie(key);
    },
    sessionSetItem:function(key, val){
        return window.sessionStorage ? sessionStorage.setItem(key, val) : cookieObject.setCookie(key, val);
    },
    sessionRemoveItem:function(key){
        return window.sessionStorage ? sessionStorage.removeItem(key) : cookieObject.removeCookie(key);
    }
};

var goodsItemIndex = 0;  //大类别food_item添加序号,下面需要用到
/* 已进入首页就初始化查看是否有数据 */
var foodType = $('#foodType');  //顶部食品名称列表
var foodAmount = $('#foodAmount');  //顶部食品名称相对于的详细数据
var guideIntake = 0;  //某一个食物大类别的指导摄入量重量,在添加设备时获取
var weightUnit = '克';
var weightUnit1 = 'g';
init();

var foodTypeAll = '';  //存储食物大类别的具体数据,用@@分割,第一个数据为食物类别名,第二个数据为类别推荐指标(就是上面的指导摄入量重量guideIntake),第三个数据为食称重单位,第四个数据为绑定的设备名,第五个数据为今日摄入量,第六个为剩余总重量
window.onload = function(){
				
	/* 添加设备弹窗相关方法 */
	var addDeviceBtn = document.getElementById('addDeviceBtn');
	var addDevice = document.getElementById('addDevice');
	var addDeviceBg = document.getElementById('addDeviceBg');
	var addDeviceClose = document.getElementById('addDeviceClose');
	addDeviceBtn.onclick = function(){
		addDevice.style.display = 'block';
	}
	addDeviceBg.onclick = function(){
		addDevice.style.display = 'none';
	}
	addDeviceClose.onclick = function(){
		addDevice.style.display = 'none';
	}
	/* 添加设备弹窗相关方法 end */
	/* 还无设备弹窗相关方法 */
	var deviceBox = document.getElementById('deviceBox');
	var deviceBg = document.getElementById('deviceBg');
	var deviceClose = document.getElementById('deviceClose');
	deviceBg.onclick = function(){
		deviceBox.style.display = 'none';
	}
	deviceClose.onclick = function(){
		deviceBox.style.display = 'none';
	}
	/* 还无设备弹窗相关方法 end */
	
	/* 系统提示弹窗相关方法 */
	var systemHint = document.getElementById('systemHint');
	var systemHintBg = document.getElementById('systemHintBg');
	var systemHintClose = document.getElementById('systemHintClose');
	systemHintBg.onclick = function(){
		systemHint.style.display = 'none';
	}
	systemHintClose.onclick = function(){
		systemHint.style.display = 'none';
	}
}

$(function(){
    let userId = js.getUserId('userId');
    console.log(userId);
    if(userId){
        let userInfo = js.getUserInfoDataList(userId);
         console.log(userInfo[0]);
    }



	let personNum = storageObject.sessionGetItem('personNum');
	let wasteNum = storageObject.sessionGetItem('wasteNum');
	let userName = storageObject.sessionGetItem('userName');
	let groupPhone = storageObject.sessionGetItem('groupPhone');
	/* userName = 'wuhong'; */
	if(!personNum){  //session参数不存在,表示第一次进入app,显示系统设置提示弹窗
		// $('#systemHint').show();
		/* $('.ingest_href').removeAttr('href'); */ //第一次进入app时.不能点击查看详情数据
	}
	var switchTop = $('.switch_top');
	if(userName){
		switchTop.find('#myname').text(userName).end().siblings('.switch_users').children('li').eq(0).text(userName);
	}
	if(groupPhone){ //没有群组共享账号信息groupPhone的session值,就不显示下拉箭头
		switchTop.children('.iconfont').show();
		if(groupPhone.indexOf('&') > -1){ //群组共享账号信息有多个的情况
			let groupPhoneArr = groupPhone.split('&');
			for(let i=0;i<groupPhoneArr.length;i++){
				let switchItemFirst = switchTop.siblings('.switch_users').find('.switch_item').eq(0).clone(true);
				switchTop.siblings('.switch_users').append(switchItemFirst).find('.switch_item:last').text(groupPhoneArr[i]);
			}
		}else{ //群组共享账号信息只有一个
			let switchItemFirst1 = switchTop.siblings('.switch_users').find('.switch_item').eq(0).clone(true);
			switchTop.siblings('.switch_users').append(switchItemFirst1).find('.switch_item:last').text(groupPhone);
		}
		//有群组共享账号信息groupPhone的session值,显示下拉箭头,添加点击方法
		switchTop.on('click',function(){
			switchTop.siblings('#switchBg').show().siblings('.switch_users').show();
		})
		$('#switchBg').on('click',function(){
			switchTop.siblings('#switchBg').hide().siblings('.switch_users').hide();
		})
		var testWeight = 0;
		$('.switch_item').on('click',function(){
			switchTop.children('#myname').text($(this).text()).end().siblings('#switchBg').hide().siblings('.switch_users').hide();
			
			/* 下面方法就是为了展示一个切换的效果 */
			testWeight = testWeight +10;
			ingestDataMethod(testWeight,testWeight+100);
		})
	}

	
	
	/* 点击加号按钮新增食物大类别 */
	/* $('#addDeviceBtn1').on('click',function(){
		$('#addDevice').show();
	}) */
	
	$('#addDeviceBtn1').click(function(){
		$('#addDevice').show();
	});


	/* 下一步方法 */
	$('#addDeviceNext1').on('click',function(){
		$('#addDevice').hide();
		$('#deviceBox').show();
		foodTypeAll = ''; //存储新的数据之前，将全局变量先清空
		let typeThat = $(this).parents('.add_dev_one');
		let addTypeName = typeThat.find('#addType1').children('.type1_active').text();
		let addTypeUnit = typeThat.find('#addType2').children('.type2_active').text();
		let addTypeNum = typeThat.find('.recommend_bun').val();
		/* 单位赋值 */
		guideIntake = addTypeNum; //某一个食物大类别的指导摄入量重量
		weightUnit = addTypeUnit;
		if(addTypeUnit == '克'){
			weightUnit1 = 'g';
		}else{
			weightUnit1 = 'ml';
		}
		/* 存储设置的数据 */
		foodTypeAll = addTypeName + '@@' + addTypeNum + '@@' + addTypeUnit;
	})
	$('#addDeviceNext2').on('click',function(){
		
		
		
		let foodTypeItem = foodType.children('li');
		let foodItemArr = foodTypeAll.split('@@');
		if(foodTypeItem && foodTypeItem.length > 0){ //已有数据
			foodType.find('.food_item').removeClass('food_active');
			foodType.css('display','inline-block').append('<li class="food_item food_active">'+foodItemArr[0]+'</li>');
		}else{  //没有数据
			foodType.css('display','inline-block').append('<li class="food_item food_active">'+foodItemArr[0]+'</li>');
		}
		let amountItem = foodAmount.find('.amount_item');
		let addDeviceBtn = amountItem.find('#addDeviceBtn');  //页面中间的新增按钮

		if(addDeviceBtn.is(':visible')){  //如果页面中间的新增按钮显示,说明刚进入首页.只有一条数据,就是欢迎页的空数据,需要添加数据
			let derviceName = $.trim($(this).parents('.add_dev_two').find('.add_txt4').text());
			amountItem.find('.index_con_box').show().find('.detect_num1').text(foodItemArr[0]).end().find('.detect_num3').text(derviceName);
			addDeviceBtn.hide();
		}else{ //如果页面中间的新增按钮不显示,就说明已经执行过下面的remove方法.此时超过一条数据,要克隆添加数据
			foodAmount.append(amountItem.eq(0).clone(true)); //克隆第一条数据,插入到最后
			amountItem.hide();
			let NowAmountItemLength  = $('#foodAmount>.amount_item').length; //插入克隆数据后总数据的长度,$('#foodAmount>.amount_item')不能替换为amountItem,因为amountItem获取的没有克隆前的数据
			$('#foodAmount>.amount_item').eq(NowAmountItemLength-1).removeClass('amount_item1').addClass('amount_item'+NowAmountItemLength).show().find('.detect_num1').text(foodItemArr[0]).end().find('.detect_num3').text(foodItemArr[3]);
		


		}
		let lanyaWeightVal = []; //蓝牙传的数据数组
		$('#lanyaList .str_lanya').each(function(){
			let _that = $(this);
			let _thatItemVal = _that.val();
			lanyaWeightVal.push(_thatItemVal);
		})
		/* lanyaWeightVal = ['24','120']; */
		// if(!lanyaWeightVal[1]){ //如果当前剩余数据不存在,默认150g
		// 	lanyaWeightVal[1] = 150;
		// }
		// ingestDataMethod(parseInt(lanyaWeightVal[0]),parseInt(lanyaWeightVal[1]));
		/* 下面添加的是测试数据 */
		if(lanyaWeightVal && lanyaWeightVal.length>0){
			let amountItemNew = foodAmount.find('.amount_item'); //clone后新的列表
			let ingestRate = 0; //蓝牙获取重量和指导摄入重量的比率
			let ingestRate1 = 0; //ingest_num位置
			let ingestRate2 = 0; //ingest_bg_new高度
			if(lanyaWeightVal[0]<parseInt(guideIntake) && lanyaWeightVal[0]>0){ //guideIntake为指导摄入量数据
				ingestRate = Math.round((parseInt(lanyaWeightVal[0])/guideIntake)*100); //计算摄入重量比率(四舍五入),
				if(ingestRate < 8){ //ingest_bg_new高度最大不能大于92%
					ingestRate2 = 92;
					ingestRate1 = 4;
				}else{
					ingestRate2 = 100 - ingestRate;
					ingestRate1 = ingestRate-4;  //数字位置比ingest_bg_new少4%
				}
				
			}else if(lanyaWeightVal[0] == 0){
				ingestRate2 = 100;
				ingestRate1 = 0;
			}else{
				ingestRate2 = 0;
				ingestRate1 = 96;  //数字位置比ingest_bg_new少4%
			}
			
			amountItemNew.eq(amountItemNew.length-1).find('.ingest_today1 .shi_num').text(lanyaWeightVal[0]);
			amountItemNew.eq(amountItemNew.length-1).find('.ingest_num').find('.shi_num').text(lanyaWeightVal[0]);
			amountItemNew.eq(amountItemNew.length-1).find('.ingest_num').animate({bottom:ingestRate1+'%'},1000);
			if(parseInt(lanyaWeightVal[0])<parseInt(guideIntake)){ //guideIntake位置指导摄入量重量
				amountItemNew.eq(amountItemNew.length-1).removeClass('index_exceed');
				amountItemNew.eq(amountItemNew.length-1).find('.ingest_today2').text('指导摄入量还剩');
				amountItemNew.eq(amountItemNew.length-1).find('.remain_weight .shi_num').text(Math.abs(guideIntake-parseInt(lanyaWeightVal[0])));
			}else{ //超出指导量
				amountItemNew.eq(amountItemNew.length-1).addClass('index_exceed');
				amountItemNew.eq(amountItemNew.length-1).find('.ingest_today2').text('超过最大摄入量');
				amountItemNew.eq(amountItemNew.length-1).find('.remain_weight .shi_num').text(Math.abs(guideIntake-parseInt(lanyaWeightVal[0])));
			}
			amountItemNew.eq(amountItemNew.length-1).find('.ingest_bg').show();
			amountItemNew.eq(amountItemNew.length-1).find('.ingest_bg_new').show().animate({height: ingestRate2 +'%'},1000);
			if(!lanyaWeightVal[1]){ //如果当前剩余数据不存在,默认150g
				lanyaWeightVal[1] = 150;
			}
			amountItemNew.eq(amountItemNew.length-1).find('.detect_num2 .shi_num').text(parseInt(lanyaWeightVal[1]));
			
			/* 赋值重量单位 */
			amountItemNew.eq(amountItemNew.length-1).find('.shi_unit').text(foodItemArr[2]);
			if(foodItemArr[2] == '克'){
				amountItemNew.eq(amountItemNew.length-1).find('.shi_unit1').text('g');
			}else{
				amountItemNew.eq(amountItemNew.length-1).find('.shi_unit1').text('ml');
			}
		}else{
			amountItem.eq(0).find('.remain_weight .shi_num').text(guideIntake);
			/* 赋值重量单位 */
			amountItem.eq(0).find('.shi_unit').text(foodItemArr[2]);
			if(foodItemArr[2] == '克'){
				amountItem.eq(0).find('.shi_unit1').text('g');
			}else{
				amountItem.eq(0).find('.shi_unit1').text('ml');
			}
		}
		$('.ingest_href').attr('href','../index/detail.html'); //添加跳转连接
		foodTypeTab(); //tab切换方法
		$('#addDevice').hide().find('.add_dev_one').show().next('.add_dev_two').hide();
		storageObject.sessionSetItem('foodItemIndex',($('#foodAmount>.amount_item').length-1));

		

		/* 存储新加类别的详细数据到session */
		let newAddDeviceName = $('#equipmentsList').find('.equipments_active').siblings('.equipments_name').text();
		foodTypeAll = foodTypeAll + '@@' + newAddDeviceName + '@@' + parseInt(lanyaWeightVal[0]) + '@@' + parseInt(lanyaWeightVal[1]); //将设备名、今日摄入量、当前剩余总量数据拼接到这一条大类别总数据中
		let foodTypeVal = storageObject.sessionGetItem('foodTypeVal');
		if(foodTypeVal && foodTypeVal.length > 0){ //如果原本session里就有数据
			storageObject.sessionSetItem('foodTypeVal',foodTypeVal+'||'+foodTypeAll);
		}else{
			storageObject.sessionSetItem('foodTypeVal',foodTypeAll);
		}
	})
	$('#addDeviceNext3').on('click',function(){
		/* 这里先扫描二维码 */
		$('#deviceBox').hide();
		$('#confirmDevice').show();
	})
	$('#addDeviceNext4').on('click',function(){
		$('#newEquipments').hide();
		let addDeviceName = $(this).parents('.equipments_main').find('.equipments_active').siblings('.equipments_name').text();
		let foodTypeAllArr = foodTypeAll.split('@@');
		$('#addDevice').show().find('.add_dev_one').hide().next('.add_dev_two').show().find('.add_txt1').text(foodTypeAllArr[0]).end().find('.add_txt2').text(foodTypeAllArr[2]).end().find('.add_txt3').text(foodTypeAllArr[1]+foodTypeAllArr[2]+'/天').end().find('.add_txt4').text(addDeviceName);
	})
	
	
	
	/* 上一步方法 */
	$('#devicePrevious1').on('click',function(){
		$('#addDevice').hide();
	})
	$('#devicePrevious2').on('click',function(){
		$('#newEquipments').show();
		$('#addDevice').hide().find('.add_dev_one').show().next('.add_dev_two').hide();
		/* foodTypeAll = foodTypeAll.substr(0,foodTypeAll.lastIndexOf('@@'));  //添加设备点击上一步时,去掉foodTypeAll最后面已经添加的设备名称
		storageObject.sessionRemoveItem('foodTypeVal'); */
	})
	$('#devicePrevious3').on('click',function(){
		$('#addDevice').show();
		$('#deviceBox').hide();
	})
	$('#devicePrevious4').on('click',function(){
		$('#confirmDevice').show();
		$('#newEquipments').hide();
	})


	/* 添加设备弹窗相关方法 */
	$('#addType1 li').click(function(){
		let thatType = $(this);
		if(thatType.hasClass('type1_customize')){
			$('#customizeProject').show();
		}else{
			thatType.addClass('type1_active').siblings().removeClass('type1_active');
		}
		
	})
	$('#addType2 li').click(function(){
		let thatType = $(this);
		thatType.addClass('type2_active').siblings().removeClass('type2_active');
	})
	
	/* 点击减按钮 */
	$('.recommend_decrease').click(function(){
		let thatPlus = $(this);
		let inputVal = parseInt(thatPlus.siblings('.recommend_bun').val());
		if(inputVal === 0){
			thatPlus.siblings('.recommend_bun').val(0);
		}else{
			thatPlus.siblings('.recommend_bun').val(inputVal-1);
		}
	})
	
	/* 点击加按钮 */
	$('.recommend_increase').click(function(){
		let thatPlus = $(this);
		let inputVal = parseInt(thatPlus.siblings('.recommend_bun').val());
		if(inputVal > 9998){
			thatPlus.siblings('.recommend_bun').val(9999);
		}else{
			thatPlus.siblings('.recommend_bun').val(inputVal+1);
		}
	})
	$('#customizeBg,#customizeClose').on('click',function(){
		$('#customizeProject').hide();
	})
	$('#customizeIcon').on('click',function(){
		$(this).siblings('#customizeName').val('').focus();
	})
	$('#customizeConfirm').on('click',function(){
		let _that = $(this);
		let customizeNameVal = _that.siblings('#customizeName').val();
		if(customizeNameVal.length >0){
			$('#customizeProject').hide();
			/* 插入输入的新的自定义项目名 */
			$('.type1_customize').siblings().removeClass('type1_active').end().before('<li class="type1_active">'+customizeNameVal+'</li>');
		}else{
			_that.addClass('customize_embar');
		}
	})
	$('#customizeName').on('focus',function(){
		$(this).siblings('#customizeConfirm').removeClass('customize_embar');
	})
	/* 添加设备弹窗相关方法 end */
	
	/* 添加新设备列表弹窗相关方法 start*/
	$('#device_bg,#newEquipmentsClose').on('click',function(){
		$('#newEquipments').hide();
	})
	$('#equipmentsList').on('click','.equipments_item',function(){
		let thatEqu = $(this);
		thatEqu.find('.equipments_check').addClass('equipments_active').end().siblings().find('.equipments_check').removeClass('equipments_active');
	})
	/* 添加新设备列表弹窗相关方法 end */


	/* 确认设备弹窗相关方法 */
	$('#confirmDeviceBack').on('click',function(){
		let thatType = $(this);
		thatType.parents('#confirmDevice').hide();
		$('#deviceBox').show();
	})
	// $('#confirmAddBtn').on('click',function(){
	// 	let thatType = $(this);
	// 	thatType.parents('#confirmDevice').hide();
	// 	$('#newEquipments').show().find('.equipments_item .equipments_check').removeClass('equipments_active');
	// 	// 展示设备列表
	// 	var deviceName = $('#confirm_name').text();
	// 	var li = '';
	// 	li += '<li class="equipments_item clearfix">'
	// 	li += '<span class="equipments_name">'+ deviceName +'</span><span class="left">左边</span><span class="equipments_function">未绑定</span>'
	// 	li += '<em class="equipments_check equipments_active"><i class="iconfont"></i></em>'	
	// 	li += '</li>'
	// 	li += '<li class="equipments_item clearfix">'
	// 	li += '<span class="equipments_name">'+ deviceName +'</span><span class="right">右边</span><span class="equipments_function">未绑定</span>'
	// 	li += '<em class="equipments_check equipments_active"><i class="iconfont"></i></em>'	
	// 	li += '</li>'	
	// 	$('#equipmentsList').append(li);
		
	// 	// 添加设备后读取蓝牙数据
		
	// 	var n = 0;
	// 	var timer = null;
	// 	timer = setInterval(function(){
	// 		n++;
			
	// 		readValue('FC:F5:C4:16:24:AA',true,'5FAFC201-1FB5-459E-8FCC-C5C9C331914B','5EB5483E-36E1-4688-B7F5-EA07361B26A8');
	// 		if(n > 5){
	// 			clearInterval(timer);
	// 		}
			
	// 	},500); 
		
		
	// })
	$('#confirmBackBtn').on('click',function(){
		let thatType = $(this);
		thatType.parents('#confirmDevice').hide();
		$('#deviceBox').show();
	})
	
	/* 确认设备弹窗相关方法 end */

	/* 系统提示弹窗相关方法 */
	$('#singleSlider').jRange({
		from: 0,//开始于
		to: 100,//结束于
		step: 1,//一次滑动多少
		format: '%s',//格式化格式
		width: ($('.waste_chart').width()/300)*300,//宽度,这里为了适应父元素的宽度，做了自适应的处理
		snap:false,  //是否只允许按增值选择(默认false)
		showLabels: true, //布尔型，是否显示滑动条下方的尺寸标签
		isRange : false //是否为选取方位
	});

	$('#mealsList .meals_item').click(function(){
		let _that = $(this);
		/* let index = _that.index()+1;
		let intervalWidth = index * ($('.waste_chart').width()/8); */
		_that.addClass('meals_active').siblings().removeClass('meals_active');
		/* $('.selected-bar').css('width',intervalWidth);
		$('.pointer').css('left',intervalWidth-9);
		$('.progress_tip').text(index).css('left',intervalWidth-5); */
	})

	$('#hintMainBottom').click(function(){ //设置了基础设置后保存到本地session里
		let _that = $(this);
		let systemMealsNum = _that.parents('.device_pop').find('.meals_active').text(); //人数
		let systemWasteRatio = _that.parents('.device_pop').find('#singleSlider').val(); //浪费比率
		storageObject.sessionSetItem('personNum',systemMealsNum);
		storageObject.sessionSetItem('wasteNum',systemWasteRatio);
		$('#systemHint').hide();
	})
	/* 系统提示弹窗相关方法 end */

	/* 系统链接方式开始 */
	$('#wife').click(function(){
		$('#device_link').hide();
		$('#wife_link').show();
		window.sessionStorage.setItem('device_link','wife');
	});
	$('.password_del').click(function(){
		$('.password_val').val('');
	});
	$('#systemLinkClose,.link_center .cancel').click(function(){
		$('#wife_link').hide();
		$('#device_link').show();
	});
	$('.link_center .sure').click(function(){
		$('#wife_link').hide();
		$('#device_link').hide();
		$('#device_tips').show()
		// 重启 app 
	});
	/* 系统链接方式结束 */
	
})

/* 顶部大类别tab切换 */
function foodTypeTab(){
	var foodType = document.querySelector('#foodType');
	var foodTypeList = foodType.querySelectorAll('li');
	var foodAmount = document.querySelector('#foodAmount');
	var foodAmountList = foodAmount.querySelectorAll('.amount_item');
	for(var i = 0; i < foodTypeList.length; i++){
		foodTypeList[i].onclick = (function (i) {
			return function () {
				goodsItemIndex = i; //全局变量,表示顶部食品大类别的序号
				storageObject.sessionSetItem('foodItemIndex',goodsItemIndex);
				addCurClass(foodTypeList,i,'food_active');
				addCurClass1(foodAmountList,i);
			}
		})(i);
	}
	
	/* tab切换class */
	function addCurClass(obj,index,className) {
		for(var i = 0; i < obj.length; i++){
			obj[i].classList.remove(className);
		}
		obj[index].classList.add(className);
	}
	/* tab切换显隐 */
	function addCurClass1(foodAmountList,index) {
		for(var i = 0; i < foodAmountList.length; i++){
			foodAmountList[i].style.display = 'none';
		}
		foodAmountList[index].style.display = 'block';
	}
}

/* 传入摄入量重量和剩余总重量,计算比例和动画效果 */
function ingestDataMethod(guideNum,ingestNum,overNum){ //guideNum为指导摄入量,ingestNum摄入量重量,overNum是剩余总量,
	if(guideNum && ingestNum && overNum){
		let amountItemNew = foodAmount.find('.amount_item'); //clone后新的列表
		let ingestRate = 0; //蓝牙获取重量和指导摄入重量的比率
		let ingestRate1 = 0; //ingest_num位置
		let ingestRate2 = 0; //ingest_bg_new高度
		if(ingestNum<parseInt(guideIntake) && ingestNum>0){ //guideIntake为指导摄入量数据
			ingestRate = Math.round((parseInt(ingestNum)/guideIntake)*100); //计算摄入重量比率(四舍五入),
			if(ingestRate < 8){ //ingest_bg_new高度最大不能大于92%
				ingestRate2 = 92;
				ingestRate1 = 4;
			}else{
				ingestRate2 = 100 - ingestRate;
				ingestRate1 = ingestRate-4;  //数字位置比ingest_bg_new少4%
			}
			
		}else if(ingestNum == 0){
			ingestRate2 = 100;
			ingestRate1 = 0;
		}else{
			ingestRate2 = 0;
			ingestRate1 = 96;  //数字位置比ingest_bg_new少4%
		}
		
		amountItemNew.eq(amountItemNew.length-1).find('.ingest_today1 .shi_num').text(ingestNum);
		amountItemNew.eq(amountItemNew.length-1).find('.ingest_num').find('.shi_num').text(ingestNum);

		/* amountItemNew.eq(amountItemNew.length-1).find('.ingest_num').animate({'bottom':ingestRate1+'%'},1000); */
		amountItemNew.eq(amountItemNew.length-1).find('.ingest_num').css('bottom',ingestRate1+'%');
		amountItemNew.eq(amountItemNew.length-1).find('.ingest_bg').show();
		amountItemNew.eq(amountItemNew.length-1).find('.ingest_bg_new').show().css('height',ingestRate2 +'%');
		/* amountItemNew.eq(amountItemNew.length-1).find('.ingest_bg_new').show().animate({'height': ingestRate2 +'%'},1000); */
		if(ingestNum < guideNum){  //今日摄入量没有超过指导摄入量
			amountItemNew.eq(amountItemNew.length-1).removeClass('index_exceed');
			amountItemNew.eq(amountItemNew.length-1).find('.ingest_today2').text('指导摄入量还剩');
			amountItemNew.eq(amountItemNew.length-1).find('.remain_weight .shi_num').text(Math.abs(guideIntake-parseInt(ingestNum)));
		}else{ //超出指导量
			amountItemNew.eq(amountItemNew.length-1).addClass('index_exceed');
			amountItemNew.eq(amountItemNew.length-1).find('.ingest_today2').text('超过最大摄入量');
			amountItemNew.eq(amountItemNew.length-1).find('.remain_weight .shi_num').text(Math.abs(guideIntake-parseInt(ingestNum)));
		}
		amountItemNew.eq(amountItemNew.length-1).find('.detect_num2 .shi_num').text(overNum);
		
		$('.ingest_href').attr('href','../index/detail.html'); //添加跳转连接
	}
}

/* 刚进入首页,如果原本就有数据展示方法 */
function init(){
	let cookieFoodTypeVal = storageObject.sessionGetItem('foodTypeVal');
	if(cookieFoodTypeVal && cookieFoodTypeVal.length >0){
		let foodTypeLevel1 = [];
		let foodTypeLevel2 = [];
		if(cookieFoodTypeVal.indexOf('||') > -1){ //有多条数据
			foodTypeLevel1 = cookieFoodTypeVal.split('||');
			for(let i=0;i<foodTypeLevel1.length;i++){
				foodTypeLevel2 = foodTypeLevel1[i].split('@@');
				guideIntake = foodTypeLevel2[1];
				let amountItem = foodAmount.find('.amount_item');
				if(i===0){
					foodType.css('display','inline-block').append('<li class="food_item food_active">'+foodTypeLevel2[0]+'</li>');
					amountItem.eq(0).find('.index_con_box').show().find('.detect_num1').text(foodTypeLevel2[0]).end().find('.detect_num3').text(foodTypeLevel2[3]);
					amountItem.eq(0).find('#addDeviceBtn').hide();
				}else{
					/* foodType.find('.food_item').removeClass('food_active'); */
					foodType.css('display','inline-block').append('<li class="food_item">'+foodTypeLevel2[0]+'</li>');
					foodAmount.append(amountItem.eq(0).clone(true)); //克隆第一条数据,插入到最后
					amountItem.hide();
					let NowAmountItemLength  =foodAmount.find('.amount_item').length; //插入克隆数据后总数据的长度,$('#foodAmount>.amount_item')不能替换为amountItem,因为amountItem获取的没有克隆前的数据
					foodAmount.find('.amount_item').eq(NowAmountItemLength-1).removeClass('amount_item1').addClass('amount_item'+NowAmountItemLength).show().find('.detect_num1').text(foodTypeLevel2[0]).end().find('.detect_num3').text(foodTypeLevel2[3]);
					let foodItemSessionIndex = storageObject.sessionGetItem('foodItemIndex'); //当前应该显示的食物大类别序号
					let foodItemIndexNew1 = 0;
					if(foodItemSessionIndex){ //如果存在
						foodItemIndexNew1 = parseInt(foodItemSessionIndex);
					}else{ //session里不存在食物大类别序号,存储本地类别的长度到本地session里
						goodsItemIndex = NowAmountItemLength-1;
						foodItemIndexNew1 = goodsItemIndex;
					}
					foodType.find('.food_item').eq(foodItemIndexNew1).addClass('food_active').siblings().removeClass('food_active');
					foodAmount.find('.amount_item').eq(foodItemIndexNew1).show().siblings().hide();
				}
				
				/* 赋值重量单位 */
				foodAmount.find('.amount_item').eq(i).find('.shi_unit').text(foodTypeLevel2[2]);
				if(foodTypeLevel2[2] == '克'){
					foodAmount.find('.amount_item').eq(i).find('.shi_unit1').text('g');
				}else{
					foodAmount.find('.amount_item').eq(i).find('.shi_unit1').text('ml');
				}
				ingestDataMethod(parseInt(foodTypeLevel2[1]),parseInt(foodTypeLevel2[4]),parseInt(foodTypeLevel2[5]));
				/* let ingestHref = foodAmount.find('.amount_item').eq(i).find('.ingest_href').attr('href');
				if(ingestHref.indexOf('?') > -1){
					ingestHref = ingestHref.substr(0,ingestHref.indexOf('?'));
				}
				foodAmount.find('.amount_item').eq(i).find('.ingest_href').attr('href',ingestHref+'?item='+i); */
			}
		}else{ //只有一条数据
			let amountItem = foodAmount.find('.amount_item');
			foodTypeLevel2 = cookieFoodTypeVal.split('@@');
			guideIntake = foodTypeLevel2[1];
			foodType.css('display','inline-block').append('<li class="food_item food_active">'+foodTypeLevel2[0]+'</li>');
			amountItem.find('.index_con_box').show().find('.detect_num1').text(foodTypeLevel2[0]).end().find('.detect_num3').text(foodTypeLevel2[3]);
			amountItem.find('#addDeviceBtn').hide();
			
			/* 赋值重量单位 */
			foodAmount.find('.amount_item').eq(0).find('.shi_unit').text(foodTypeLevel2[2]);
			if(foodTypeLevel2[2] == '克'){
				foodAmount.find('.amount_item').eq(0).find('.shi_unit1').text('g');
			}else{
				foodAmount.find('.amount_item').eq(0).find('.shi_unit1').text('ml');
			}
			
			ingestDataMethod(parseInt(foodTypeLevel2[1]),parseInt(foodTypeLevel2[4]),parseInt(foodTypeLevel2[5]));
		}
		foodTypeTab(); //tab切换方法
		storageObject.sessionSetItem('foodItemIndex',goodsItemIndex);
		$('.ingest_href').attr('href','../index/detail.html'); //添加跳转连接
		let firstAddDeviceBtn = foodAmount.find('.amount_item').eq(0).find('#addDeviceBtn');
		if(firstAddDeviceBtn.is(':visible')){
			foodAmount.find('.amount_item').eq(0).find('.ingest_href').removeAttr('href');
		}
		
	}
	
}










