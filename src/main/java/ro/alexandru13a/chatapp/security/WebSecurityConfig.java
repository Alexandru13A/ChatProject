package ro.alexandru13a.chatapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ro.alexandru13a.chatapp.security.oauth2.OAuth2LoginSuccessHandler;
import ro.alexandru13a.chatapp.security.oauth2.UserOAuth2Service;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  private UserOAuth2Service auth2Service;
  private OAuth2LoginSuccessHandler auth2LoginSuccessHandler;
  private DatabaseSuccessLoginHandler databaseSuccessLoginHandler;

  @Autowired
  public void setDatabaseSuccessLoginHandler(DatabaseSuccessLoginHandler databaseSuccessLoginHandler) {
    this.databaseSuccessLoginHandler = databaseSuccessLoginHandler;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new CustomUserDetailsService();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService());
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.authenticationProvider(authenticationProvider());

    http.authorizeHttpRequests(
        (requests) -> requests.requestMatchers("/css/**", "/images/**", "/js/**", "/webjars/**").permitAll()
            .requestMatchers("/account_details", "/update_account")
            .authenticated().anyRequest().permitAll())
        .formLogin(login -> login.loginPage("/login")
            .usernameParameter("email")
            .successHandler(databaseSuccessLoginHandler)
            .permitAll())
        .logout(logout -> logout.permitAll())
        .rememberMe(remember -> remember.userDetailsService(userDetailsService()));

    return http.build();
  }

}
