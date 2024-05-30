package ro.alexandru13a.chatapp.message;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import ro.alexandru13a.chatapp.entity.Message;
import ro.alexandru13a.chatapp.entity.User;

@Service
public class MessageService {

  private MessageRepository messageRepository;

  public MessageService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public Message saveMessage(String content, User sender, User receiver) {
    Message message = new Message();

    message.setContent(content);
    message.setUserSender(sender);
    message.setUserReceiver(receiver);
    message.setTimestamp(LocalDateTime.now());

    return messageRepository.save(message);
  }

  public List<Message> getMessageBetweenUsers(User sender, User receiver) {
    return messageRepository.findBySenderAndReceiver(sender, receiver);
  }

  public Message getLastMessage(User sender , User receiver){
    return messageRepository.getLastMessage(sender, receiver);
  }

}
