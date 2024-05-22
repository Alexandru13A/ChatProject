package ro.alexandru13a.chatapp.user.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import ro.alexandru13a.chatapp.entity.User;
import ro.alexandru13a.chatapp.mail.MailSender;
import ro.alexandru13a.chatapp.user.UserService;
import ro.alexandru13a.chatapp.utility.Utility;

@Controller
public class UserController {

  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
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

  @GetMapping("/user_details")
  public String viewUserDetails(Model model, HttpServletRequest request) {

    String email = Utility.getEmailOfAuthenticatedUser(request);
    User user = userService.getUserByEmail(email);
    model.addAttribute("user", user);
    model.addAttribute("fullName", user.getFullName());


    return "user/user_details";

  }

}
