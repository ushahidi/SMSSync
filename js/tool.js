
/**
 * This is a quick hack to provide QR code generator
 */
$(document).ready(function() {
	var form = $("#qr-code-generator");
	$.validator.setDefaults({
		errorPlacement: function(error, element) {
			// if the input has a prepend or append element, put the validation msg after the parent div
			if(element.parent().hasClass('input-prepend') || element.parent().hasClass('input-append')) {
				error.insertAfter(element.parent());
					// else just place the validation message immediatly after the input
			} else {
				error.insertAfter(element);
			}
		},
		errorElement: "small", // contain the error msg in a small tag
		wrapper: "div", // wrap the error message and small tag in a div
			highlight: function(element) {
					$(element).closest('.control-group').addClass('error'); // add the Bootstrap error class to the control group
			},
		success: function(element) {
			$(element).closest('.control-group').removeClass('error'); // remove the Boostrap error class from the control group
		}
	});

	form.validate(
	{
		rules: {
			title: {
				minlength: 4,
				required: true
			},
			url: {
				url: true,
				required: true
			},
			secret: {
				minlength: 3,
				require: false
			}
		}
	});
	form.submit(function( e ) {
		e.preventDefault();
		if(form.valid()) {
			var data = {};
			form.serializeArray().map(function(x){data[x.name] = x.value;});
			var qr_code_result = $('#qr-code-result');
			var qrcode = JSON.stringify(data);
			qr_code_result.toggle(true);
			qr_code_result.text('');
			qr_code_result.qrcode({width: 350,height: 350, text: qrcode, render: "canvas"});
			qr_code_result.append("<p>Launch SMSsync on your phone then scan this QR code</p>");
			var show_qrcode = $('#qr-code');
			show_qrcode.toggle(true);
			show_qrcode.text('');
			show_qrcode.append('<p><code>'+qrcode+'</p></code>');
			form.trigger("reset");
		}
	});
});