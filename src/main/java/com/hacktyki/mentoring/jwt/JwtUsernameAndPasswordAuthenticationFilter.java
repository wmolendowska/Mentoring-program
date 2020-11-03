package com.hacktyki.mentoring.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;


public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfiguration jwtConfiguration;
    private final SecretKey secretKey;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfiguration jwtConfiguration, SecretKey secretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtConfiguration = jwtConfiguration;
        this.secretKey = secretKey;

    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if(CorsUtils.isCorsRequest(request)){
            response.setHeader("Access-Control-Allow-Origin", "*");
        }

        try {
            UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );
            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            throw  new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))
                .signWith(secretKey)
                .compact();

        response.addHeader("Access-Control-Expose-Headers", "Authorization");



//        Cookie cookie = new Cookie("token", token);
//        cookie.setPath("/");
////        cookie.setSecure(true);
//        cookie.setMaxAge(1800);
////        cookie.setHttpOnly(true);
//        response.addCookie(cookie);

//        ResponseCookie responseCookie = ResponseCookie.from(
//                "token", token)
//                .sameSite("None")
//                .path("/")
//                .build();
//        response.addHeader("wiola", responseCookie.toString());
        response.addHeader(jwtConfiguration.getAuthorizationHeader(), jwtConfiguration.getTokenPrefix() + token);
    }

}
