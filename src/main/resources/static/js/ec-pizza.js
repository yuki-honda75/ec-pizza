"use strict"

$(function(){
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
      xhr.setRequestHeader(header, token);
    });
    //郵便番号入力時のハイフン自動入力
    function inputHyphen(input) {
        return input.slice(0, 3) + '-' + input.slice(3, input.length);
    }

    $('#zipcode').on('keyup', function(event){
        let input = $(this).val();

        let key = event.keyCode || event.charCode;
        if (key == 8 || key == 46) {
            return false;
        }

        if (input.length == 3) {
            $(this).val(inputHyphen(input));
        }
    });

    //パスワード確認
    $('#inputConfirmationPassword').on('keyup', function(){
        let inputPassword = $('#inputPassword').val();
        let inputConfirmationPassword = $('#inputConfirmationPassword').val();
        let error_msg = $('#password_msg');
        console.log(inputConfirmationPassword);
        if (inputConfirmationPassword == '') {
            error_msg.text("")
        } else if (inputPassword != inputConfirmationPassword) {
            error_msg.text("パスワードが一致していません")
        } else {
            error_msg.text("確認パスワードOK")
        }
    });

    //住所取得
    $('#zipcode_btn').on('click', function() {
        $.ajax({
            url: "https://zipcoda.net/api",
            type: "GET",
            dataType: "jsonp",
            data: {
                zipcode: $('#zipcode').val()
            },
            async: true
        }).done(function(data){
            console.log(data);
            $('#zipcode_error').text('');
            $('#address').val(data.items[0].pref + data.items[0].address);
        }).fail(function(jqXHR, textStatus, errorThrown){
            $('#zipcode_error').text('住所が見つかりません');
            console.log("通信失敗");
        });
    })

    function calcTotalPrice() {
        let size = $('.size:checked').val();
        console.log(size);
        let priceM = Number($('#price_M').val());
        let priceL = Number($('#price_L').val());
        let toppingCount = $('.topping:checked').length
        console.log(priceM);
        console.log(toppingCount);
        let quantity = $('#quantity').val();
        let totalPrice = 0;
        if (size == '0') {
            totalPrice = (priceM + 200 * toppingCount) * quantity;
            $('#total_price').text(totalPrice);
        } else if(size = '1') {
            totalPrice = (priceL + 300 * toppingCount) * quantity;
            $('#total_price').text(totalPrice);
        }
    }

    calcTotalPrice();

    $('input,select').on('change', function() {
       calcTotalPrice(); 
    });
});