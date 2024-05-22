package ro.alexandru13a.chatapp.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import ro.alexandru13a.chatapp.entity.AuthenticationType;
import ro.alexandru13a.chatapp.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void testCreateUser() {

    ro.alexandru13a.chatapp.entity.User user = new User();
    user.setFirstName("Daniel");
    user.setLastName("Dan");
    user.setUsername("daniel76");
    user.setEmail("daniel@gmail.com");
    user.setPassword("daniel1234");
    user.setAuthenticationType(AuthenticationType.DATABASE);

    User savedUser = userRepository.save(user);

    User existUser = entityManager.find(User.class, savedUser.getId());

    assertThat(existUser.getEmail()).isEqualTo(user.getEmail());

  }

  @Test
  public void testFindUserByEmail(){
      String email = "alexandru@gmail.com";
      User user = userRepository.findByEmail(email);
      assertThat(user).isNotNull();


  }

}
