package com.example.myrecipai.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class EmailUtil {
@Autowired
private JavaMailSender mailSender;
    public void sendMailWithCode(String sendTo, String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendTo);
        message.setSubject("MyRecipAI");
        message.setText("Code " + code);
        mailSender.send(message);
    }
}

