package com.hacktyki.mentoring.user.model;

import lombok.Data;

@Data
public class AuthenticationRequest {


    private String username;
    private String password;

//    public AuthenticationRequest(User user) {
//        this.username = user.getUsername();
//        this.password = user.getPassword();
//    }


//    public AuthenticationRequest(String username, String password) {
//        this.username = username;
//        this.password = password;
//    }


}
