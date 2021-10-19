// Change the type of input to password or text
function show(pwd_id) {
    const password = document.getElementById(pwd_id);
    if (password.type === "password") {
        password.type = "text";
    } else {
        password.type = "password";
    }
}

function goToForgotPassword() {
    window.location.replace('/pages/auth/forgot-password');
}

function goToRegistration() {
    window.location.replace('/pages/auth/register');
}
