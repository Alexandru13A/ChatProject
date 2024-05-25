package ro.alexandru13a.chatapp.message.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.message.MessageService;
import ro.alexandru13a.chatapp.user.UserService;

@Controller
public class MessageController {

  private UserService userService;
  private MessageService messageService;


  public MessageController(UserService userService, MessageService messageService) {
    this.userService = userService;
    this.messageService = messageService;
  }

   @PostMapping("/send_message")
  public String sendMessage(HttpServletRequest request, @RequestParam("receiverId") Integer receiverId, @RequestParam("content") String content) {
    String email = request.getUserPrincipal().getName();
    User sender = userService.getUserByEmail(email);
    User receiver = userService.getUserById(receiverId);

    messageService.saveMessage(content, sender, receiver);
    return "redirect:/home?receiverId=" + receiverId;
  }



  
}