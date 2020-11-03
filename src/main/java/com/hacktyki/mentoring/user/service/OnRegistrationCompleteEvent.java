package com.hacktyki.mentoring.user.service;

import com.hacktyki.mentoring.user.model.repository.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String url;
    private User user;

    public OnRegistrationCompleteEvent(String url, User user) {
        super(user);
        this.url = url;
        this.user = user;
    }
}
