package ro.alexandru13a.chatapp.mail;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.Getter;

@Getter
public class MailSender {

  public static String MAIL_HOST = "smtp.gmail.com";
  public static Integer MAIL_PORT = 587;
  public static String MAIL_USERNAME = "alecsutuale@gmail.com";
  public static String MAIL_PASSWORD = "wrabzpxxrwchfwlg";
  public static String SMTP_AUTH = "true";
  public static String SMTP_SECURE = "true";

  public static JavaMailSenderImpl prepareMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    mailSender.setHost(MAIL_HOST);
    mailSender.setPort(MAIL_PORT);
    mailSender.setUsername(MAIL_USERNAME);
    mailSender.setPassword(MAIL_PASSWORD);

    Properties mailProperties = new Properties();
    mailProperties.setProperty("mail.smtp.auth", SMTP_AUTH);
    mailProperties.setProperty("mail.smtp.starttls.enable", SMTP_SECURE);

    mailSender.setJavaMailProperties(mailProperties);

    return mailSender;
  }

  public static void main(String[] args) {
    System.out.println(SMTP_AUTH);
    System.out.println(SMTP_SECURE);
  }


}
