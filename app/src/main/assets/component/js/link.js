$(function(){
	showLevelWeight();
	function showLevelWeight(){
		var picker = new mui.PopPicker({
			layer: 1,
			buttons: ['取消', '确定']
		});
		picker.setData(
			[
				{
					value: "1",
					text: "蓝牙连接"
				}, {
					value: "2",
					text: "WIFI连接"
				}
			]
		);
		document.getElementById("link").addEventListener('tap', function(event) {
			var currentHeight = $("#link").text();
			      var checkData = picker.pickers[0].items;
			      var currentIndex = 1;
			     // console.log(checkData)
			      for(var i in checkData){
			        if(checkData[i].text == currentHeight){
			          currentIndex = i
			          break;
			        }
			      }
			// 默认显示第3项
			picker.pickers[0].setSelectedIndex(currentIndex, 2000);
			picker.show(function(selectItems) {
				var text = selectItems[0].text;
				$("#link").text(text );
				$("#link").css('color','#333');
				window.sessionStorage.setItem('device_link',text);
				if(text == 'WIFI连接'){
					$('.link_tc').show();
				}else{
					$('.link_tc').hide();
				}
				
			});
		});
		
	}

    //  进入设置页面，查询个人信息数据，就餐人数和浪费比例
    // 进入个人资料部分，获取useId
   var useId = js.getUserId('userId');
   // 查询用户信息, 存放用户信息
   var userInfoList = js.getUserInfoDataList(useId);
   var a = userInfoList.substring(1,userInfoList.length-1);
   var userInfoObj = JSON.parse(a); // 用户信息对象
    if(userInfoObj.wasteRate){
        $('.waste_num,.waste,#inline-range-val').text(userInfoObj.wasteRate);
        $('.bg_span').css('width', userInfoObj.wasteRate + '%') ;
        $('input[type="range"]').val(userInfoObj.wasteRate);
    }
    if(userInfoObj.number){
        $('.diner_num').text(userInfoObj.number);
        $('.num_val').val(userInfoObj.number);
    }

    // 修改用户信息（重新设置，就餐人数和浪费比例）
    $('#paramater_back').click(function(){
        var number = $('#dinner_num').text();
        var wasteRate = $('#waste_num').text();
        js.updateUserInfo(useId,userInfoObj.nickName,'','','','',wasteRate,number);
    })

})