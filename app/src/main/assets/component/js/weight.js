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
					text: "50 kg"
				}, {
					value: "2",
					text: "50.5 kg"
				}, {
					value: "3",
					text: "60 kg"
				},  {
					value: "4",
					text: "60.5 kg"
				}, {
					value: "5",
					text: "70 kg"
				}, {
					value: "6",
					text: "70.5 kg"
				}, {
					value: "7",
					text: "80 kg"
				}, {
					value: "8",
					text: "80.5 kg"
				}, {
					value: "9",
					text: "90 kg"
				}, {
					value: "10",
					text: "90.5 kg"
				}, {
					value: "11",
					text: "100 kg"
				}
			]
		);
		document.getElementById("weight").addEventListener('tap', function(event) {
			var currentHeight = $("#weight").text();
			      var checkData = picker.pickers[0].items;
			      var currentIndex = 20;
			     // console.log(checkData)
			      for(var i in checkData){
			        if(checkData[i].text == currentHeight){
			          currentIndex = i
			          break;
			        }
			      }
			// $("#weight").text("");
			// 默认显示第3项
			picker.pickers[0].setSelectedIndex(currentIndex, 2000);
			picker.show(function(selectItems) {
				var text = selectItems[0].text;
				$("#weight").text(text );
				$("#weight").css('color','#333');
				window.sessionStorage.setItem('weight',text);
			});
		});
		
	}
})