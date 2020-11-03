package com.hacktyki.mentoring.configuration;

import com.hacktyki.mentoring.meeting.repository.MeetingRepository;
import com.hacktyki.mentoring.user.model.repository.UserRepository;
import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.AuthorityType;
import com.hacktyki.mentoring.user.model.UserStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class DBInit implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MeetingRepository meetingRepository;

    public DBInit(UserRepository userRepository, PasswordEncoder passwordEncoder, MeetingRepository meetingRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.meetingRepository = meetingRepository;
    }


    @Override
    public void run(String... args) {

        User mentor = new User();
        mentor.setRole(AuthorityType.MENTOR);
        mentor.setStatus(UserStatus.ACTIVE);
        mentor.setUsername("mentor");
        mentor.setEmail("mentor@email.com");
        mentor.setName("Adam");
        mentor.setLastName("Nowak");
        mentor.setPassword(passwordEncoder.encode("mentor"));
        userRepository.save(mentor);

        User student = new User();
        student.setRole(AuthorityType.STUDENT);
        student.setStatus(UserStatus.ACTIVE);
        student.setUsername("student");
        student.setEmail("student@email.com");
        student.setName("Anna");
        student.setLastName("Mak");
        student.setMentorId(mentor.getId());
        student.setPassword(passwordEncoder.encode("student"));
        userRepository.save(student);

        User admin = new User();
        admin.setRole(AuthorityType.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setUsername("admin");
        admin.setEmail("admin@email.com");
        admin.setName("Jan");
        admin.setLastName("Kowalski");
        admin.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(admin);

        Meeting meeting = new Meeting();
        meeting.setStudent(student);
        meeting.setMentor(mentor);
        meeting.setTime(ZonedDateTime.parse("2020-10-26T13:00:00+01:00"));
        meetingRepository.save(meeting);
    }
}
