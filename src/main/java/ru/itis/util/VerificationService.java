package ru.itis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.api.EmailService;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class VerificationService {

    private final EmailService emailService;

    @Autowired
    public VerificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public String sendVerificationCode(String email) {
        String verificationCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        emailService.sendVerificationCode(email, verificationCode);
        return verificationCode;
    }
}
