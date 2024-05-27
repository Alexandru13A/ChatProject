package ro.alexandru13a.chatapp.user.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.mail.MailSender;
import ro.alexandru13a.chatapp.message.MessageService;
import ro.alexandru13a.chatapp.security.CustomUserDetails;
import ro.alexandru13a.chatapp.security.oauth2.UserOAuth2;
import ro.alexandru13a.chatapp.user.UserService;
import ro.alexandru13a.chatapp.utility.FileUploadUtil;
import ro.alexandru13a.chatapp.utility.Utility;

@Controller
public class UserController {

  private UserService userService;
  private MessageService messageService;

  public UserController(UserService userService, MessageService messageService) {
    this.userService = userService;
    this.messageService = messageService;

  }

  @GetMapping("/home")
  public String chatPage(HttpServletRequest request, Model model,@RequestParam(value = "receiverId", required = false) Integer receiverId) {

    String email = request.getUserPrincipal().getName();
    User principal = userService.getUserByEmail(email);

    List<User> listFriends = userService.getFriends(principal.getId()).stream()
    .collect(Collectors.toList());
  
    model.addAttribute("principal", principal);
    model.addAttribute("listFriends", listFriends);
    model.addAttribute("userImage", principal.getUserImagePath());

    if(receiverId != null){
      User receiver = userService.getUserById(receiverId);
      model.addAttribute("receiver", receiver);
      model.addAttribute("messages", messageService.getMessageBetweenUsers(principal, receiver));
     
    }else{
      model.addAttribute("receiver", null);
    }

    return ("chat/main_chat_window");
  }

  @GetMapping("/add_friend")
  public String searchUsers(HttpServletRequest request, Model model, @Param("keyword") String keyword) {
    String email = request.getUserPrincipal().getName();
    User loggedUser = userService.getUserByEmail(email);

    List<User> filteredUserList = new ArrayList<>();
    List<User> friends = userService.getFriends(loggedUser.getId()).stream()
        .collect(Collectors.toList());

    filteredUserList.addAll(
        userService.getUsersByUsername(keyword).stream()
            .filter(user -> !loggedUser.getUsername().equals(user.getUsername()))
            .filter(user -> !friends.contains(user))
            .collect(Collectors.toList()));

    model.addAttribute("users", filteredUserList);
    model.addAttribute("keyword", keyword);
    return "user/add_friend";

  }

  @PostMapping("/add_friend/{id}")
  public String addFriend(@PathVariable("id") Integer id, HttpServletRequest request) {
    String email = request.getUserPrincipal().getName();
    User loggedUser = userService.getUserByEmail(email);
    User userToAddAtFriends = userService.getUserById(id);

    if (userToAddAtFriends == null) {
      return "error/user_not_found";
    }

    userService.addFriend(loggedUser.getId(), userToAddAtFriends.getId());

    return "redirect:/home";
  }

  @GetMapping("/register")
  public String getRegisterPage(Model model) {
    model.addAttribute("user", new User());
    return "user/register/register_form";
  }

  @PostMapping("/process_register")
  public String processRegistration(User user, Model model, HttpServletRequest request)
      throws UnsupportedEncodingException, MessagingException {
    userService.registerUser(user);
    sendVerificationEmail(request, user);

    return "user/register/register_success";
  }

