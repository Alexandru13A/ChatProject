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
import ro.alexandru13a.chatapp.exceptions.UserNotFoundException;
import ro.alexandru13a.chatapp.mail.MailSender;
import ro.alexandru13a.chatapp.user.UserService;
import ro.alexandru13a.chatapp.utility.Utility;

@Controller
public class ForgotPasswordController {

  private UserService userService;

  public ForgotPasswordController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/forgot_password")
  public String showRequestForm() {
    return "user/forgot_password_form";
  }

  @PostMapping("/forgot_password")
  public String progressRequestPasswordForm(HttpServletRequest request, Model model) {
    String email = request.getParameter("email");

    try {
      String token = userService.updateResetPasswordToken(email);
      String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
      sendEmail(link, email);

      model.addAttribute("message", "We have sent a reset password link to your email."
          + " Check your Inbox or Spam section.");

    } catch (UserNotFoundException e) {
      model.addAttribute("error", e.getMessage());
    } catch (UnsupportedEncodingException | MessagingException e) {
      model.addAttribute("error", "Could not send email.");
    }

    return "user/forgot_password_form";
  }

  private void sendEmail(String link, String email) throws UnsupportedEncodingException, MessagingException {

    final String MAIL_FROM = "alecsutuale@gmail.com";
    final String MAIL_SENDER_NAME = "ChatApp Team";

    JavaMailSenderImpl javaMailSenderImpl = MailSender.prepareMailSender();

    String toAddress = email;
    String subject = "Reset password link";
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
        + "<p>To be able to reset your password, click on the link below:</p>"
        + "<p><a href=\"" + link + "\">Reset your password</a></p>"
        + "<p>Thank you,<br>ChatApp Team</p>"
        + "</div>"
        + "<div class='footer'>"
        + "If you didn't ask for this action, please ignore this email."
        + "</div>"
        + "</div>"
        + "</body>"
        + "</html>";
    ;

    MimeMessage message = javaMailSenderImpl.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(MAIL_FROM, MAIL_SENDER_NAME);
    helper.setTo(toAddress);
    helper.setSubject(subject);
    helper.setText(content, true);

    javaMailSenderImpl.send(message);
  }

  @GetMapping("/reset_password")
  public String showResetPasswordForm(@Param("token") String token, Model model) {
    User user = userService.getByResetPasswordToken(token);

    if (user != null) {
      model.addAttribute("token", token);
    } else {
      model.addAttribute("message", "Invalid Token!");
    }
    return "user/reset_password_form";
  }

  @PostMapping("/reset_password")
  public String processResetPasswordForm(HttpServletRequest request, Model model) {
    String token = request.getParameter("token");
    String password = request.getParameter("password");

    try {
      userService.updatePassword(token, password);
      model.addAttribute("title", "Reset password action");
      model.addAttribute("message", "You have successfully change your password !");
      return "message";

    } catch (UserNotFoundException e) {
      model.addAttribute("message", e.getMessage());
      return "message";
    }
  }

}
