package com.hacktyki.mentoring;

import com.hacktyki.mentoring.exceptions.MeetingAlreadyBookedException;
import com.hacktyki.mentoring.exceptions.StudentAssignToAnotherMentorException;
import com.hacktyki.mentoring.meeting.repository.MeetingRepository;
import com.hacktyki.mentoring.user.model.repository.UserRepository;
import com.hacktyki.mentoring.user.model.repository.VerificationTokenRepository;
import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.AuthorityType;
import com.hacktyki.mentoring.meeting.service.MeetingService;
import com.hacktyki.mentoring.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@ActiveProfiles("h2")
public class MeetingServiceTest {

    @Autowired
    MeetingService meetingService;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @AfterEach
    void tearDown() {
        List<Meeting> meetings = meetingRepository.findAll();
        for (Meeting meeting : meetings) {
            meeting.setMentor(null);
            meeting.setStudent(null);
            meetingRepository.delete(meeting);
        }
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void addMeetingsTime() {
        assertEquals("", 0L, meetingRepository.count());
        User mentor = createMentorAccount();
        ZonedDateTime from = ZonedDateTime.parse("2020-09-01T15:00:00Z");
        ZonedDateTime to = ZonedDateTime.parse("2020-09-01T15:30:00Z");
        meetingService.addMeetingsTime(from, to, mentor);
        assertEquals("", 2L, meetingRepository.count());
    }

    @Test
    public void bookMeeting_meetingAvailable_meetingBooked() throws MeetingAlreadyBookedException {
        User student = createStudentAccount();
        User mentor = createMentorAccount();

        student.setMentorId(mentor.getId());
        userRepository.save(student);

        Meeting meeting = new Meeting();
        meeting.setMentor(mentor);
        meeting.setTime(ZonedDateTime.parse("2020-07-29T15:00:00Z"));
        meetingRepository.save(meeting);

        Meeting savedMeeting = meetingService.getMeetingById(meeting.getId());

        meetingService.bookMeeting(savedMeeting.getId(), userRepository.getUserById(student.getId()));

        User meetingStudent = userRepository.getUserById(student.getId());
        User meetingMentor = userRepository.getUserById(student.getMentorId());
        Meeting bookedMeeting = meetingService.getMeetingById(meeting.getId());

        assertEquals("", bookedMeeting.getMentor().getId(), meetingMentor.getId());
        assertEquals("", bookedMeeting.getStudent().getId(), meetingStudent.getId());
    }


    @Test
    public void cancelMeeting_meetingExist_meetingCancelled() throws StudentAssignToAnotherMentorException, MeetingAlreadyBookedException {
        User mentor = createMentorAccount();
        User student = createStudentAccount();
        student.setMentorId(mentor.getId());
        userRepository.save(student);

        Meeting meeting = new Meeting();
        meeting.setMentor(mentor);
        meeting.setStudent(student);
        meeting.setTime(ZonedDateTime.parse("2020-07-29T15:00:00Z"));
        meetingRepository.save(meeting);

        Meeting savedMeeting = meetingService.getMeetingById(meeting.getId());

        meetingService.cancelMeeting(savedMeeting.getId(), student);
        assertEquals("", 0L, meetingRepository.count());
    }

    private User createMentorAccount() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User mentor = new User();
        mentor.setEmail("test@email.com");
        mentor.setName("name");
        mentor.setLastName("lastname");
        mentor.setRole(AuthorityType.MENTOR);
        mentor.setPassword(passwordEncoder.encode("TEsst12!"));
        mentor.setActive(true);
        mentor.setUsername("mentor1");
        userRepository.save(mentor);
        return mentor;
    }

    private User createStudentAccount() {
        User student = new User();
        student.setEmail("studenttest@email.com");
        student.setName("name");
        student.setLastName("lastname");
        student.setRole(AuthorityType.STUDENT);
        student.setPassword("TEsst12!");
        student.setActive(true);
        student.setUsername("student");
        userRepository.save(student);
        return student;
    }

}