  private void sendVerificationEmail(HttpServletRequest request, User user)
      throws UnsupportedEncodingException, MessagingException {

    final String MAIL_FROM = "alecsutuale@gmail.com";
    final String MAIL_SENDER_NAME = "ChatApp Team";

    String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + user.getVerificationCode();
    JavaMailSenderImpl mailSender = MailSender.prepareMailSender();

    String toAddress = user.getEmail();
    String subject = "Chatapp - Activate your account";
    String content = "<!DOCTYPE html>"
        + "<html>"
        + "<head>"
        + "<style>"
        + "body { font-family: Arial, sans-serif; }"
        + ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ccc; border-radius: 10px; }"
        + ".header { background-color: #f8f9fa; padding: 10px 0; text-align: center; font-size: 24px; color: #333; }"
        + ".content { margin: 20px 0; font-size: 16px; line-height: 1.6; }"
        + ".content a { color: #007bff; text-decoration: none; }"
        + ".footer { margin-top: 20px; font-size: 14px; color: #666; text-align: center; }"
        + "</style>"
        + "</head>"
        + "<body>"
        + "<div class='container'>"
        + "<div class='header'>ChatApp</div>"
        + "<div class='content'>"
        + "<p>Hello,</p>"
        + "<p>To be able to connect to the ChatApp application, click on the link below:</p>"
        + "<p><a href=\"" + verifyURL + "\">Activate your account</a></p>"
        + "<p>Thank you,<br>ChatApp Team</p>"
        + "</div>"
        + "<div class='footer'>"
        + "If you didn't create this account, please ignore this email."
        + "</div>"
        + "</div>"
        + "</body>"
        + "</html>";

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(MAIL_FROM, MAIL_SENDER_NAME);
    helper.setTo(toAddress);
    helper.setSubject(subject);
    helper.setText(content, true);

    mailSender.send(message);

    System.out.println("To address: " + toAddress);
    System.out.println("Verify URL: " + verifyURL);
  }

  @GetMapping("/verify")
  public String verifyAccount(@Param("code") String code, Model model) {
    boolean verified = userService.verify(code);
    return "user/register/" + (verified ? "verify_success" : "verify_fail");
  }

  @GetMapping("/account_details")
  public String viewAccountDetails(Model model, HttpServletRequest request) {

    String email = Utility.getEmailOfAuthenticatedUser(request);
    User user = userService.getUserByEmail(email);
    model.addAttribute("user", user);
    model.addAttribute("fullName", user.getFullName());
    return "user/user_details";
  }

  @SuppressWarnings("null")
  @PostMapping("/update_account")
  public String updateAccount(User user, RedirectAttributes redirectAttributes, HttpServletRequest request,
      @RequestParam("image") MultipartFile multipartFile, @RequestParam("newUsername") String newUsername)
      throws IOException {
       

        if(!userService.isUsernameUnique(newUsername)){
          redirectAttributes.addFlashAttribute("message","The username "+newUsername+" it's already taken");
          return "redirect:/account_details";
        }

    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
      user.setProfilePhoto(fileName);
      User savedUser = userService.updateUser(user, newUsername);

      String uploadDir = "user-photo/" + savedUser.getId();

      FileUploadUtil.cleanDirectory(uploadDir);
      FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    } else {
      if (user.getProfilePhoto().isEmpty())
        user.setProfilePhoto(null);
      userService.updateUser(user, newUsername);
    }
    updateNameForAuthenticatedCustomer(user, request);
    return "redirect:/account_details";
  }

  private void updateNameForAuthenticatedCustomer(User user, HttpServletRequest request) {
    Object principal = request.getUserPrincipal();

    if (principal instanceof UsernamePasswordAuthenticationToken
        || principal instanceof RememberMeAuthenticationToken) {
      CustomUserDetails userDetails = getUserDetailsObject(principal);
      User authenticatedCustomer = userDetails.getUser();
      authenticatedCustomer.setFirstName(user.getFirstName());
      authenticatedCustomer.setLastName(user.getLastName());

    } else if (principal instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
      UserOAuth2 oAuth2User = (UserOAuth2) oauth2Token.getPrincipal();
      String fullName = user.getFirstName() + " " + user.getLastName();
      oAuth2User.setFullName(fullName);
    }
  }

  // Get name from authentication with Facebook/Google
  private CustomUserDetails getUserDetailsObject(Object principal) {
    CustomUserDetails userDetails = null;
    if (principal instanceof UsernamePasswordAuthenticationToken) {
      UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
      userDetails = (CustomUserDetails) token.getPrincipal();
    } else if (principal instanceof RememberMeAuthenticationToken) {
      RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
      userDetails = (CustomUserDetails) token.getPrincipal();
    }
    return userDetails;
  }

}
