package com.hacktyki.mentoring.user.service;

import com.hacktyki.mentoring.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UserDeleteService {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final UserService userService;

    public UserDeleteService(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void setup() {
        executorService.scheduleAtFixedRate(deleteUser(), 10, 10, TimeUnit.DAYS);
    }


    public Runnable deleteUser() {
        return () -> {
            userService.deleteUser(ZonedDateTime.now().minusYears(1));
            userService.deleteVerificationToken(ZonedDateTime.now().minusDays(10));
        };
    }

}
