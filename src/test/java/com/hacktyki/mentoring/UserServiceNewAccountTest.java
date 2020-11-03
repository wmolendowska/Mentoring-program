package com.hacktyki.mentoring;

import com.hacktyki.mentoring.exceptions.PasswordRulesNotMatchException;
import com.hacktyki.mentoring.exceptions.TokenExpiredException;
import com.hacktyki.mentoring.user.model.repository.UserRepository;
import com.hacktyki.mentoring.user.model.repository.VerificationTokenRepository;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.repository.entity.VerificationToken;
import com.hacktyki.mentoring.user.model.AuthorityType;
import com.hacktyki.mentoring.user.model.UserAccount;
import com.hacktyki.mentoring.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@ActiveProfiles("h2")
public class UserServiceNewAccountTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @AfterEach
    void tearDown() {
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void addUser() {
        final UserAccount user = new UserAccount("test@email.com", "name", "lastName", AuthorityType.STUDENT);
        assertEquals("", 0L, userRepository.count());
        userService.createEntity(user);
        assertEquals("",1L, userRepository.count());
    }

        @Test
        public void generateUsername(){
            final UserAccount userAccount = new UserAccount("test@email.com", "name", "lastName", AuthorityType.STUDENT);
            User user = userService.createEntity(userAccount);
            String username = user.getUsername();
            String substring = username.substring(6);
         StringBuilder number = new StringBuilder();
            for (char c : substring.toCharArray()) {
                if (c >= 48 && c <= 57) {
                    number.append(c);
                }
            }
         assertEquals("Username test failed", "namlas" + number, username);
        }

    @Test
    public void createVerificationToken() {
        final UserAccount userAccount = new UserAccount("test@email.com", "name", "lastName", AuthorityType.STUDENT);
        User user = userService.createEntity(userAccount);
        assertEquals("", 0L, verificationTokenRepository.count());
        userService.createVerificationToken(user, "123456789");
        assertEquals("", 1L, verificationTokenRepository.count());
    }

    @Test
    public void activateUser() throws PasswordRulesNotMatchException, TokenExpiredException {
        final UserAccount userAccount = new UserAccount("test@email.com", "name", "lastName", AuthorityType.STUDENT);
        User user = userService.createEntity(userAccount);
        VerificationToken verificationToken = userService.createVerificationToken(user, "123456789");
        userService.activateUser(verificationToken.getToken());
        User verifiedUser = userRepository.getUserById(user.getId());
        assertEquals("", true, verifiedUser.isActive());
    }
}
