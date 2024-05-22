package ro.alexandru13a.chatapp;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.user.UserService;

@Controller
public class MainController {

  private UserService userService;

  public MainController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/")
  public String welcomePage(HttpServletRequest request,Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return "index";
    }
    String email = request.getUserPrincipal().getName();
    System.out.println(email);
    User user = userService.getUserByEmail(email);
    model.addAttribute("userImage", user.getUserImagePath());
    return "chat/main_chat_window";
  }

  @GetMapping("/login")
  public String loginPage() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return "user/login";
    }
    return "redirect:/";
  }


}
