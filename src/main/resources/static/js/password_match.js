function checkPasswordMatch(confirmPassword) {
  if (confirmPassword.value != $("#password").val()) {
    confirmPassword.setCustomValidity("Password doesn't match !");
  } else {
    confirmPassword.setCustomValidity("");
  }
}