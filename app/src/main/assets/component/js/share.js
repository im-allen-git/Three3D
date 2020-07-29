$(function(){
    // 获取 userId
    var userId = js.getUserId('userId');

    // 进入页面查询群组数据，没有数据群组列表就显示空
    var bindingUserList = js.getBindingUserList(userId);
    var bindingUserObj = JSON.parse(bindingUserList);
    if(bindingUserObj && bindingUserObj.length > 0){
        var list = '';
        for(var i = 0;i< bindingUserObj.length;i++){
            if(bindingUserObj[i].bindingUserid && bindingUserObj[i].bindingUserid != ''){
                list += '<div class="list clearfix">'
                list +=	'<div class="col-xs-10">'
                list +=		'<span class="group_name" id="add_name"> '+ bindingUserObj[i].bindingUserid +' </span>'
                list +=		'<span class="state"> 已连接 </span>'
                list +=	'</div>'
                list +=	'<div class="col-xs-2 text-right">'
                list +=		'<i class="iconfont icon-delete del_list" onclick="del_list(this,'+ userId +')"></i>'
                list +=	'</div>'
                list += '</div>';
            }
        }

        $('.group_list').append(list).show();
        $('.no_group').hide();
    }else{
        $('.group_list').hide();
        $('.no_group').show();
    };

    // 添加群组数据

    $('#group_phone_data').click(function(){
        var group_list = '';
        var bindingId = $.trim($('#phone_val').val());
        group_list += '<div class="list clearfix">'
        group_list +=	'<div class="col-xs-10">'
        group_list +=		'<span class="group_name" id="add_name"> '+ bindingId +' </span>'
        group_list +=		'<span class="state"> 已连接 </span>'
        group_list +=	'</div>'
        group_list +=	'<div class="col-xs-2 text-right">'
        group_list +=		'<i class="iconfont icon-delete del_list" onclick="del_list(this,'+ userId +')"></i>'
        group_list +=	'</div>'
        group_list +='</div>';
        $('.group_list').append(group_list).show();
        $('.no_group').hide();
        $('.phone_tc').hide();
        js.bindingUserAdd(userId,bindingId);
    });

    // 删除群组用户数据

    // 清空电话号码添加
    $('.phone_del').click(function(){
        $('.phone_val').val('');
    });
})

// 删除群组成员
function del_list(obj,userId){
    $(obj).closest('.list').remove();
    var li_name = $(obj).closest('.list').find('#add_name').text().trim();
    js.bindingUserDel(userId,li_name);
};
