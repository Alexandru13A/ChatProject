package ro.alexandru13a.chatapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.user.UserRepository;


public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User customer = userRepository.findByEmail(email);
    if (customer == null) {
      throw new UsernameNotFoundException("No customer found with email: " + email);
    }
    return new CustomUserDetails(customer);

  }

}
