var now = new Date();
var year = now.getFullYear();
var month = ((now.getMonth()+1)<10?"0":"")+(now.getMonth()+1)
var nowWeek1 = ''; //当前周数据
var day = (now.getDate()<10?"0":"")+now.getDate();
var hour = (now.getHours()<10?"0":"")+now.getHours();
var minutes = (now.getMinutes()<10?"0":"")+now.getMinutes();
var xAxis = []; //X轴的时间数据
var manArr = []; //手动输入数据
var devArr = []; //设备记录的数据
var oldxAxis = []; //xAxis X轴的时间数据因为新增手动输入时间会变化,oldxAxis用来存储没变化前的设备时间

var manualTimeArr = [];  //下方meal_data人均摄入量列表里所有手动输入meal_item_capita的时间组成的数组
var manualweightArr = [];  //下方meal_data人均摄入量列表里所有手动输入meal_item_capita的摄入重量组成的数组
var foodTypeAll = '';  //存储食物大类别的具体数据
var guideIntake = 0;  //某一个食物大类别的指导摄入量重量,在添加设备时获取
var weightUnit = '克';
var weightUnit1 = 'g';

initDate();

$(function(){
	var mySwiper = new Swiper ('.date_chart_box', {
	    direction: 'horizontal', // 垂直切换选项
	    loop: true, // 循环模式选项
		observer: true, //修改swiper自己或子元素时，自动初始化swiper
		observeParents: true, //修改swiper的父元素时，自动初始化swiper
	    pagination: {//分页器
	      el: '.swiper-pagination1',
		  clickable :true,
		  renderBullet: function (index, className) {
			switch(index){
				case 0:text='日';break;
				case 1:text='周';break;
				case 2:text='月';break;
				case 3:text='年';break;
			}
			return '<span class="' + className + '">' + text + '</span>';
		  },
		  clickableClass : 'chart_top_list',
		  bulletClass : 'chart_top_item',
		  bulletActiveClass: 'chart_top_sctive'
		  
	    }
	})

	setTimeout(function () {
        var mySwiper1 = new Swiper ('.date_chart_box1', {
            direction: 'horizontal', // 垂直切换选项
            loop: true, // 循环模式选项
			observer: true, //修改swiper自己或子元素时，自动初始化swiper
			observeParents: true, //修改swiper的父元素时，自动初始化swiper,添加了observer,observeParents后页面有多个swiper滑动模块都能正常滑动
            pagination: {//分页器
              el: '.swiper-pagination2',
        	  clickable :true,
        	  renderBullet: function (index, className) {
        		switch(index){
        			case 0:text='日';break;
        			case 1:text='周';break;
        			case 2:text='月';break;
        			case 3:text='年';break;
        		}
        		return '<span class="' + className + '">' + text + '</span>';
        	  },
        	  clickableClass : 'chart_top_list',
        	  bulletClass : 'chart_top_item',
        	  bulletActiveClass: 'chart_top_sctive'
            }
        })
    }, 200);
	
	
	
	/* 需要后台提供的初始化数据 */
	/* var mealDataAll = '1@06:10-06:40@1.5@克|2@11:10-11:40@3@克|3@20:10-20:40@1@克|手动输入@17:45@2@克#1@07:05-07:40@2@克|2@12:00-12:30@3.5@克|3@19:00-19:40@0.9@克|手动输入@18:15@1.0@克';
	var mealTypeDataList = [];
	var mealTypeDataListItem = [];
	var mealTypeDataListItemHtml = '';
	var numRex= /^[1-9]*[1-9][0-9]*$/;
	if(mealDataAll.indexOf('#') > -1){  //有多个大类别
		var mealTypeData = mealDataAll.split('#');
		for(let i=0;i<mealTypeData.length;i++){
			if(mealTypeData[i].indexOf('|') > -1){ //有多条数据
				 mealTypeDataList = mealTypeData[i].split('|');
				for(let j=0;j<mealTypeDataList.length;j++){
					mealTypeDataListItem = mealTypeDataList[j].split('@');
					if(numRex.test(mealTypeDataListItem[0]) === true){
						console.log(mealTypeDataListItem[0])
						mealTypeDataListItemHtml += '<li class="meal_item"><span class="meal_text"><em class="meal_num">第'+LowercaseToUppercase(Number(mealTypeDataListItem[0]))+'餐';
						
					}else{
						console.log(mealTypeDataListItem[0])
					}
					mealTypeDataListItemHtml += '';
				}
			}else{
				mealTypeDataListItem = mealTypeDataList[j].split('@');
			}
			
		}
	}else{
		
	} */



	xAxis = ["06:10", "11:10", "20:10"];
	oldxAxis =  ["06:10", "11:10", "20:10"];
	manArr = [0, 0, 0];
	devArr = [1, 2, 3];
	var editItem = $('#editItem');
	//detail_type子元素大类别的序号，要操作类别下的内容,先确认大类别,后确认类别下的第几条数据
	var itemIndex = Number(document.getElementById('detailType').getAttribute('item-index'));
	var mealDataElement = $('#typeCorrespondDetail .type_item').eq(itemIndex).find('.meal_data'); //因为.meal_data有多个,这里通过序号来查询,下面点击detail_item切换大类别时用重新获取,不然一直是第一次获取的那个元素
	var foodTypeVal = storageObject.sessionGetItem('foodTypeVal');  //session里存储的食品类别信息数据
	if(foodTypeVal&&foodTypeVal.length>0){
		var foodTypeValList = [];
		var foodTypeValList2 = [];
		if(foodTypeVal.indexOf('||') > -1){
			foodTypeValList = foodTypeVal.split('||');
			for(let i=0;i<foodTypeValList.length;i++){
				foodTypeValList2 = foodTypeValList[i].split('@@');
				if(i==0){
					$('#detailType').children('.detail_item').eq(0).text(foodTypeValList2[0]);
				}else if(i==1){
					$('#detailType').children('.detail_item').eq(1).text(foodTypeValList2[0]);
				}
				
				/* 赋值重量单位 */
				$('#typeCorrespondDetail').find('.type_item').eq(i).find('.shi_unit').text(foodTypeValList2[2]);
				if(foodTypeValList2[2] == '克'){
					$('#typeCorrespondDetail').find('.type_item').eq(i).find('.shi_unit1').text('g');
				}else{
					$('#typeCorrespondDetail').find('.type_item').eq(i).find('.shi_unit1').text('ml');
				}

				/* foodTypeValList2 = ''; */
				
			}
		}else{
			foodTypeValListFirst = foodTypeVal.split('@@')[0];
			$('#detailType').children('.detail_item').eq(0).text(foodTypeValListFirst);
			
			/* 赋值重量单位 */
			$('#typeCorrespondDetail').find('.type_item').eq(0).find('.shi_unit').text(foodTypeValListFirst[2]);
			if(foodTypeValListFirst[2] == '克'){
				$('#typeCorrespondDetail').find('.type_item').eq(0).find('.shi_unit1').text('g');
			}else{
				$('#typeCorrespondDetail').find('.type_item').eq(0).find('.shi_unit1').text('ml');
			}
		}
	}
	setTimeout(initChart(xAxis,manArr,devArr),120); //添加120ms时间让echarts渲染,不然canvas图标会超出div的宽度
	
	/* 因为手动输入插入数据要克隆meal_data子列表第一条数据,
	未防止用户先将子列表全删除了再手动输入(这时clone第一条数据为空),这里初始化时就clone插入,但是隐藏.
	这里如果clone后赋值给参数,后面再clone这个参数去插入到列表里.会造成插入的列表数据点击编辑和删除按钮没有反应 */
	mealDataElement.prepend(mealDataElement.find('.meal_item').eq(0).clone(true).hide());
	mealDataElement.find('.meal_item').eq(0).find('.meal_edit').attr('edit-number',0);  //给刚clone插入的第一个元素属性赋值0
	var item = parseInt(storageObject.sessionGetItem('foodItemIndex'));  //首页和详情页面通过foodItemIndex值确认显示哪个食品类别
	if(item == 1){
		$('#detailType').attr('item-index',item).children('.detail_item').eq(item).show().addClass('detail_active').siblings().removeClass('detail_active');
		$('.type_correspond_detail .type_item').eq(item).show().siblings().hide();
		itemIndex = item; //全局变量itemIndex值更新,下面可以直接用
		setTimeout(initChart1,120);
	}else{
		if(foodTypeValList && foodTypeValList.length > 1){ //大于一条数据
			$('#detailType').find('.detail_item').eq(1).show();
		}
	}
	/* 需要后台提供的初始化数据 end */

	$('.detail_item').on('click',function(){
		let _that = $(this);
		let index = _that.index();
		_that.addClass('detail_active').siblings().removeClass('detail_active');
		$('.type_correspond_detail .type_item').eq(index).show().siblings().hide();
		_that.parent().attr('item-index',index); //给父元素item-index属性添加序号,下面需要用到
		itemIndex = index; //全局变量itemIndex值更新,下面可以直接用、
		mealDataElement = $('#typeCorrespondDetail .type_item').eq(itemIndex).find('.meal_data'); //**这里很重要,切换的时候重新获取值,不然后面用每次获取的都说上面获取的那个元素
		let currentTypeUnit = $.trim(mealDataElement.find('.shi_unit:first').text());
		console.log(currentTypeUnit);
		setTimeout(initChart1,120);
	})
	
	/* 日周月年部分方法 */
	/* $('.chart_top_item').on('click',function(){
		let _that = $(this);
		let index = _that.index();
		_that.addClass('chart_top_sctive').siblings().removeClass('chart_top_sctive');
		_that.parents('.date_chart').find('.date_chart_list .date_chart_item').eq(index).show().siblings().hide();
	}) */

	var nowDayTime = year+'/'+month+'/'+day;
	var timeDay = nowDayTime; //传递addDate方法的日期参数
	var timeWeek = nowDayTime; //传递addDate方法的周参数，以为周数据也是用day数据减去7再计算的,所以这里用day数据做初始值
	var nowMonthTime = year+'/'+month;
	var timeMonth = nowMonthTime; //传递addDate方法的月份参数
	var timeYear = year; //传递addDate方法的年份参数
	var lessDayType = '';
	var lessWeekType = '';
	var lessMonthType = '';
	$('.date_chart_day').on('click','.date_right',function(){ //天数加方法
		let _that = $(this);
		if(_that.hasClass('date_no_click')){
			return false;
		}
		let plusTime = addDate(timeDay.toString(),1);
		timeDay = plusTime;  //将加过一天的新的时间传给参数,下次点击要用此参数
		if(nowDayTime.toString() === plusTime.toString()){ //如果时间增加到当前日期,就不能再增加了
			_that.addClass('date_no_click');
		}
		lessDayType = plusTime.split('/');
		lessDayType = lessDayType[0]+'年'+lessDayType[1]+'月'+lessDayType[2]+'日'; //转换时间格式
		_that.parents('.date_chart_day').find('.date_chart_txt').text(lessDayType);
	})

	$('.date_chart_day').on('click','.date_left',function(){ //天数减方法
		let lessTime = addDate(timeDay.toString(),-1);
		timeDay = lessTime;  //将减过一天的新的时间传给参数,下次点击要用此参数
		lessDayType = lessTime.split('/');
		lessDayType = lessDayType[0]+'年'+lessDayType[1]+'月'+lessDayType[2]+'日'; //转换时间格式
		$(this).parents('.date_chart_day').find('.date_chart_txt').text(lessDayType).siblings('.date_right').removeClass('date_no_click');
	})

	var plusWeekTime4 = '';
	$('.date_chart_week').on('click','.date_right',function(){ //周数加方法
		let _that = $(this);
		if(_that.hasClass('date_no_click')){
			return false;
		}
		let plusTime4 = addDate(timeWeek.toString(),7);
		timeWeek = plusTime4;//将加过一周的新的时间传给参数,下次点击要用此参数
		lessWeekType = plusTime4.split('/');
		let getDate1 = getMonthWeek(lessWeekType[0], lessWeekType[1], lessWeekType[2]);  /* 获取传过去的事件是某月的第几周 */
		lessWeekType = getDate1.getYear + "年" +getDate1.getMonth+"月"+getDate1.getWeek+"周"; //转换时间格式
		plusWeekTime4 = getDate1.getYear + "/" +getDate1.getMonth+"/"+getDate1.getWeek; //另外一种转换时间格式,用于和当前时间对于的周数对比
		if(nowWeek1.toString() === plusWeekTime4.toString()){ //如果时间增加到当前日期,就不能再增加了
			_that.addClass('date_no_click');
		}
		_that.parents('.date_chart_week').find('.date_chart_txt').text(lessWeekType);
	})
	$('.date_chart_week').on('click','.date_left',function(){ //周数减方法
		let lessTime4 = addDate(timeWeek.toString(),-7); //周每次减7天
		timeWeek = lessTime4;  //将减过一周的新的时间传给参数,下次点击要用此参数
		lessWeekType = lessTime4.split('/'); //将减掉7日后的时间2020/06/30,拆分成单独的年月日
		let getDate1 = getMonthWeek(lessWeekType[0], lessWeekType[1], lessWeekType[2]);  /* 获取传过去的事件是某月的第几周 */
		lessWeekType = getDate1.getYear + "年" +getDate1.getMonth+"月"+getDate1.getWeek+"周"; //转换时间格式
		$(this).parents('.date_chart_week').find('.date_chart_txt').text(lessWeekType).siblings('.date_right').removeClass('date_no_click');
	})
	
	$('.date_chart_month').on('click','.date_right',function(){ //月数加方法
		let _that = $(this);
		if(_that.hasClass('date_no_click')){
			return false;
		}
		var plusTime2 = addMonth(timeMonth.toString(),1);
		timeMonth = plusTime2;//将加过一月的新的时间传给参数,下次点击要用此参数
		if(nowMonthTime.toString() === plusTime2.toString()){ //如果时间增加到当前日期,就不能再增加了
			_that.addClass('date_no_click');
		}
		lessMonthType = plusTime2.split('/');
		lessMonthType = lessMonthType[0]+'年'+lessMonthType[1]+'月'; //转换时间格式
		_that.parents('.date_chart_month').find('.date_chart_txt').text(lessMonthType);
	})
	$('.date_chart_month').on('click','.date_left',function(){ //月数减方法
		var lessTime2 = addMonth(timeMonth.toString(),-1);
		timeMonth = lessTime2;//将减过一月的新的时间传给参数,下次点击要用此参数
		lessMonthType = lessTime2.split('/');
		lessMonthType = lessMonthType[0]+'年'+lessMonthType[1]+'月'; //转换时间格式
		$(this).parents('.date_chart_month').find('.date_chart_txt').text(lessMonthType).siblings('.date_right').removeClass('date_no_click');
	})
	
	$('.date_chart_year').on('click','.date_right',function(){ //年数加方法
		let _that = $(this);
		if(_that.hasClass('date_no_click')){
			return false;
		}
		var plusTime3 = addYear(timeYear.toString(),1);
		timeYear = plusTime3;//将加过一年的新的时间传给参数,下次点击要用此参数
		if(year.toString() === plusTime3.toString()){ //如果时间增加到当前年,就不能再增加了
			_that.addClass('date_no_click');
		}
		_that.parents('.date_chart_year').find('.date_chart_txt').text(plusTime3+'年');
	})

	$('.date_chart_year').on('click','.date_left',function(){ //年数减方法
		var lessTime3 = addYear(timeYear.toString(),-1);
		timeYear = lessTime3;//将减过一年的新的时间传给参数,下次点击要用此参数
		$(this).parents('.date_chart_year').find('.date_chart_txt').text(lessTime3+'年').siblings('.date_right').removeClass('date_no_click');
	})
	
	/* 日周月年部分方法 end */


	/* meal_data部分相关方法 */
	var editCapita = false;
	$('#typeCorrespondDetail .meal_item').on('click','.meal_edit',function(){
		var mealEditObj = [{'num':180,'name':'克'},{'num':160,'name':'克'},{'num':140,'name':'克'},{'num':120,'name':'克'}];  //默认的数据,后面从后台获取
		let _this = $(this);
		let editNumber = Number(_this.attr('edit-number'));   //当前编辑元素的默认编号
		let editIndex = _this.parents('.meal_item').index();    //当前编辑元素的序号
		let editTime1 = $.trim(_this.parents('.meal_item').find('.meal_time').text()); //时间
		editTime1 = editTime1.substr(1,editTime1.length-2);
		editItem.show().find('.device_operating_title').text(_this.parents('.meal_item').find('.meal_num').text()).siblings('.dining_time').text(editTime1);
		let mealEditHtml = '';
		let mealEditObj1 = storageObject.sessionGetItem("mealEdit-"+itemIndex+"-"+editNumber);
		if(mealEditObj1){ //如果当前编辑的内容在session里有数据,展示session的数据
			mealEditObj1 = mealEditObj1.split('@');
			editItem.find('#editType').find('.edit_item').eq(Number(mealEditObj1[0]-1)).addClass('edit_active').siblings().removeClass('edit_active');
			$('#inline-range').val(mealEditObj1[1]);
			$('#inline-range-val').text(mealEditObj1[1]).css('left', mealEditObj1[1] + '%');
			$('.bg_span').css('width', mealEditObj1[1] + '%');
			let nowMealEditObj = []; //创建空数组
			for(let i=2;i<mealEditObj1.length;i++){ //mealEditObj1前面两个数据是进餐人数和浪费比率,所以从2开始
				let jj = {} //创建数组对象
				jj.num = mealEditObj1[i];
				jj.name = weightUnit;
				nowMealEditObj.push(jj); //数组对象对数组里插入
			}
			mealEditObj = nowMealEditObj;
		}
		if(mealEditObj.length>0){
			editItem.find('.dev_input_item3').html('');
			for(let i=0;i<mealEditObj.length;i++){
				let index = LowercaseToUppercase(i+1);
				mealEditHtml += '<div class="weighing_list"><span class="input_txt"><span class="input_txt">第'+index+'次称重</span>';
				mealEditHtml += '<div class="increase_decrease"><span class="recommend_decrease"></span><input class="recommend_bun" type="text" value="'+mealEditObj[i].num+'">';
				mealEditHtml += '<span class="recommend_increase iconfont">&#xe630;</span><b class="recommend_font shi_unit">'+mealEditObj[i].name+'</b>';
				mealEditHtml += '</div><i class="recommend_close iconfont">&#xe7c3;</i></div>';
				
			}
			editItem.find('.dev_input_item3').append(mealEditHtml);
		}
		$('#addDeviceNext5').attr({'session-edit':itemIndex+'-'+editNumber,'edit-index':editIndex});  //将大类别序号,人均摄入列表序号赋值给确认按钮,后面存session时要用到,editIndex是编辑元素的序号,后面确认的时候要用到
		if(_this.parents('.meal_item').hasClass('meal_item_capita')){  //有meal_item_capita类名就表示是手动输入数据
			editCapita = true;
		}
		
		/* 点击减按钮 */
		$('.recommend_decrease').on('click',function(){
			let thatPlus = $(this);
			let inputVal = parseInt(thatPlus.siblings('.recommend_bun').val());
			if(inputVal === 0){
				thatPlus.siblings('.recommend_bun').val(0);
			}else{
				thatPlus.siblings('.recommend_bun').val(inputVal-1);
			}
		})
		
		/* 点击加按钮 */
		$('.recommend_increase').on('click',function(){
			let thatPlus = $(this);
			let inputVal = parseInt(thatPlus.siblings('.recommend_bun').val());
			if(inputVal > 9998){
				thatPlus.siblings('.recommend_bun').val(9999);
			}else{
				thatPlus.siblings('.recommend_bun').val(inputVal+1);
			}
		})

		/* 称重重量删除按钮 */
		$('.recommend_close').on('click',function(){
			let index = $(this).parents('.weighing_list').index();
			$('#confirmDelete').show().find('#addDeviceNext3').removeAttr('close-meal-index').attr('close-weigh-index',index);
		})
		
		
		
	})
	var mealCloseTime = '';  //点击人均摄入量列表里 设备数据 删除按钮获取的时间数据,下面通过length>-1判断是删除设备数据
	var manualCloseTime = '';  //点击人均摄入量列表里 手动输入数据 删除按钮获取的时间数据,下面通过length>-1判断是删除手动输入数据
	$('#typeCorrespondDetail .meal_item').on('click','.meal_close',function(){
		let _this = $(this);
		let index = _this.parents('.meal_item').index();
		$('#confirmDelete').show().find('#addDeviceNext3').removeAttr('close-weigh-index').attr('close-meal-index',index);
		var closeTime1 = $.trim(_this.parents('.meal_item').find('.meal_time').text());
		closeTime1 = closeTime1.substr(1,closeTime1.length-2);
		if(closeTime1.indexOf('-') > -1){
			closeTime1 = closeTime1.split('-')[0];
		}
		let fatherMealItem = _this.parents('.meal_item');
		if(fatherMealItem.hasClass('meal_item_capita')){ //删除的是手动输入的数据,将被点击的手动输入赋值给时间参数,后面点击确认按钮再删除
			manualCloseTime = closeTime1;
		}else{ //删除的是设备数据
			mealCloseTime = closeTime1;
		}
		
		
		
	})

	$('#confirmBg,#confirmClose').on('click',function(){
		$('#confirmDelete').hide();
	})
	$('#devicePrevious3').on('click',function(){
		$('#confirmDelete').hide();
	})

	$('#addDeviceNext3').on('click',function(){
		$('#confirmDelete').hide();
		let closeMealAttr = $(this).attr("close-meal-index");  //要删除的就餐数据的序号,这里判断有此属性就表示删除就餐列表部分的数据，否则删除的是编辑就餐editItem弹窗里的称重数据
		if(typeof closeMealAttr !== typeof undefined && closeMealAttr !== false){  //存在closeMealAttr属性,删除就餐数据
			if(manualCloseTime.length > 0){ //删除的是手动输入的数据
				let manualCloseTimeIndex = $.inArray(manualCloseTime,xAxis);
				if(Number(devArr[manualCloseTimeIndex]) !== 0){ //删除的是手动输入所以要看看同一时间内设备数据是否存在,这里不为0表示有设备数据,那就只能将手动输入数据置为0
					manArr[manualCloseTimeIndex] = 0;
				}else{ //这里为0表示没有设备数据,那就将图表里这一时间对应的手动输入数据、设备数据删除
					xAxis.splice(manualCloseTimeIndex,1); //删除数据里manualCloseTimeIndex位置的数据
					manArr.splice(manualCloseTimeIndex,1);
					devArr.splice(manualCloseTimeIndex,1);
				}
				manualCloseTime = ''; //使用过时间参数清空,后面从新开始
			}else if(mealCloseTime.length > 0){ //删除的是设备数据
				let mealCloseTimeIndex = $.inArray(mealCloseTime,xAxis);
				if(Number(manArr[mealCloseTimeIndex]) !== 0){ //删除的是设备数据所以要看看同一时间内手动输入数据是否存在,这里不为0表示有手动输入数据,那就只能将设备数据置为0
					devArr[mealCloseTimeIndex] = 0;
				}else{ //这里为0表示没有手动输入数据,那就将图表里这一时间对应的手动输入数据、设备数据删除
					xAxis.splice(mealCloseTimeIndex,1); //删除数据里mealCloseTimeIndex位置的数据
					manArr.splice(mealCloseTimeIndex,1);
					devArr.splice(mealCloseTimeIndex,1);
				}
				mealCloseTime = ''; //使用过时间参数清空,后面从新开始
			}
			setTimeout(initChart(xAxis,manArr,devArr),120);  //图表数据更新
			let delMealItem = mealDataElement.find('.meal_item').eq(Number(closeMealAttr)); //被点击的删除的元素

			/* 删除数据前,先将数据对应的session清除 */
			let delNumber = delMealItem.find('.meal_edit').attr('edit-number');
			storageObject.sessionRemoveItem("mealEdit-"+itemIndex+"-"+delNumber);

			delMealItem.remove(); //删除元素

		}else{  //不存在属性,删除就餐数据编辑弹窗里的称重数据
			let closeWeighAttr = Number($(this).attr("close-Weigh-index"));
			editItem.find('.dev_input_item3').children('.weighing_list').eq(closeWeighAttr).remove();
		}
		
		
		
		
	})

	$('#editBg,#editClose,#devicePrevious5').on('click',function(){
		editItem.hide();
	})
	
	$('#addDeviceNext5').on('click',function(){
		editItem.hide();
		let aThis = $(this);
		let sessionEdit = aThis.attr('session-edit'); //要存储sission名称的后缀名
		let numberOfMeals = $.trim(editItem.find('#editType .edit_active').text());
		let WasteRatio = $.trim(editItem.find('#inline-range-val').text());
		let weighingWeight = '';
		let weighingWeightArr = [];
		editItem.find('.recommend_bun').each(function(){
			weighingWeight = weighingWeight + '@' +$(this).val();
			weighingWeightArr.push(Number($(this).val()));
		})
		sessionHTML = numberOfMeals+'@'+WasteRatio+weighingWeight;
		storageObject.sessionSetItem("mealEdit-"+sessionEdit,sessionHTML);
		arrAction(weighingWeightArr); //计算称重总重方法
		let weighingWeightAll = arrActionNum; //arrAction方法计算的值赋值给weighingWeightAll

		/* let weighingWeightArrAverage = weighingWeightAll / (weighingWeightArr.length-1); */
		let perCapitaIntake = ((weighingWeightAll*(1-parseInt(WasteRatio)/100))/parseInt(numberOfMeals)).toFixed(1); //计算摄入量的平均值
		let index = parseInt(aThis.attr('edit-index'));
		let editTime = editItem.find('.dining_time').text();
		if(editTime.indexOf('-')>-1){
			editTime = editTime.split('-')[0];
		}
		let inArrIndex =  $.inArray(editTime,xAxis); //编辑时间在时间数组里的序号
		mealDataElement.find('.meal_item').eq(index).find('.meal_weight').text(perCapitaIntake+weightUnit);
		if(editCapita == true){  //editCapita为true就表示编辑的是手动输入数据
			manArr.splice(inArrIndex,1,perCapitaIntake);
		}else{  //editCapita为false表示编辑的是设备数据
			devArr.splice(inArrIndex,1,perCapitaIntake);
		}
		setTimeout(initChart(xAxis,manArr,devArr),120);  //图表数据更新
		editCapita = false;  //判断结束后恢复参数为false,再次点击时还要用
	})

	$('#ratioInterval').on('click','.interval_item',function(){
		let _that = $(this);
		let index = _that.index();
		let intervalWidth = (index+1)*12.5;
		_that.parents('.waste_chart').children('.progress_bar').css('width',intervalWidth+'%').find('.progress_tip').text(index+1);
	})
	
	mui.init({
		swipeBack: true //启用右滑关闭功能
	});
	//监听input事件，获取range的value值，也可以直接element.value获取该range的值
	var rangeList = document.querySelectorAll('input[type="range"]');
	for (var i = 0, len = rangeList.length; i < len; i++) {
		rangeList[i].addEventListener('input', function() {
			if (this.id.indexOf('field') >= 0) {
				document.getElementById(this.id + '-input').value = this.value;
				$('.bg_span').css('width', this.value + '%');
			} else {
				$('#'+this.id + '-val').text(this.value).css('left', this.value + '%');
				$('.bg_span').css('width', this.value + '%');
			}
		});
	}
	
	// $('.single-slider').jRange({
	// 	from: 0,//开始于
	// 	to: 100,//结束于
	// 	step: 12.5,//一次滑动多少
	// 	scale: [0,12.5,25,37.5,50,62.5,75,87.5,100],//分割点
	// 	format: '%s',//格式化格式
	// 	width: '100%',//宽度
	// 	showLabels: false,
	// 	snap: false,
	// 	showScale: false,
	// 	isRange:false,
	// 	onstatechange:function (data) {//数字变化的时候的回调函数,不论是点击还是拖动都会走此方法
	// 		/* $('.pointer-label').text(data/12.5).show();
	// 		console.log('数字变化'+data)
	// 		$('.slider').jRange('setValue', '12.5'); */
	// 	},
	// 	ondragend:function (data) {//拖动结束时的回调函数
	// 		/* $('.pointer-label').text(data/12.5).show(); */
	// 		setTimeout(function(){
	// 			let index = data/12.5;
	// 			let intervalWidth = ($('.waste_chart').width()) / 8;
	// 			let indexWidth = intervalWidth*index;
	// 			let circleLeft = indexWidth - 9;
	// 			let tipLeft = indexWidth - 4;
	// 			$('#editType .edit_item').eq(index-1).addClass('edit_active').siblings().removeClass('edit_active');
	// 			if(index === 0){
	// 				indexWidth = 0;
	// 				circleLeft = -5;
	// 				tipLeft = 0;
	// 				$('#editType .edit_item').removeClass('edit_active');
	// 			}
	// 			$('.progress_tip').text(index).show();
	// 			console.log('拖动结束'+data)
	// 			$('.selected-bar').css('width',indexWidth);
	// 			$('.pointer').css('left',circleLeft);
	// 			$('.progress_tip').css('left',tipLeft);
	// 		},420);
	// 	},
	// 	onbarclicked:function (data) {//刻度条被按住时的回调函数
	// 		/* $('.pointer-label').text(err/12.5).show(); */
	// 		setTimeout(function(){
	// 			let index = data/12.5;
	// 			let intervalWidth = ($('.waste_chart').width()) / 8;
	// 			let indexWidth = intervalWidth*index;
	// 			let circleLeft = indexWidth - 9;
	// 			let tipLeft = indexWidth - 4;
	// 			$('#editType .edit_item').eq(index-1).addClass('edit_active').siblings().removeClass('edit_active');
	// 			if(index === 0){
	// 				indexWidth = 0;
	// 				circleLeft = -5;
	// 				tipLeft = 0;
	// 				$('#editType .edit_item').removeClass('edit_active');
	// 			}
				
	// 			$('.progress_tip').text(index).show();
	// 			console.log('拖动结束'+data)
	// 			$('.selected-bar').css('width',indexWidth);
	// 			$('.pointer').css('left',circleLeft);
	// 			$('.progress_tip').css('left',tipLeft);
	// 		},420);
	// 	}
	// });

	$('#editType').on('click','.edit_item',function(){
		let _that = $(this);
		/* let index = _that.index()+1;
		let intervalWidth = index * ($('.waste_chart').width()/8); */
		_that.addClass('edit_active').siblings().removeClass('edit_active');
		/* $('.selected-bar').css('width',intervalWidth);
		$('.pointer').css('left',intervalWidth-9);
		$('.progress_tip').text(index).css('left',intervalWidth-5); */
	})
	
	

	/* meal_data部分相关方法 end */
	
	
	/* manualInput手动输入部分相关方法 */
	$('#footItem1').on('click',function(){
		$('#manualInput').show();
	})
	
	$('#manualClose').on('click',function(){
		$(this).parents('#manualInput').hide();
	})
	/* 点击选择日期方法 */
	$("#manualDay").click(function () {
		var dtPicker = new mui.DtPicker({ type: 'date', beginYear:1949,endYear:3016});
		/*参数：'datetime'-完整日期视图(年月日时分)
				'date'--年视图(年月日)
				'time' --时间视图(时分)
				'month'--月视图(年月)
				'hour'--时视图(年月日时)
		*/      
		dtPicker.show(function (selectItems) {
		   var y = selectItems.y.text;  //获取选择的年
		   var m = selectItems.m.text;  //获取选择的月
		   var d = selectItems.d.text;  //获取选择的日
		   var date = y + "/" + m + "/" + d ; 
		   $("#manualDay em").text(date); //赋值
		})
	});

	/* 点击选择时间方法 */
	$("#manualTime").click(function () {
		let dtPicker1 = new mui.DtPicker({ type: 'time'}); //type为time,表示是时间视图(时分)
		dtPicker1.show(function (selectItems) {
		   let h = selectItems.h.text;  //获取选择的时
		   let i = selectItems.i.text;  //获取选择的分
		   let date = h + ":" + i ; 
		   $("#manualTime em").text(date); //赋值
		})
	});

	/* 选择摄入量方法 */
	manualIntake('manualIntake');

	$('#manualConfirm').on('click',function(){
		let mealIntake = $('#manualIntake em').text();
		let mealIntake1  = Number(mealIntake.replace('克',''));
		let that1 = $(this);
		if(mealIntake1 != 0){
			that1.parents('#manualInput').hide();
			let mealDay = $('#manualDay em').text();
			let mealTime = $('#manualTime em').text();
			let mealFlag = true; //设备时间是否重复参数
			let manualFlag = true;  //手动输入时间是否重复参数
			mealDataElement.find('.meal_item_capita').remove();//重要: 每次新增手动输入数据前先将原来显示的手动输入列表数据删除(有meal_item_capita类名的),下面给排序后再循环插入
			if(mealDay === nowDayTime){ //选择的是当前日期,添加此数据到当前页面
				/* 操作dayChart图表 */
				for(let i=0;i<xAxis.length;i++){
					if(xAxis[i] === mealTime){ //选择的时间在原数组里已存在，那此时图表里不需要新增数据项,只需要修改原来的manArr手动输入数据数组既可
						mealFlag = false;
					}
				}
				if(mealFlag == true){  //选择的时间不在原数组里,给图表里添加一条新的数据
					/* 插入手动数据后给图表数据排序 */
					xAxis.push(mealTime);
					xAxis.sort(function(a,b){ //图表时间排序
						return a > b ? 1 : -1
					})
					let mealTimeIndex = xAxis.indexOf(mealTime); //查询新增的时间在数组里的位置
					manArr.splice(mealTimeIndex,0,mealIntake1);  //手动输入数组添加一个选择的克数
					devArr.splice(mealTimeIndex,0,0);  //设备获取数据添加0
				}else{  //选择的时间在原数组里
					/* 图表数据更新,替换manArr数组里这个位置的数据 */
					let mealTimeIndex1 = xAxis.indexOf(mealTime);
					manArr.splice(mealTimeIndex1,1,mealIntake1);  //用新的克数替换旧的手动输入的克数
				}
				setTimeout(initChart(xAxis,manArr,devArr),120);  //图表数据更新
				
				/* 操作meal_data列表 */
				/* 插入手动数据后给下方meal_data人均摄入量列表里所有手动输入meal_item_capita的时间组成的数组排序 */
				for(let k=0;k<manualTimeArr.length;k++){
					if(manualTimeArr[k] === mealTime){ //选择的时间在原数组里已存在，那此时图表里不需要新增数据项,只需要修改原来的manArr手动输入数据数组既可
						manualFlag = false;
					}
				}
				if(manualFlag == true){  //新增的手动输入时间不在原manualTimeArr数组里,新增一条数据
					manualTimeArr.push(mealTime);
					manualTimeArr.sort(function(a,b){ //下方列表里所有手动输入meal_item_capita的时间排序
						return a > b ? 1 : -1
					})
					let manualTimeIndex = manualTimeArr.indexOf(mealTime); //查询新增的时间在手动输入时间数组里的位置
					manualweightArr.splice(manualTimeIndex,0,mealIntake);  //获取到位置后，给摄入重量数组相应位置插入新增的人均摄入重量
					/* console.log('新插入数据不在原数组里'+manualTimeArr)
					console.log('新插入数据不在原数组里'+manualweightArr) */
				}else{  //新增的手动输入时间在原manualTimeArr数组里,meal_data列表里manualTimeArr人均摄入数组对应位置的旧数据替换掉
					let manualTimeIndex1 = manualTimeArr.indexOf(mealTime); //查询新增的时间在原来的手动输入时间数组里的位置
					manualweightArr.splice(manualTimeIndex1,1,mealIntake);  //获取到位置后,将摄入重量数组相应位置的旧的人均摄入重量数据替换掉
					/* console.log('新数据在原数组里'+manualTimeArr)
					console.log('新数据在原数组里'+manualweightArr) */
				}
				/* 更新meal_data列表里手动输入的数据 */
				for(let j=0;j<manualTimeArr.length;j++){
					let mealListLength = mealDataElement.find('.meal_item').length; //重要: 在for循环里每次clone前获取meal_item数组的长度,放到for外面或者clone后面获取都不对
					mealDataElement.append(mealDataElement.find('.meal_item').eq(0).clone(true).css('display','flex'));  //克隆eq(0)条数据后插入到最后.即使设备数据全被删除了，还有隐藏的那条数据可以clone
					mealDataElement.find('.meal_item').eq(mealListLength).addClass('meal_item_capita').find('.meal_edit').attr('edit-number',mealListLength).end().find('.meal_num').text('手动输入').siblings('.meal_time').text('('+manualTimeArr[j]+')').siblings('.meal_weight').text(manualweightArr[j]);
				}
				
			}else{ //选择的不是当前日期,就要将这个添加数据添加到对应的日期去,需要传到后台了
				
			}
		}else{
			that1.css('background','#ccc');
		}
	})

	/* manualInput手动输入部分相关方法 end */

	/* addDevice编辑项目部分相关方法 */
	var addDevice = $('#addDevice');
	$('#footItem2').on('click',function(){
		addDevice.show();
	})

	$('#addDeviceBg,#addDeviceClose,#devicePrevious1').on('click',function(){
		addDevice.hide();
	})
	$('#addType1').on('click','li',function(){
		let thatType = $(this);
		if(thatType.hasClass('type1_customize')){
			$('#customizeProject').show();
		}else{
			thatType.addClass('type1_active').siblings().removeClass('type1_active');
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
	$('#addType2').on('click','li',function(){
		let thatType = $(this);
		thatType.addClass('type2_active').siblings().removeClass('type2_active');
	})
	
	/* 点击减按钮 */
	addDevice.find('.recommend_decrease').on('click',function(){
		let thatPlus = $(this);
		let inputVal = parseInt(thatPlus.siblings('.recommend_bun').val());
		if(inputVal === 0){
			thatPlus.siblings('.recommend_bun').val(0);
		}else{
			thatPlus.siblings('.recommend_bun').val(inputVal-1);
		}
	})

	/* 点击加按钮 */
	addDevice.find('.recommend_increase').on('click',function(){
		let thatPlus = $(this);
		let inputVal = parseInt(thatPlus.siblings('.recommend_bun').val());
		if(inputVal > 9998){
			thatPlus.siblings('.recommend_bun').val(9999);
		}else{
			thatPlus.siblings('.recommend_bun').val(inputVal+1);
		}
	})

	$('#addDeviceNext1').on('click',function(){
		addDevice.hide();
		$('#newEquipments').show();
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
	/* add_dev_two确认添加设备上一步方法 */
	$('#devicePrevious2').on('click',function(){
		$('#newEquipments').show();
		$('#addDevice').hide().find('.add_dev_one').show().next('.add_dev_two').hide();
		foodTypeAll = foodTypeAll.substr(0,foodTypeAll.lastIndexOf('@@'));  //添加设备点击上一步时,去掉foodTypeAll最后面已经添加的设备名称
		storageObject.sessionRemoveItem('foodTypeVal');
	})
	/* add_dev_two确认添加设备完成方法 */
	$('#addDeviceNext2').on('click',function(){
		let foodTypeItem = $('#foodType').children('li');
		let foodItemArr = foodTypeAll.split('@@');
		$('#addDevice').hide().find('.add_dev_one').show().next('.add_dev_two').hide();
	})
	
	/* addDevice编辑项目部分相关方法 end */
	
	/* newEquipments添加新设备列表相关方法 */
	$('#device_bg,#newEquipmentsClose').on('click',function(){
		$('#newEquipments').hide();
	})
	$('#equipmentsList').on('click','.equipments_item',function(){
		let thatEqu = $(this);
		thatEqu.find('.equipments_check').addClass('equipments_active').end().siblings().find('.equipments_check').removeClass('equipments_active');
	})
	$('#devicePrevious4').on('click',function(){
		$('#confirmDevice').show();
		$('#newEquipments').hide();
	})
	$('#addDeviceNext4').on('click',function(){
		$('#newEquipments').hide();
		let addDeviceName = $(this).parents('.equipments_main').find('.equipments_active').siblings('.equipments_name').text();
		let foodTypeAllArr = foodTypeAll.split('@@');
		$('#addDevice').show().find('.add_dev_one').hide().next('.add_dev_two').show().find('.add_txt1').text(foodTypeAllArr[0]).end().find('.add_txt2').text(foodTypeAllArr[2]).end().find('.add_txt3').text(foodTypeAllArr[1]+foodTypeAllArr[2]+'/天').end().find('.add_txt4').text(addDeviceName);
		foodTypeAll = foodTypeAll + '@@' + addDeviceName;
		if(foodTypeVal && foodTypeVal.length > 0){
			storageObject.sessionSetItem('foodTypeVal',foodTypeVal+'||'+foodTypeAll);
		}else{
			storageObject.sessionSetItem('foodTypeVal',foodTypeAll);
		}
	})
	/* newEquipments添加新设备列表相关方法 end */
	
	
	
	
	
	
})

/* 初始化时间 */
function initDate(){
	/* var now = new Date();
	var year = now.getFullYear();
	var month = ((now.getMonth()+1)<10?"0":"")+(now.getMonth()+1)
	var day = (now.getDate()<10?"0":"")+now.getDate(); */
	var getDate = getMonthWeek(year, month, day);
	var dayTime = year + "年" +month+"月"+day+"日";
	var weekTime = year + "年" +month+"月"+getDate.getWeek+"周";
	nowWeek1 = getDate.getYear + "/" +getDate.getMonth+"/"+getDate.getWeek;
	$('.date_chart_day').find('.date_chart_txt').text(dayTime); //日周月年时间初始化
	$('.date_chart_week').find('.date_chart_txt').text(weekTime);
	$('.date_chart_month').find('.date_chart_txt').text(year + "年" +month+"月");
	$('.date_chart_year').find('.date_chart_txt').text(year + "年");
	$('#manualDay em').text(year + "/" +month+"/"+day); //手动输入事件初始化
	$('#manualTime em').text(hour + ":" +minutes);
}

// 日期，在原有日期基础上，增加days天数，默认增加1天
function addDate(date,days){
	if (days == undefined || days == ''){
		days = 1;
	}
	var d=new Date(date);
	d.setDate(d.getDate()+days);
	var month=d.getMonth()+1;
	var day = d.getDate();
	if(month<10){
		month = "0"+month;
	}
	if(day<10){
		day = "0"+day;
	}
	var val = d.getFullYear()+"/"+month+"/"+day;
	return val;
}
//月份，在原有的日期基础上，增加 months 月份，默认增加1月
function addMonth(date,months){
    if(months==undefined||months=='')
        months=1;
    var date=new Date(date);
    date.setMonth(date.getMonth()+months);
    var month=date.getMonth()+1;
	if(month<10){
		month = "0"+month;
	}
    return date.getFullYear()+'/'+month;
}

//年份，在原有的日期基础上，增加 years年份，默认增加1年
function addYear(date,years){
    if(years==undefined||years=='')
        years=1;
    var date=new Date(date);
    date.setFullYear(date.getFullYear()+years);
    return date.getFullYear();
}

function initChart(xAxis,manArr,devArr){
	// 柱状体
	var myChart = echarts.init(document.getElementById('dayChart'));
	
	// 指定图表的配置项和数据
	var option = {
		tooltip: {},
		legend: {},
		grid: { //配置canves图标父元素div的距离
			top:"25px",
		    left:"10px",
		    right:"10px",
		    bottom:"20px",
			width:'auto',
			containLabel: true
		},
		xAxis: { //配置x轴数据和坐标轴轴线
			axisLine:{
				show: false
			},
			axisTick:{
				show: false
			},
			type: 'category',
			data: xAxis
		},
		yAxis: { //配置y轴数据和坐标轴轴线
			axisLine:{
				show: false
			},
			axisTick:{
				show: false
			},
			splitLine: {
				show: true,
				lineStyle: {
					type: 'dashed'
			    }
			}
		},
		series: [
			{
				type: 'bar',
				size: '100%',  //图大小
				barWidth : 8, //设置柱的宽度
				barGap: "100%",/*多个并排柱子设置柱子之间的间距*/
				/* barCategoryGap: "100%", */   /*同一系列的柱间距离，默认为类目间距的20%，可设固定值*/
				itemStyle:{
					normal:{
						barBorderRadius: 4, //设置柱的圆角
						color:'#AD39FF',
						label: { //柱顶部显示数值
							show: true,
							position: 'top',
							distance: 3,
							textStyle: {	    //数值样式
							    fontSize: 14
						    },
							formatter: function (params) { //数据为0时不显示数字0
								if (params.value > 0) {
									return params.value;
								} else {
									return '';
								}
						    }
						}
					}
				},
				data: manArr
			}, 
			{
				type: 'bar',
				barWidth : 8, //设置柱的宽度
				barGap: "100%",/*多个并排柱子设置柱子之间的间距*/
				itemStyle:{
					normal:{
						barBorderRadius: 4, //设置柱的圆角
						color:'#39A0FF',
						label: { //柱顶部显示数值
							show: true,
							position: 'top',
							distance: 3,
							textStyle: {	    //数值样式
							    fontSize: 14
							},
							formatter: function (params) { //数据为0时不显示数字0
								if (params.value > 0) {
									return params.value;
								} else {
									return '';
								}
						    }
						}
					}
				},
				data: devArr
			}
			
		]
	};
	
	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option,true,true);
	$(window).resize(function(){  //改变浏览器窗口大小时，图标自动从新加载
		myChart.resize();
	})
}

function initChart1(){
	// 柱状体
	var myChart1 = echarts.init(document.getElementById('dayChart2'));
	
	// 指定图表的配置项和数据
	var option1 = {
		tooltip: {},
		legend: {},
		grid: { //配置canves图标父元素div的距离
			top:"25px",
		    left:"10px",
		    right:"10px",
		    bottom:"20px",
			width:'auto',
			containLabel: true
		},
		xAxis: { //配置x轴数据和坐标轴轴线
			axisLine:{
				show: false
			},
			axisTick:{
				show: false
			},
			type: 'category',
			data: ["06:00", "12:00","15:05", "18:00"]
		},
		yAxis: { //配置y轴数据和坐标轴轴线
			axisLine:{
				show: false
			},
			axisTick:{
				show: false
			},
			splitLine: {
				show: true,
				lineStyle: {
					type: 'dashed'
			    }
			}
		},
		series: [
			{
				type: 'bar',
				barWidth : 8, //设置柱的宽度
				barGap: "100%",/*多个并排柱子设置柱子之间的间距*/
				/* barCategoryGap: "100%", */   /*同一系列的柱间距离，默认为类目间距的20%，可设固定值*/
				itemStyle:{
					normal:{
						barBorderRadius: 4, //设置柱的圆角
						color:'#AD39FF',
						label: { //柱顶部显示数值
							show: true,
							position: 'top',
							distance: 3,
							textStyle: {	    //数值样式
							    fontSize: 14
						    },
							formatter: function (params) { //数据为0时不显示数字0
								if (params.value > 0) {
									return params.value;
								} else {
									return '';
								}
						    }
						}
					}
				},
				data: [4, 4, 0,3]
			}, 
			{
				type: 'bar',
				barWidth : 8, //设置柱的宽度
				barGap: "100%",/*多个并排柱子设置柱子之间的间距*/
				itemStyle:{
					normal:{
						barBorderRadius: 4, //设置柱的圆角
						color:'#39A0FF',
						label: { //柱顶部显示数值
							show: true,
							position: 'top',
							distance: 3,
							textStyle: {	    //数值样式
							    fontSize: 14
							},
							formatter: function (params) { //数据为0时不显示数字0
								if (params.value > 0) {
									return params.value;
								} else {
									return '';
								}
						    }
						}
					}
				},
				data: [3.6, 3,3.9, 0]
			}
			
		]
	};
	
	// 使用刚指定的配置项和数据显示图表。
	myChart1.setOption(option1,true,true);
	$(window).resize(function(){  //改变浏览器窗口大小时，图标自动从新加载
		myChart1.resize();
	})
}

function manualIntake(id){
	var picker = new mui.PopPicker({
		layer: 1,
		buttons: ['取消','确定']
	});
	picker.setData(
		[
			{
				value: "1",
				text: "0克"
			}, {
				value: "2",
				text: "1克"
			}, {
				value: "3",
				text: "2克"
			}, {
				value: "4",
				text: "3克"
			}, {
				value: "5",
				text: "4克"
			},
			{
				value: "6",
				text: "5克"
			},
			{
				value: "7",
				text: "6克"
			},
			{
				value: "8",
				text: "7克"
			},
			{
				value: "9",
				text: "8克"
			},
			{
				value: "10",
				text: "9克"
			},
			{
				value: "11",
				text: "10克"
			},
			{
				value: "12",
				text: "11克"
			},
			{
				value: "13",
				text: "12克"
			},
			{
				value: "14",
				text: "13克"
			},
			{
				value: "15",
				text: "14克"
			},
			{
				value: "16",
				text: "15克"
			},
			{
				value: "17",
				text: "16克"
			},
			{
				value: "18",
				text: "17克"
			},
			{
				value: "19",
				text: "18克"
			},
			{
				value: "20",
				text: "19克"
			},
			{
				value: "21",
				text: "20克"
			}
		]
	);
	document.getElementById(id).addEventListener('tap', function(event) {
		/* $("#"+id).text(""); */
		picker.pickers[0].setSelectedIndex(6, 2000);
		picker.show(function(selectItems) {
			let text = selectItems[0].text;
			$("#"+id).find('em').text(text);
			let textNum = Number(text.replace('克',''));
			if(textNum == 0){
				$('#manualConfirm').css('background','#ccc');
			}else{
				$('#manualConfirm').css('background','#16B347');
			}
			
		});
	});
}

/* 获取当前时间是本月第几周 */
function getMonthWeek (a, b, c) {
	/**
	* a = d = 当前日期
	* b = 6 - w = 当前周的还有几天过完(不算今天)
	* a + b 的和在除以7 就是当天是当前月份的第几周
	*/
	var date = new Date(a, parseInt(b) - 1, c),
		w = date.getDay(),
		d = date.getDate();
	if(w==0){
		w=7;
	}
	var config={
		getMonth:date.getMonth()+1,
		getYear:date.getFullYear(),
		getWeek:Math.ceil((d + 6 - w) / 7),
	}
	return config;
};


/* 正排序函数 */
function sortNumber(a,b){
	return a - b
}

/* 小写转大写 */
function LowercaseToUppercase(num){
	var uppercase = '';
	switch(num){
		case 1 : uppercase = '一';break;
		case 2 : uppercase = '二';break;
		case 3 : uppercase = '三';break;
		case 4 : uppercase = '四';break;
		case 5 : uppercase = '五';break;
		case 6 : uppercase = '六';break;
		case 7 : uppercase = '七';break;
		case 8 : uppercase = '八';break;
		case 9 : uppercase = '九';break;
	}
	return uppercase;
}


//cookie
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

/* 计算数组的差值, */
var arrActionNum = 0; //计算差值总数
function arrAction (r){
	let arrActionFlag = false;
	let sun = 0,j=0;
	for ( let i =  0 ; i < r.length-1 ; i++){
	    if ( r[i] > r[i+1] ){
	        sun = sun+(r[i]-r[i+1]);
			arrActionFlag = false;
	    }else{
			j = i;
			arrActionFlag = true;
			break;
		}
	}
	if(arrActionFlag == true){
		r.splice(j,1);
		let r1 = r;
		arrAction (r1);
		return false;
	}
	
	arrActionNum = sun;
	return false;
}

// 获取url中参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg); //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}













