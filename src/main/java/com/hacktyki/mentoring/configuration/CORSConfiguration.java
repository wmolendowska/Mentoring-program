package com.hacktyki.mentoring.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class CORSConfiguration implements WebMvcConfigurer {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "OPTIONS")
                        .allowedHeaders("*")
                        .allowedOrigins("http://localhost:4200")
                        .allowCredentials(true);
            }

}
