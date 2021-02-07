var firstAccess = 0; //是否第一次访问app
var firstBuild = 0; //是否第一次访问创建模型
var firstMyWorld = 0; //是否第一次访问我的世界
var isAndroid =true;
$(function () {

});
//console.log(123)
function goPage( type ) {
    type = Number(type);
    switch (type) {
            case 1:
                $(".save_succ,.save_name_module_bg").hide();
                js.changeActive( "1" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;
            case 2:
                js.changeActive( "2" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;
            case 3:
                js.changeActive( "3" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;
            case 4:
                js.changeActive( "4" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;
            case 5:
                // window.location.href = "ios:@5" ; //1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                js.changeActive( "5" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;

            case 6:
                js.changeActive( "6" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;
            case 7:
                js.changeActive( "7" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;
            case 8:
                js.changeActive( "8" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;
            case 61:
                js.changeActive( "61" );//1,我的模型 2 商城 3 模型库首页 4 创建模型 5 返回上一页
                break;
            case 66:
                js.changeActive( "66" );//第一次wifi连接 引导页
                break;

        }

}
function firstCheck(access, build, myworld){//0 没有访问过  1 访问过
    firstAccess = access;
    firstBuild = build;
    firstMyWorld = myworld;
}