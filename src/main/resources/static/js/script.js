// Field name and properties should be same to display the msg

//Complete Validation here

$(function() {
    var $userRegister = $("#userRegister");

    $userRegister.validate({
        rules: {
            name: {
                required: true,
                letteronly: true
            },
            email: {
                required: true,
                email: true
            },
            mobileNumber: {
                required: true,
                phoneNumber: true
            },
//            password: {
//                required: true,
//                strongPassword: true
//            },
//            confirmPassword: {
//                required: true,
//                equalTo: '[name="password"]'
//            },
            address: {
                required: true,
                letteronly: true
            },
            state: {
                required: true,
                letteronly: true
            },
            city: {
                required: true,
                letteronly: true
            },
            pincode: {
                required: true,
                pincode: true
            },
            img: {
                required: true
            }
        },
        messages: {
            name: {
                required: 'Full Name is required',
                letteronly: 'Only letters, spaces, and hyphens allowed'
            },
            email: {
                required: 'Email is required',
                email: 'Please enter a valid email'
            },
            mobileNumber: {
                required: 'Mobile Number is required',
                phoneNumber: 'Enter a valid 10-digit mobile number'
            },
//            password: {
//                required: 'Password is required',
//                strongPassword: 'Password must be strong (min 8 chars, special char, number)'
//            },
//            confirmPassword: {
//                required: 'Confirm your password',
//                equalTo: 'Passwords do not match'
//            },
            address: {
                required: 'Address is required',
                letteronly: 'Only letters, spaces, and hyphens allowed'
            },
            state: {
                required: 'State is required',
                letteronly: 'Only letters, spaces, and hyphens allowed'
            },
            city: {
                required: 'City is required',
                letteronly: 'Only letters, spaces, and hyphens allowed'
            },
            pincode: {
                required: 'Pincode is required',
                pincode: 'Enter a valid 6-digit pincode'
            },
            img: {
                required: 'Profile image is required'
            }
        },
        errorElement: 'div',
        errorClass: 'text-danger',
        highlight: function(element) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function(element) {
            $(element).removeClass('is-invalid');
        }
    });
});

// Allow only letters, space, hyphen
jQuery.validator.addMethod("letteronly", function(value, element) {
    return this.optional(element) || /^[a-zA-Z\s-]+$/.test(value);
}, "Only letters, spaces, and hyphens are allowed.");


// Strong password: min 8 chars, upper, lower, number, special char
jQuery.validator.addMethod("strongPassword", function(value, element) {
    return this.optional(element) || /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(value);
}, "Password must contain at least 8 characters, including uppercase, lowercase, number, and special character.");


// 10-digit Indian mobile number
jQuery.validator.addMethod("phoneNumber", function(value, element) {
    return this.optional(element) || /^[6-9]\d{9}$/.test(value);
}, "Please enter a valid 10-digit mobile number.");


// 6-digit pincode
jQuery.validator.addMethod("pincode", function(value, element) {
    return this.optional(element) || /^\d{6}$/.test(value);
}, "Enter a valid 6-digit pincode.");



