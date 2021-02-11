package com.hacktyki.mentoring.meeting.repository;

import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findMeetingsByStudentAndTimeAfter(User student, ZonedDateTime time);

    List<Meeting> findMeetingsByStudentAndTimeBefore(User student, ZonedDateTime time);

    List<Meeting> findMeetingsByStudentAndTimeBetween(User student, ZonedDateTime from, ZonedDateTime to);

    List<Meeting> findMeetingsByMentorAndTimeAfter(User mentor, ZonedDateTime time);

    List<Meeting> findMeetingsByMentorAndTimeBefore(User mentor, ZonedDateTime time);

    List<Meeting> findMeetingsByMentorAndStudentAndTimeBetween(User mentor, User student, ZonedDateTime from, ZonedDateTime to);

    List<Meeting> findMeetingsByMentorAndTimeBetween(User mentor, ZonedDateTime from, ZonedDateTime to);

    List<Meeting> findMeetingsByMentorAndTime(User mentor, LocalDate date);

    List<Meeting> findMeetingsByTimeAfter(ZonedDateTime time);

    List<Meeting> findMeetingsByTimeBefore(ZonedDateTime time);

    List<Meeting> findMeetingsByTimeBetween(ZonedDateTime from, ZonedDateTime to);

    Optional<Meeting> getMeetingById(Long id);
}
