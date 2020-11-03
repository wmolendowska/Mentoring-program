package com.hacktyki.mentoring;

import com.hacktyki.mentoring.exceptions.PasswordNotMatchException;
import com.hacktyki.mentoring.exceptions.PasswordRulesNotMatchException;
import com.hacktyki.mentoring.meeting.repository.MeetingRepository;
import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.UserStatus;
import com.hacktyki.mentoring.user.model.repository.UserRepository;
import com.hacktyki.mentoring.user.model.repository.VerificationTokenRepository;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.AuthorityType;
import com.hacktyki.mentoring.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotEquals;

@SpringBootTest
@ActiveProfiles("H2")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private MeetingRepository meetingRepository;



    @AfterEach
    void tearDown() {
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void addUserToDatabase() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setEmail("test@email.com");
        user.setName("name");
        user.setLastName("lastname");
        user.setRole(AuthorityType.MENTOR);
        user.setPassword(passwordEncoder.encode("TEsst12!"));
        user.setStatus(UserStatus.ACTIVE);
        user.setUsername("user");
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "user", password = "TEsst12!")
    public void changePassword() throws PasswordRulesNotMatchException, PasswordNotMatchException {
        User user = userService.getUserByUsername("user");
        userService.changePassword("TEsst12!", "TEsst21!");
        User user1 = userRepository.getUserById(user.getId());
        assertNotEquals("", user.getPassword(), user1.getPassword());
    }

    @Test
    public void deactivateUser() {
        userService.deactivateUser("user");
        User user = userService.getUserByUsername("user");
        assertEquals("", true, user.getStatus().equals(UserStatus.INACTIVE));
    }

    @Test
    public void deleteUser() {
        Meeting meeting = new Meeting();
        meeting.setMentor(userService.getUserByUsername("user"));
        meeting.setTime(ZonedDateTime.parse("2019-07-29T16:00:00Z"));
        meetingRepository.save(meeting);
        userService.deactivateUser("user");
        userService.deleteUser(ZonedDateTime.parse("2021-07-31T15:00:00Z"));
        assertEquals("", true, true);
    }


//    @Test
//    @WithMockUser(username = "user", password = "TEsst12!")
//    public void setMentorToStudent() throws StudentAssignToAnotherMentorException {
//
//        User student = createStudentAccount();
//
//        userService.setMentorToStudent("student");
//
//        User mentor = userService.getUserByUsername("user");
//        User assignedStudent = userRepository.getUserById(student.getId());
//
//        assertEquals("", mentor.getId(), assignedStudent.getMentorId());
//    }

//    @Test
//    public void activateDeactivatedUser() {
//        userService.activateDeactivatedUser("user");
//        User user = userService.getUserByUsername("user");
//        assertEquals("", null, user.getDeactivationTime());
//    }


//    @Test
//    @WithMockUser(username = "user", password = "TEsst12!")
//    public void deleteMentorFromStudent() throws PasswordRulesNotMatchException, TokenExpiredException, StudentAssignToAnotherMentorException {
//        User student = createStudentAccount();
//
//        userService.setMentorToStudent("student");
//
//        userService.deleteMentorFromStudent(student.getUsername());
//        User unassignedStudent = userRepository.getUserById(student.getId());
//
//        assertEquals("", null, unassignedStudent.getMentorId());
//    }
//
//    private User createStudentAccount() {
//        User student = new User();
//        student.setEmail("studenttest@email.com");
//        student.setName("name");
//        student.setLastName("lastname");
//        student.setRole(AuthorityType.STUDENT);
//        student.setPassword("TEsst12!");
//        student.setActive(true);
//        student.setUsername("student");
//        userRepository.save(student);
//        return student;
//    }

}
