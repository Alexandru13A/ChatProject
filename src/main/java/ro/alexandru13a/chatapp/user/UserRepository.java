package ro.alexandru13a.chatapp.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ro.alexandru13a.chatapp.entity.AuthenticationType;
import ro.alexandru13a.chatapp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  @Query("SELECT u FROM User u WHERE u.email = ?1")
  public User findByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.username = ?1")
  public User getUserByUsername(String username);

  @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', ?1, '%'))")
  public List<User> findByUsername(String username);

  @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
  public User findByVerificationCode(String verificationCode);

  @Query("UPDATE User u SET u.enabled = true , u.verificationCode = null WHERE u.id = ?1")
  @Modifying
  public void enabled(Integer id);

  @Query("UPDATE User u SET u.authenticationType = ?2 WHERE u.id = ?1")
  @Modifying
  public void updateAuthenticationType(Integer userId, AuthenticationType type);

  public User findByResetPasswordToken(String resetPasswordToken);

}
