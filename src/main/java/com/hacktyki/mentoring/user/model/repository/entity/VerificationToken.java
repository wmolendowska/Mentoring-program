package com.hacktyki.mentoring.user.model.repository.entity;

import com.hacktyki.mentoring.user.model.repository.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
public class VerificationToken {
    private static final int EXPIRATION = 72;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "userId")
    private User user;

    private ZonedDateTime expiryDate;

    private ZonedDateTime calculateExpiryDate() {
        return expiryDate = ZonedDateTime.now().plusHours(EXPIRATION);
    }

    public VerificationToken() {
        this.expiryDate = calculateExpiryDate();
    }
}
