package com.hacktyki.mentoring.meeting.controller;

import com.hacktyki.mentoring.exceptions.IllegalDateException;
import com.hacktyki.mentoring.meeting.model.MeetingInfoForAdmin;
import com.hacktyki.mentoring.meeting.model.MeetingInfoForMentor;
import com.hacktyki.mentoring.meeting.model.MeetingInfoForStudent;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.meeting.service.MeetingService;
import com.hacktyki.mentoring.user.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
public class MeetingController {
    private final MeetingService meetingService;
    private final UserService userService;


    public MeetingController(MeetingService meetingService, UserService userService) {
        this.meetingService = meetingService;
        this.userService = userService;
    }


    @GetMapping("admin/meetingsForDate/{time}")
    public ResponseEntity<List<MeetingInfoForAdmin>> geAllMeetingsForDate(
            @PathVariable("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime time) {
        List<MeetingInfoForAdmin> meetings = meetingService.getAllMeetingsForDate(time);
        return ResponseEntity.ok(meetings);
    }

    @DeleteMapping("admin/deleteMeeting")
    public ResponseEntity<String> deleteMeeting(@RequestParam Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.ok("Meeting deleted");
    }

    @GetMapping("mentor/meetingsForDate/{time}")
    public ResponseEntity<List<MeetingInfoForMentor>> getMeetingsForLoggedMentorAndDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime time) {
        User mentor = userService.authenticateUser();
        List<MeetingInfoForMentor> simpleMeetings = meetingService.getMeetingsForMentorAndDate(mentor, time);
        return ResponseEntity.ok(simpleMeetings);
    }


    @PostMapping("mentor/addMeetings/{from}/{to}")
    public void addMeetingsTime(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to) throws IllegalDateException {
        User user = userService.authenticateUser();
        meetingService.addMeetingsTime(from, to, user);
    }

    @GetMapping("student/meetingsForDate/{time}")
    public ResponseEntity<List<MeetingInfoForStudent>> getMeetingsForLoggedStudentAndDate(
            @PathVariable("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime time
    ) {
        User student = userService.authenticateUser();
        List<MeetingInfoForStudent> meetings = meetingService.getMeetingsTimeForStudentAndData(student, time);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("student/mentorCalendarForDate/{time}")
    public ResponseEntity<List<MeetingInfoForStudent>> getMeetingsForUsersMentorAndDate(
            @PathVariable("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime time
    ) {
        User student = userService.authenticateUser();
        List<MeetingInfoForStudent> meetingInfo = meetingService.getMeetingsInfoForStudentsMentorAndDate(student, time);
        return ResponseEntity.ok(meetingInfo);
    }

    @PostMapping("student/bookMeeting")
    public void bookMeeting(
            @RequestParam Long id) {
        User user = userService.authenticateUser();
        meetingService.bookMeeting(id, user);
    }

    @PostMapping("cancelMeeting")
    public void cancelMeeting(
            @RequestParam Long id) {
        User user = userService.authenticateUser();
        meetingService.cancelMeeting(id, user);
    }

}
