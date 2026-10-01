package ru.itis.emailsender;

public interface EmailSender {

    void sendVerificationCode(String targetEmail, String code);
}
