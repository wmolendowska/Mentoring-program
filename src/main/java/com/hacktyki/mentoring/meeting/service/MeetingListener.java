package com.hacktyki.mentoring.meeting.service;

import com.hacktyki.mentoring.mailService.MailService;
import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MeetingListener implements ApplicationListener<OnMeetingBookedOrCancelledEvent> {
    private final MailService mailService;

    public MeetingListener(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void onApplicationEvent(OnMeetingBookedOrCancelledEvent event) {

            this.confirmMeeting(event);

    }

    private void confirmMeeting(OnMeetingBookedOrCancelledEvent event) {
        User student = event.getStudent();
        User mentor = event.getMentor();
        Meeting meeting = event.getMeeting();

        if(event.isCancelled()) {
            sendConfirmationMail(student, mentor, meeting.getTime(), "Cancelled meeting", " is cancelled.");
            sendConfirmationMail(mentor, student, meeting.getTime(), "Cancelled meeting", " is cancelled.");
        } else {
            sendConfirmationMail(student, mentor, meeting.getTime(), "Meeting confirmation", ".");
            sendConfirmationMail(mentor, student, meeting.getTime(), "Meeting confirmation", ".");
        }
    }

    private void sendConfirmationMail(User to, User with, ZonedDateTime meetingTime, String subject, String cancellationText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        mailService.sendMessage(to.getEmail(), subject, new StringBuilder().append("Your meeting with ")
                .append(with.getName()).append(" ").append(with.getLastName()).append(", is scheduled for ")
                .append(meetingTime.format(formatter)).append(cancellationText).toString());
    }

    }

