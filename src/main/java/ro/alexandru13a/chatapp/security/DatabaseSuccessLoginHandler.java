package ro.alexandru13a.chatapp.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ro.alexandru13a.chatapp.entity.AuthenticationType;
import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.user.UserService;

@Component
public class DatabaseSuccessLoginHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  
  private UserService userService;

  public void setUserService(UserService userService){
    this.userService = userService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authentication) throws IOException, ServletException {

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User user = userDetails.getUser();
    userService.updateAuthenticationType(user, AuthenticationType.DATABASE);
    super.onAuthenticationSuccess(request, response, chain, authentication);
  }

}
