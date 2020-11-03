package com.hacktyki.mentoring.user.model.repository;

import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.repository.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken getByToken(String token);

    VerificationToken getByUser(User user);

    List<VerificationToken> getByExpiryDateIsBefore(ZonedDateTime expiryDate);
}
