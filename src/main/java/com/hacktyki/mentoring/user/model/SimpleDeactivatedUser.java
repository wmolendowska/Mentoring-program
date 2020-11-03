package com.hacktyki.mentoring.user.model;

import com.hacktyki.mentoring.user.model.repository.entity.User;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class SimpleDeactivatedUser {

    private Long id;
    private String username;
    private String email;
    private String name;
    private String lastName;
    private ZonedDateTime deactivationTime;

    public SimpleDeactivatedUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.deactivationTime = user.getDeactivationTime();
    }
}
