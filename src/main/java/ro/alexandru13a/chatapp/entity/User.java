package ro.alexandru13a.chatapp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(length = 80, unique = true, nullable = false)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, length = 64)
  private String password;

  @Column(length = 20, name = "phone_number")
  private String phoneNumber;

  private String profilePhoto;

  private boolean enabled;

  @Column(name = "verification_code", length = 64)
  private String verificationCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider", length = 10)
  private AuthenticationType authenticationType;

  @Column(name = "created_time", length = 45)
  private Date createdTime;

  @Column(name = "reset_password_token", length = 30)
  private String resetPasswordToken;

  @ManyToMany
  @JoinTable(name = "user_friends",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "friend_id"))
          @OrderBy("id ASC")
  private Set<User> friends = new HashSet<>();
  
  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Transient
  public String getUserImagePath() {
    if (id == null || profilePhoto == null)
      return "/images/status/user.png";

    return "/user-photo/" + this.id + "/" + this.profilePhoto;
  }

}
