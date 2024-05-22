package ro.alexandru13a.chatapp.user.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.alexandru13a.chatapp.user.UserService;

@RestController
public class UserRestController {

  private UserService userService;

  public UserRestController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/check_unique_email")
  public String checkForDuplicateEmail(@Param("email") String email) {
    return userService.isEmailUnique(email) ? "OK" : "Duplicated";
  }

}
