package com.hacktyki.mentoring.configuration;


import com.hacktyki.mentoring.jwt.JwtConfiguration;
import com.hacktyki.mentoring.jwt.JwtTokenVerifier;
import com.hacktyki.mentoring.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.hacktyki.mentoring.user.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    private final MyUserDetailsService myUserDetailsService;
    private final SecretKey secretKey;
    private final JwtConfiguration jwtConfiguration;

    public SecurityConfiguration(MyUserDetailsService myUserDetailsService, SecretKey secretKey, JwtConfiguration jwtConfiguration) {
        this.myUserDetailsService = myUserDetailsService;
        this.secretKey = secretKey;
        this.jwtConfiguration = jwtConfiguration;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(this.myUserDetailsService);

        return daoAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().headers().and().csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfiguration, secretKey))
                .addFilterAfter(new JwtTokenVerifier(jwtConfiguration, secretKey), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/newUser/registrationConfirm").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/newUser").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/students").hasAnyRole("ADMIN", "MENTOR")
                .antMatchers(HttpMethod.POST, "/releaseStudent").hasAnyRole("ADMIN", "MENTOR")
                .antMatchers(HttpMethod.GET, "/mentor/**").hasRole("MENTOR")
                .antMatchers(HttpMethod.GET, "/student/**").hasRole("STUDENT")
                .antMatchers(HttpMethod.POST, "/changePassword").hasAnyRole("ADMIN", "MENTOR", "STUDENT")
                .antMatchers(HttpMethod.POST, "/cancelMeeting").hasAnyRole("MENTOR", "STUDENT");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
