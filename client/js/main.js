/**
 * Created by anthony on 20/12/2016.
 */

var api = 'http://0.0.0.0:8080';
var key = '3cf0e880-a782-4ce6-a63c-7ae95891051f';


var placesAutocomplete = places({
    container: document.querySelector('#address-input')
});

$("#withdrawl").click(function (e) {
    var account = $('#account_number').val();
    var amount = $('#amount').val();

    var params = {
        account: account,
        amount: amount,
        api_key: key
    };

    params = jQuery.param(params);
    console.log(params);

    url = api + '/api/withdrawl?' + params;
    console.log(url);

    $.ajax({
        url: url,
        dataType: 'text',
        type: 'POST',
        success: function (data) {
            console.log(data);
        },
        error: function (xhr, status, error) {
            console.log(xhr);
            console.log(status);
            console.log(error);
        }
    });
});


$("#add-customer").click(function (e) {


    var customer_name = $('#name').val();
    var email = $('#email').val();
    var password = $('#password').val();
    var address = $('#address-input').val();
    var option = $("#custom-select").val();

    var customer = {
        name: customer_name,
        email: email,
        address: address,
        password: password,
        account_type: option
    };

    customer = jQuery.param(customer);
    console.log(customer);

    url = api + '/api/customer/create?' + customer;
    console.log(url);

    $.ajax({
        url: url,
        dataType: 'text',
        type: 'POST',
        success: function (data) {
            console.log(data);
        },
        error: function (xhr, status, error) {
            console.log(xhr);
            console.log(status);
            console.log(error);
        }
    });


});