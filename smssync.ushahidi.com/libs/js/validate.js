$(function(){
   // validate the comment form when it is submitted
    $("#freeform").validate({
        errorClass: "error",        
      	    rules: {
      		    name: {
      			    required: true,
      				minlength: 2
      			},
      			email: {
      				required: true,
      				email: true
      			},
      			message: {
      				required: true,
      				minlength: 2
      			},
      			captcha: {required:true}
      		},
      		messages: {
      			name: {
      				required: "Please give us your name.",
      				minlength: "Please use more than two characters."
      			},
      			email: "Please enter a valid email address.",
      			message: {
      				required: "Please include a message."
      			},
      			captcha: {
      				required: "Please prove that you are not a robot."
      			}
      		}
      	});
});
