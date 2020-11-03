package com.hacktyki.mentoring;

import org.h2.security.auth.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MentoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentoringApplication.class, args);
    }

}
