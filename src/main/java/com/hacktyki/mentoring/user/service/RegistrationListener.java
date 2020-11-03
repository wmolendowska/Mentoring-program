package com.hacktyki.mentoring.user.service;

import com.hacktyki.mentoring.mailService.MailService;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.repository.entity.VerificationToken;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private UserService userService;
    private MailService mailService;

    public RegistrationListener(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = userService.createVerificationToken(user, token);

        String subject = "Registration confirmation";
        String confirmationUrl = event.getUrl() + "/registrationConfirm?token=" + token;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

        mailService.sendMessage(user.getEmail(), subject, new StringBuilder()
                .append("Your account on mentoring platform has been created.").append("\r\n")
                .append("Your username: ").append(user.getUsername()).append("\r\n")
                .append("To confirm registration and set password click link: ").append("\r\n")
                .append(confirmationUrl).append("\r\n").append("Link expires ")
                .append(verificationToken.getExpiryDate().format(formatter)).toString());
    }
}
