package com.hacktyki.mentoring.meeting.model;

import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.SimpleUser;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MeetingInfoForAdmin {
        private Long id;
        private ZonedDateTime time;
        private SimpleUser student;
        private SimpleUser mentor;

        public MeetingInfoForAdmin(Meeting meeting) {
            this.id = meeting.getId();
            this.time = meeting.getTime();
            if(meeting.getStudent() != null){
                this.student = new SimpleUser(meeting.getStudent());
            }
            this.mentor = new SimpleUser(meeting.getMentor());

        }
}
