$(function(){
	showLevelHeight();
	function showLevelHeight() {
		var picker = new mui.PopPicker({
			layer: 1,
			buttons: ['取消','确定']
		});
		picker.setData(
			[
				{value: "1",text: "140 cm"}, 
				{value: "2",text: "141 cm"},
				{value: "3",text: "142 cm"},	
				{value: "4",text: "143 cm"},
				{value: "5",text: "144 cm"},
				{value: "6",text: "145 cm"},
				{value: "7",text: "146 cm"},
				{value: "8",text: "147 cm"},
				{value: "9",text: "148 cm"},
				{value: "10",text: "149 cm"},
				{value: "11",text: "150 cm"},
				{value: "12",text: "151 cm"},
				{value: "13",text: "152 cm"},
				{value: "14",text: "153 cm"},
				{value: "15",text: "154 cm"},
				{value: "16",text: "155 cm"},
				{value: "17",text: "156 cm"},
				{value: "18",text: "157 cm"},
				{value: "19",text: "158 cm"},
				{value: "20",text: "159 cm"},
				{value: "21",text: "160 cm"},
				{value: "22",text: "161 cm"},
				{value: "23",text: "162 cm"},
				{value: "24",text: "163 cm"},
				{value: "25",text: "164 cm"},
				{value: "26",text: "165 cm"},
				{value: "27",text: "166 cm"},
				{value: "28",text: "167 cm"},
				{value: "29",text: "169 cm"},
				{value: "30",text: "170 cm"},
				{value: "31",text: "171 cm"},
				{value: "32",text: "172 cm"},
			    {value: "33",text: "173 cm"},
			    {value: "34",text: "174 cm"},
			    {value: "35",text: "175 cm"},
			    {value: "36",text: "176 cm"},
			    {value: "37",text: "177 cm"},
			    {value: "38",text: "178 cm"},
			    {value: "39",text: "179 cm"},
			    {value: "40",text: "180 cm"},
				{value: "41",text: "181 cm"},
				{value: "42",text: "182 cm"},
				{value: "43",text: "183 cm"},
				{value: "44",text: "184 cm"},
				{value: "45",text: "185 cm"},
				{value: "46",text: "186 cm"},
				{value: "47",text: "187 cm"},
				{value: "48",text: "188 cm"},
				{value: "49",text: "189 cm"},
				{value: "50",text: "190 cm"},
				{value: "51",text: "191 cm"},
				{value: "52",text: "192 cm"},
				{value: "53",text: "193 cm"},
				{value: "54",text: "194 cm"},
				{value: "55",text: "195 cm"},
				{value: "56",text: "196 cm"},
				{value: "57",text: "197 cm"},
				{value: "58",text: "198 cm"},
				{value: "59",text: "199 cm"},
				{value: "60",text: "200 cm"}
			]
		);
		document.getElementById("height").addEventListener('tap', function(event) {
			var currentHeight = $("#height").text();
			      var checkData = picker.pickers[0].items;
			      var currentIndex = 20;
			     // console.log(checkData)
			      for(var i in checkData){
			        if(checkData[i].text == currentHeight){
			          currentIndex = i
			          break;
			        }
			      }
			// $("#height").text("");
			// 默认显示第7项
			picker.pickers[0].setSelectedIndex(currentIndex, 2000);
			
			picker.show(function(selectItems) {
				var text = selectItems[0].text;
				$("#height").text(text);
				$("#height").css('color','#333');
				 window.sessionStorage.setItem('height',text);
				
			});
		});
	}
})