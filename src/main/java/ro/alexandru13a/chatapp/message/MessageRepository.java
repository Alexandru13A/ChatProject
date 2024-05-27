package ro.alexandru13a.chatapp.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ro.alexandru13a.chatapp.entity.Message;
import ro.alexandru13a.chatapp.entity.User;

public interface MessageRepository extends JpaRepository<Message, Integer> {
  

  @Query("SELECT m FROM Message m WHERE (m.userSender = ?1 AND m.userReceiver = ?2) OR (m.userSender = ?2 AND m.userReceiver = ?1) ORDER BY m.timestamp ASC")
  List<Message> findBySenderAndReceiver(User userSender, User userReceiver);
}
