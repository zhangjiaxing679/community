function get_code() {
// 获取用户输入的邮箱地址
    var email=$("#your-email").val();
    console.log("email:", email);

    //发送异步请求
    $.post(
        CONTEXT_PATH+"/user/code",
        {
            "email":email
        }
    );
}
