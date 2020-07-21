$(function(){
	$('#systemBg,#systemClose,.system_previous').on('click',function(){
		$('#systemBox').hide();
	})
	$('#systemNext').on('click',function(){   //确认删除
		 let delIndex = $(this).attr('del-index');
		$('.device_list').children('li').eq(delIndex).remove();
		$('#systemBox').hide(); 
		if($('.device_list').children('li').length > 0){
			 $('.no_device').hide();
		}else{
			$('.no_device').show();
		}
		
		var li_name = $('.device_list').children('li').eq(delIndex).find('#add_name').text().trim();
		
		var deivceName = window.sessionStorage.getItem('deivceName');
		if(deivceName && deivceName.length > 0){
			var device_arr2 = deivceName.split(',');
			device_arr2.splice($.inArray(li_name,device_arr2),1);  // 删除数组中元素值
			var s = device_arr2.join(',');
			// 添加群组 设置缓存
			window.sessionStorage.setItem('deivceName',s); 
			// 如果全部删除了，提示显示
		}
	})
	
	/* 确认设备弹窗相关方法 */
		$('#confirmDeviceBack').on('click',function(){
			let thatType = $(this);
			thatType.parents('#confirmDevice').hide();
			$('#deviceBox').show();
		})
		
		$('#confirmBackBtn').on('click',function(){
			let thatType = $(this);
			thatType.parents('#confirmDevice').hide();
			$('#deviceBox').show();
		})
		/* 确认设备弹窗相关方法 end */
		
})