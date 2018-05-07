$(document).ready(function() {

    $('input').iCheck({
        checkboxClass: 'icheckbox_minimal-green',
        radioClass: 'iradio_minimal-green',
        increaseArea: '20%'
    });

    var panelOne = $('.form-panel.two').height(),
        panelTwo = $('.form-panel.two')[0].scrollHeight;

    $('.form-panel.two').not('.form-panel.two.active').on('click', function(e) {
        e.preventDefault();

        $('.form-toggle').addClass('visible');
        $('.form-panel.one').addClass('hidden');
        $('.form-panel.two').addClass('active');
        $('.form').animate({
            'height': panelTwo
        }, 200);
    });

    $('.form-toggle').on('click', function(e) {
        e.preventDefault();
        $(this).removeClass('visible');
        $('.form-panel.one').removeClass('hidden');
        $('.form-panel.two').removeClass('active');
        $('.form').animate({
            'height': panelOne + 92
        }, 200);
    });

});


function reloadCode() {
    $("#validateCodeImg").attr("src", ctx + "gifCode?data=" + new Date() + "");
}

function login() {
    var userName = $(".one input[name='userName']").val();
    var password = $(".one input[name='password']").val();
    var code = $(".one input[name='code']").val();
    var rememberMe = $(".one input[name='rememberMe']").is(':checked');

    var modulus = $("#password").attr("data-modulus"),
        exponent = $("#password").attr("data-exponent");

    // console.log(password)
    // console.log(modulus, exponent);

    if (password.length != 256) {
        var publicKey = RSAUtils.getKeyPair(exponent, '', modulus);
        password = RSAUtils.encryptedString(publicKey, password);
    }

    if (userName == "") {
        $MB.n_warning("请输入用户名！");
        return;
    }
    if (password == "") {
        $MB.n_warning("请输入密码！");
        return;
    }
    if (code == "") {
        $MB.n_warning("请输入验证码！");
        return;
    }
    $.ajax({
        type: "post",
        url: "/login",
        data: {
            "userName": userName,
            "password": password,
            "code": code,
            "rememberMe": rememberMe
        },
        dataType: "json",
        success: function (r) {

            if (r.code == 200) {
                // console.log(r.code)
                window.location.href = '/admin/index';
            } else {
                reloadCode();
                // console.log(r.msg)
                $MB.n_warning(r.msg);
            }
        }
    });
}

function register() {
    var userName = $(".two input[name='userName']").val().trim();
    var password = $(".two input[name='password']").val().trim();
    var rePassword = $(".two input[name='rePassword']").val().trim();
    if (userName == "") {
        $MB.n_warning("用户名不能为空！");
        return;
    } else if (userName.length > 10) {
        $MB.n_warning("用户名长度不能超过10个字符！");
        return;
    } else if (userName.length < 3) {
        $MB.n_warning("用户名长度不能少于3个字符！");
        return;
    }
    if (password == "") {
        $MB.n_warning("密码不能为空！");
        return;
    }
    if (rePassword == "") {
        $MB.n_warning("请再次输入密码！");
        return;
    }
    if (rePassword != password) {
        $MB.n_warning("两次密码输入不一致！");
        return;
    }
    console.log(userName, password);
    $.ajax({
        type: "post",
        url: "/register",
        data: {
            "userName": userName,
            "password": password
        },
        dataType: "json",
        success: function(r) {
            if (r.code == 1) {
                $MB.n_success(r.msg);
                $(".two input[name='userName']").val("");
                $(".two input[name='password']").val("");
                $(".two input[name='rePassword']").val("");
                $('.form-toggle').trigger('click');
            } else {
                $MB.n_warning(r.msg);
            }
        }
    });
}

document.onkeyup = function(e) {
    if (window.event)
        e = window.event;
    var code = e.charCode || e.keyCode;
    if (code == 13) {
        login();
    }
}