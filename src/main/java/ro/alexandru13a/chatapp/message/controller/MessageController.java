package ro.alexandru13a.chatapp.message.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import ro.alexandru13a.chatapp.entity.Message;
import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.message.MessageService;
import ro.alexandru13a.chatapp.user.UserService;
import ro.alexandru13a.chatapp.utility.FileUploadUtil;
import ro.alexandru13a.chatapp.utility.Utility;

@Controller
public class MessageController {

  private UserService userService;
  private MessageService messageService;

  public MessageController(UserService userService, MessageService messageService) {
    this.userService = userService;
    this.messageService = messageService;
  }

  @PostMapping("/send_message")
  public String sendMessage(HttpServletRequest request, @RequestParam("receiverId") Integer receiverId,
      @RequestParam("content") String content, @RequestParam("image") MultipartFile image) throws IOException {
    String email = request.getUserPrincipal().getName();
    User sender = userService.getUserByEmail(email);
    User receiver = userService.getUserById(receiverId);

    if (!image.isEmpty()) {
      String fileName = StringUtils.cleanPath(image.getOriginalFilename());
      Message savedMessage = messageService.savePhoto(fileName,content, sender, receiver);

      String uploadDirectory ="message-photo/"+sender.getUsername()+"/"+savedMessage.getId();
      
      FileUploadUtil.saveFile(uploadDirectory, fileName, image);
    } else {
      messageService.saveMessage(content, sender, receiver);
    }

    return "redirect:/home?receiverId=" + receiverId;
  }

  @PostMapping("/delete_message/{id}")
  public String deleteMessage(HttpServletRequest request, @RequestParam("receiverId") Integer receiverId,
      @RequestParam("messageId") Integer messageId) {

    String email = Utility.getEmailOfAuthenticatedUser(request);
    User sender = userService.getUserByEmail(email);
    User receiver = userService.getUserById(receiverId);

    List<Message> messages = messageService.getMessageBetweenUsers(sender, receiver);

    for (Message messageToDelete : messages) {
      if (messageToDelete.getId() == messageId) {
        messageService.deleteSelectedMessage(messageId);
      }
    }

    return "redirect:/home?receiverId=" + receiverId;
  }

}