package ro.alexandru13a.chatapp.user;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import ro.alexandru13a.chatapp.entity.AuthenticationType;
import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.exceptions.UserNotFoundException;

@Service
@Transactional
public class UserService {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean isEmailUnique(String email) {
    User user = userRepository.findByEmail(email);
    return user == null;
  }

  public boolean isUsernameUnique(String username){
    User user = userRepository.getUserByUsername(username);
    return user == null;
  }

  public void addFriend(Integer userId, Integer friendId) {
    User user = getUserById(userId);
    User friend = getUserById(friendId);

    if (user != null && friend != null && !user.equals(friend)) {
      user.getFriends().add(friend);
      friend.getFriends().add(user);
      userRepository.save(user);
    }
  }

   public Set<User> getFriends(Integer userId) {
        User user = getUserById(userId);
        if (user != null) {
            return user.getFriends();
        } else {
            return new HashSet<>();
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
      User user = getUserById(userId);
      User friend = getUserById(friendId);

      if (user != null && friend != null && user.getFriends().contains(friend)) {
          user.getFriends().remove(friend);
          friend.getFriends().remove(user);
          userRepository.save(user); 
      }
  }

  public void registerUser(User user) {
    encodePassword(user);
    user.setEnabled(false);
    user.setCreatedTime(new Date());
    user.setAuthenticationType(AuthenticationType.DATABASE);

    String randomCode = RandomString.make(64);
    user.setVerificationCode(randomCode);

    userRepository.save(user);

  }

  public User updateUser(User userInForm, String newUsername) {
    User userInDb = userRepository.findById(userInForm.getId()).get();

    if (userInDb.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
      if (!userInForm.getPassword().isEmpty()) {
        encodePassword(userInForm);
      } else {
        userInForm.setPassword(userInDb.getPassword());
      }
    } else {
      userInForm.setPassword(userInDb.getPassword());
    }

    if (newUsername.isEmpty() || newUsername == null) {
      userInForm.setUsername(userInDb.getUsername());
    } else {
      userInForm.setUsername(newUsername);
    }

    userInForm.setFriends(userInDb.getFriends());
    userInForm.setFirstName(userInDb.getFirstName());
    userInForm.setLastName(userInDb.getLastName());
    userInForm.setEmail(userInDb.getEmail());

    if(userInDb.getPhoneNumber().isEmpty() || userInDb.getPhoneNumber() == null){
      userInDb.setPhoneNumber(userInForm.getPhoneNumber());
    }else{
      userInForm.setPhoneNumber(userInDb.getPhoneNumber());
    }

    userInForm.setEnabled(userInDb.isEnabled());
    userInForm.setCreatedTime(userInDb.getCreatedTime());
    userInForm.setVerificationCode(userInDb.getVerificationCode());
    userInForm.setAuthenticationType(userInDb.getAuthenticationType());
    userInForm.setResetPasswordToken(userInDb.getResetPasswordToken());

    return userRepository.save(userInForm);
  }

  public void save(User user) {
    userRepository.save(user);
  }

  public User getUserById(Integer id) {
    return userRepository.findById(id).get();
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }


  public List<User> getUsersByUsername(String keyword) {
    return userRepository.findByUsername(keyword);
  }

  public boolean verify(String verificationCode) {
    User user = userRepository.findByVerificationCode(verificationCode);

    if (user == null || user.isEnabled()) {
      return false;
    } else {
      userRepository.enabled(user.getId());
      return true;
    }
  }

  private void encodePassword(User user) {
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
  }

  public void updateAuthenticationType(User user, AuthenticationType type) {
    if (!user.getAuthenticationType().equals(type)) {
      userRepository.updateAuthenticationType(user.getId(), type);
    }
  }

  public void addNewUserUponOAuthLogin(String name, String email, AuthenticationType authenticationType) {

    User user = new User();
    user.setEmail(email);
    setName(name, user);

    user.setEnabled(true);
    user.setCreatedTime(new Date());
    user.setAuthenticationType(authenticationType);
    user.setPassword("");
    user.setPhoneNumber("");
    user.setUsername(name);

    userRepository.save(user);

  }

  private void setName(String name, User user) {
    String[] nameArray = name.split(" ");

    if (nameArray.length < 2) {
      user.setFirstName(name);
      user.setLastName("");
    } else {
      String firstName = nameArray[0];
      user.setFirstName(firstName);
      String lastName = name.replaceFirst(firstName + " ", "");
      user.setLastName(lastName);
    }
  }

  public String updateResetPasswordToken(String email) throws UserNotFoundException {

    User user = userRepository.findByEmail(email);
    if (user != null) {
      String token = RandomString.make(30);
      user.setResetPasswordToken(token);
      userRepository.save(user);
      return token;
    } else {
      throw new UserNotFoundException("Could not find any user with the email: " + email);
    }
  }

  public User getByResetPasswordToken(String token) {
    return userRepository.findByResetPasswordToken(token);
  }

  public void updatePassword(String token, String newPassword) throws UserNotFoundException {
    User user = userRepository.findByResetPasswordToken(token);

    if (user == null) {
      throw new UserNotFoundException("No user find: Invalid Token");
    }

    user.setPassword(newPassword);
    user.setResetPasswordToken(null);
    encodePassword(user);
    userRepository.save(user);
  }

}
