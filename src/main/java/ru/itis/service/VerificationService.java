package ru.itis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.emailsender.EmailSender;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class VerificationService {

    private final EmailSender emailSender;

    @Autowired
    public VerificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public String sendVerificationCode(String email) {
        String verificationCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        emailSender.sendVerificationCode(email, verificationCode);
        return verificationCode;
    }
}
