package ro.alexandru13a.chatapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String content;
  private String photo;

  private LocalDateTime timestamp;

  @ManyToOne
  @JoinColumn(name = "sender_id")
  private User userSender;

  @ManyToOne
  @JoinColumn(name = "receiver_id")
  private User userReceiver;

  @Transient
  public String getPhotoImagePath() {
    if (id == null || photo == null)
      return null;

    return "/message-photo/" + this.userSender.getUsername() + "/" + this.id + "/" + this.photo;
  }

}
