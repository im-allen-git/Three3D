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
})