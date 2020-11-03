package com.hacktyki.mentoring.meeting.repository.entity;

import com.hacktyki.mentoring.user.model.repository.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private ZonedDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idStudent")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMentor", nullable = false)
    private User mentor;

}
