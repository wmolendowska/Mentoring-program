package com.hacktyki.mentoring.user.model;

import com.hacktyki.mentoring.user.model.repository.entity.User;
import lombok.Data;

@Data
public class SimpleUser {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String lastName;
    private AuthorityType role;

    public SimpleUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.role = user.getRole();
    }

}
