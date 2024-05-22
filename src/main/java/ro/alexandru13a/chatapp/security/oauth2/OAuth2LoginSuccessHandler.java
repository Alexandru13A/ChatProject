package ro.alexandru13a.chatapp.security.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ro.alexandru13a.chatapp.entity.AuthenticationType;
import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.user.UserService;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  private UserService userService;

  @Autowired
  @Lazy
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws ServletException, IOException {

    UserOAuth2 auth2User = (UserOAuth2) authentication.getPrincipal();
    String name = auth2User.getName();
    String email = auth2User.getEmail();
    String clientName = auth2User.getClientName();

    AuthenticationType authenticationType = getAuthenticationType(clientName);

    User user = userService.getUserByEmail(email);
    if (user == null) {
      userService.addNewUserUponOAuthLogin(name, email, authenticationType);
    } else {
      auth2User.setFullName(user.getFullName());
      userService.updateAuthenticationType(user, authenticationType);
    }

    super.onAuthenticationSuccess(request, response, authentication);
  }

  private AuthenticationType getAuthenticationType(String clientName) {
    if (clientName.equals("Google")) {
      return AuthenticationType.GOOGLE;
    } else if (clientName.equals("facebook")) {
      return AuthenticationType.FACEBOOK;
    } else {
      return AuthenticationType.DATABASE;
    }
  }
}
