package com.hacktyki.mentoring.user.service;

import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.repository.UserRepository;
import com.hacktyki.mentoring.user.service.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.getUserByUsername(username);
        user.orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
        return user.map(MyUserDetails::new).get();
    }
}
