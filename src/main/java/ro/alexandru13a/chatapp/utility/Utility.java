package ro.alexandru13a.chatapp.utility;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;
import ro.alexandru13a.chatapp.security.oauth2.UserOAuth2;

public class Utility {

  public static String getSiteURL(HttpServletRequest request) {
    String siteURL = request.getRequestURL().toString();
    return siteURL.replace(request.getServletPath(), "");
  }

    public static String getEmailOfAuthenticatedUser(HttpServletRequest request) {

    Object principal = request.getUserPrincipal();
    if (principal == null)
      return null;

    String customerEmail = null;
    if (principal instanceof UsernamePasswordAuthenticationToken
        || principal instanceof RememberMeAuthenticationToken) {
      customerEmail = request.getUserPrincipal().getName();
    } else if (principal instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
      UserOAuth2 auth2User = (UserOAuth2) oauth2Token.getPrincipal();
      customerEmail = auth2User.getEmail();
    }
    return customerEmail;
  }
}
