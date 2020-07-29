$(function(){
    // 后台交互部分
    // 进入个人资料部分，获取useId
    var useId = js.getUserId('userId');
    // 查询用户信息, 存放用户信息
    var userInfoList = js.getUserInfoDataList(useId);
    var a = userInfoList.substring(1,userInfoList.length-1);
    var userInfoObj = JSON.parse(a);// 用户信息对象
    console.log('userInfoObj == ' + userInfoList);
    if(userInfoObj.nickName){
        $('.name_text').text(userInfoObj.nickName);
        $('.name_val').val(userInfoObj.nickName);
    };
    if(userInfoObj.sex){
        $('.sex_text').text(userInfoObj.sex);
        $('.sex_tc .sex ').each(function(){
            if($(this).text() == userInfoObj.sex){
                $(this).addClass('active').siblings('span').removeClass('active');
            }
        })
    };
    if(userInfoObj.birthday){
        $('#demo4').text(userInfoObj.birthday).css('color','#333');
    };
    if(userInfoObj.height){
         $('#height').text(userInfoObj.height).css('color','#333');
    };
    if(userInfoObj.weight){
         $('#weight').text(userInfoObj.weight).css('color','#333');
    };
    // 修改用户信息后，返回个人中心并提交后台数据参数
    $('#person_data').click(function(){
     var nickName = $('.name_text').text();
     var sex = $('.sex_text').text();
     var birthday = $('#demo4').text();
     var height = $('#height').text();
     var weight = $('#weight').text();
     js.updateUserInfo(useId,nickName,sex,birthday,height,weight,'','');
    })
})