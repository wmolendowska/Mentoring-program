package com.hacktyki.mentoring.meeting.service;

import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnMeetingBookedOrCancelledEvent extends ApplicationEvent {
    private User mentor;
    private User student;
    private Meeting meeting;
    private boolean isCancelled;

    public OnMeetingBookedOrCancelledEvent(Meeting meeting, User mentor, User student, boolean isCancelled) {
        super(meeting);
        this.mentor = mentor;
        this.student = student;
        this.meeting = meeting;
        this.isCancelled = isCancelled;
    }
}
