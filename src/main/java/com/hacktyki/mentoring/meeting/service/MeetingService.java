package com.hacktyki.mentoring.meeting.service;

import com.hacktyki.mentoring.exceptions.IllegalDateException;
import com.hacktyki.mentoring.exceptions.MeetingAlreadyBookedException;
import com.hacktyki.mentoring.exceptions.MeetingNotFoundException;
import com.hacktyki.mentoring.meeting.model.MeetingInfoForAdmin;
import com.hacktyki.mentoring.meeting.model.MeetingInfoForMentor;
import com.hacktyki.mentoring.meeting.model.MeetingInfoForStudent;
import com.hacktyki.mentoring.meeting.repository.MeetingRepository;
import com.hacktyki.mentoring.user.model.repository.UserRepository;
import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.AuthorityType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public MeetingService(MeetingRepository meetingRepository, UserRepository userRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Meeting getMeetingById(Long id) {
        Optional<Meeting> meeting = meetingRepository.getMeetingById(id);
        return meeting.orElseThrow(() -> (new MeetingNotFoundException("Meeting not found")));
    }

    public List<MeetingInfoForMentor> getMeetingsForMentorAndDate(User mentor, ZonedDateTime time) {
        System.out.println(time.getOffset().toString().substring(0, 3));
        List<Meeting> meetings = meetingRepository.findMeetingsByMentorAndTimeBetween(
                mentor, time, time.plusHours(23).plusMinutes(59));
        System.out.println(meetings);
        return meetings.stream()
                .map(MeetingInfoForMentor::new)
                .sorted((Comparator.comparing(MeetingInfoForMentor::getTime)))
                .collect(Collectors.toList());
    }

    public List<MeetingInfoForStudent> getMeetingsTimeForStudentAndData(User student, ZonedDateTime time) {
        List<Meeting> meetings = meetingRepository.findMeetingsByStudentAndTimeBetween
                (student, time.withHour(0).withMinute(0), time.withHour(23).withMinute(59));
        return meetings.stream()
                .map(MeetingInfoForStudent::new)
                .sorted(Comparator.comparing(MeetingInfoForStudent::getTime))
                .collect(Collectors.toList());
    }

    public List<MeetingInfoForStudent> getMeetingsInfoForStudentsMentorAndDate(User student, ZonedDateTime time) {
        User mentor = userRepository.getUserById(student.getMentorId());
        List<Meeting> meetings = meetingRepository.findMeetingsByMentorAndStudentAndTimeBetween(
                mentor, null, time.withHour(0).withMinute(0), time.withHour(23).withMinute(59));
        return meetings.stream()
                .map(MeetingInfoForStudent::new)
                .collect(Collectors.toList());
    }

    public void addMeetingsTime(ZonedDateTime from, ZonedDateTime to, User mentor) throws IllegalDateException {
        if (from.isBefore(ZonedDateTime.now()) || (from.getDayOfYear() != to.getDayOfYear())) {
            throw new IllegalDateException("Illegal date");
        }
        while (from.isBefore(to) && from.plusMinutes(15).isBefore(to.plusSeconds(1))) {
            Meeting meeting = new Meeting();
            meeting.setMentor(mentor);
            meeting.setTime(from);
            from = from.plusMinutes(15);
            meetingRepository.save(meeting);
        }
    }

    public void bookMeeting(Long id, User student) throws MeetingAlreadyBookedException {
        Meeting meeting = getMeetingById(id);
        if (meeting.getStudent() != null) {
            throw new MeetingAlreadyBookedException("Meeting already booked");
        }
        meeting.setStudent(student);
        meetingRepository.save(meeting);
        User mentor = userRepository.getUserById(student.getMentorId());
        applicationEventPublisher.publishEvent(new OnMeetingBookedOrCancelledEvent(meeting, mentor, student, false));
    }


    public void cancelMeeting(Long id, User user) {
        Meeting meeting = getMeetingById(id);
        if (user.getRole() == AuthorityType.MENTOR) {
            if(meeting.getStudent() != null) {
                applicationEventPublisher.publishEvent(new OnMeetingBookedOrCancelledEvent(meeting, user, meeting.getStudent(), true));
                meeting.setStudent(null);
            }
            meeting.setMentor(null);
            meetingRepository.delete(meeting);
        } else {
            User mentor = userRepository.getUserById(user.getMentorId());
            applicationEventPublisher.publishEvent(new OnMeetingBookedOrCancelledEvent(meeting, mentor, user, true));
            meeting.setStudent(null);
            meetingRepository.save(meeting);
        }
    }

    public List<MeetingInfoForAdmin> getAllMeetingsForDate(ZonedDateTime time) {
        System.out.println(time);
        return meetingRepository.findMeetingsByTimeBetween(time.withHour(0).withMinute(0), time.withHour(23).withMinute(59))
                .stream()
                .map(MeetingInfoForAdmin::new)
                .sorted((Comparator.comparing(MeetingInfoForAdmin::getTime)))
                .collect(Collectors.toList());
    }

    public void deleteMeeting(Long id) {
        Meeting meeting = getMeetingById(id);
        meetingRepository.delete(meeting);
    }

}
