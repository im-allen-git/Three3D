$( function () {

} );

function goPage( type ) {
	switch (type) {
		case 1:
			js.changeActive( "1" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
			break;

		case 4:
			js.changeActive( "4" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
			break;

		case 6:
			js.changeActive( "6" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页 6 index页面
			break;
	}
}

function thisParamInfo( type ) {
	if (type == 0) {
		$( ".module_param,.module_param_bg" ).show();
	} else {
		$( ".module_param,.module_param_bg" ).hide();
	}
}
