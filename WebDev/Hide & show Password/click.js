let eyeicon = document.getElementById("eyeicon");
let password = document.getElementById("password");

eyeicon.onclick = function () {
    if (password.type === "password") {
        password.type = "text"; // Corrected assignment
        eyeicon.src = "img/open.png";
    } else {
        password.type = "password"; // Corrected assignment
        eyeicon.src = "img/close.png";
    }
};
