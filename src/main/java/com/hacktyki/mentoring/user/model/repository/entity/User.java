package com.hacktyki.mentoring.user.model.repository.entity;

import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.AuthorityType;
import com.hacktyki.mentoring.user.model.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity(name = "user_account")
@Data
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @Column
    private UserStatus status;

    @Column(nullable = false)
    private AuthorityType role;

    @Column
    private Long mentorId;

    @Column
    private ZonedDateTime deactivationTime;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Meeting> meetings;

}
