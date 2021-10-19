// Change the type of input to password or text
function show(pwd_id, icon_id, className) {
    const password = document.getElementById(pwd_id);
    const icon = document.getElementById(icon_id);
    if (password.type === "password") {
        password.type = "text";
        icon.className = `fa fa-fw fa-eye field-icon eye-icon ${className}`;
    }
    else {
        password.type = "password";
        icon.className = `fa fa-fw fa-eye-slash field-icon eye-icon ${className}`;
    }
}
