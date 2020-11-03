package com.hacktyki.mentoring.meeting.model;

import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MeetingInfoForStudent {
    private Long id;
    private ZonedDateTime time;
    private boolean isBooked;

    public MeetingInfoForStudent(Meeting meeting) {
        this.id = meeting.getId();
        this.time = meeting.getTime();
        this.isBooked = meeting.getStudent() != null;
    }
}
