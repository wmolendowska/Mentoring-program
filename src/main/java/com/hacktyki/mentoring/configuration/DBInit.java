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

        User mentor2 = new User();
        mentor2.setRole(AuthorityType.MENTOR);
        mentor2.setStatus(UserStatus.ACTIVE);
        mentor2.setUsername("mentor2");
        mentor2.setEmail("mentor2@email.com");
        mentor2.setName("Elwira");
        mentor2.setLastName("Lis");
        mentor2.setPassword(passwordEncoder.encode("mentor2"));
        userRepository.save(mentor2);

        User student = new User();
        student.setRole(AuthorityType.STUDENT);
        student.setStatus(UserStatus.ACTIVE);
        student.setUsername("student");
        student.setEmail("wiomol97@gmail.com");
        student.setName("Anna");
        student.setLastName("Mak");
        student.setMentorId(mentor.getId());
        student.setPassword(passwordEncoder.encode("student"));
        userRepository.save(student);

        User student2 = new User();
        student2.setRole(AuthorityType.STUDENT);
        student2.setStatus(UserStatus.ACTIVE);
        student2.setUsername("student2");
        student2.setEmail("student2@email.com");
        student2.setName("Krzysztof");
        student2.setLastName("Bratek");
        student2.setMentorId(mentor.getId());
        student2.setPassword(passwordEncoder.encode("student2"));
        userRepository.save(student2);

        User student3 = new User();
        student3.setRole(AuthorityType.STUDENT);
        student3.setStatus(UserStatus.ACTIVE);
        student3.setUsername("student3");
        student3.setEmail("student3@email.com");
        student3.setName("Jan");
        student3.setLastName("Kowalski");
        student3.setMentorId(mentor2.getId());
        student3.setPassword(passwordEncoder.encode("student3"));
        userRepository.save(student3);


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
