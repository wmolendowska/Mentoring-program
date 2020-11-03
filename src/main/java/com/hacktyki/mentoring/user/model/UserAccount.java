package com.hacktyki.mentoring.user.model;


import com.hacktyki.mentoring.user.model.AuthorityType;
import lombok.Data;

@Data
public class UserAccount{
    private String email;
    private String name;
    private String lastName;
    private AuthorityType role;

    public UserAccount() {
    }

    public UserAccount(String email, String name, String lastName, AuthorityType role) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.role = role;
    }
}


