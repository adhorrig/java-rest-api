/**
 * Created by anthony on 20/12/2016.
 */

var api = 'http://0.0.0.0:8080';
var key = '3cf0e880-a782-4ce6-a63c-7ae95891051f';


var placesAutocomplete = places({
    container: document.querySelector('#address-input')
});

function get_data(method, url){
    console.log(url);
    console.log(method);
    $.ajax({
        url: url,
        dataType: 'text',
        type: method,
        success: function (data) {
            console.log(data);
            $('.message-block').show("slide");
            $('.message').text(data);
        },
        error: function (xhr, status, error) {
            $('.message-block').show("slide");
            console.log(xhr);
            console.log(status);
            console.log(error);
            $('.data').text(xhr);
        }
    });

}


$("#transfer").click(function (e) {
    var from = $('#from').val();
    var to = $('#to').val();
    var amount = $('#amount').val();

    var params = {
        from: from,
        to: to,
        amount: amount,
        api_key: key
    };

    get_data('POST', api + '/api/transfer/create?' + jQuery.param(params));
});


$("#get-balance").click(function (e) {
    var account = $('#account').val();

    var params = {
        account_number: account,
        api_key: key
    };

    get_data('GET', api + '/api/balance?' + jQuery.param(params));
});


$("#withdrawl").click(function (e) {
    var account = $('#account_number').val();
    var amount = $('#amount').val();

    var params = {
        account: account,
        amount: amount,
        api_key: key
    };

    get_data('POST', api + '/api/withdrawl?' + jQuery.param(params));
});


$("#add-customer").click(function (e) {
    var customer_name = $('#name').val();
    var email = $('#email').val();
    var password = $('#password').val();
    var address = $('#address-input').val();
    var option = $("#custom-select").val();

    var params = {
        name: customer_name,
        email: email,
        address: address,
        password: password,
        account_type: option,
		api_key: key
    };

    get_data('POST', api + '/api/customer/create?' + jQuery.param(params));


});