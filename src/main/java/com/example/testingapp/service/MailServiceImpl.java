package com.example.testingapp.service;

import com.example.testingapp.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendVerificationCode(User user) {
        String toAddress = user.getEmail();
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you!";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = "http://localhost:8080/user/verification?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        try {
            helper.setTo(toAddress);
            helper.setSubject(subject);
//            helper.addAttachment();
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong mail setup");
        }

        mailSender.send(message);
        log.info("Message is sent to {}", user.getEmail());
    }

    @Override
    public void sendUrl(User user) {
        String toAddress = user.getEmail();
        String subject = "Please go to URL";
        String token = RandomStringUtils.randomNumeric(6);
        String content = "Dear [[name]],<br>"
                + "Please click the link below to change your password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">CHANGE PASSWORD</a></h3>"
                + "Thank you!";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        content = content.replace("[[name]]", user.getUsername());
        //need frontEnd
        String changeURL = "http://localhost:8080/user/changePassword?email=" + user.getEmail() + "&token=" + token;
        content = content.replace("[[URL]]", changeURL);

        try {
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong mail setup");
        }

        mailSender.send(message);
        log.info("Message is sent to {}", user.getEmail());
    }
}
