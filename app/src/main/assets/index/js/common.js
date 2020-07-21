window.onload = window.onresize = function() {

	var clientWidth = document.documentElement.clientWidth;

	document.getElementsByTagName("html")[0].style.fontSize =

		clientWidth / 750 * 100 + "px";
}



/* (function () {
	var currClientWidth, fontValue, originWidth;
	originWidth = 750;
	__resize();
	window.addEventListener('resize', __resize, false);

	function __resize() {
		currClientWidth = document.documentElement.clientWidth || document.body.clientWidth;
		if (currClientWidth > 768) {
			currClientWidth = 768;
		}
		if (currClientWidth < 320) {
			currClientWidth = 320;
		}
		fontValue = (currClientWidth / originWidth * 100).toFixed(2);
		document.getElementsByTagName("html")[0].style.fontSize = fontValue + 'px';
	}
})(); */