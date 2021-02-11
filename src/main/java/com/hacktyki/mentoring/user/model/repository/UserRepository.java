package com.hacktyki.mentoring.user.model.repository;

import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.AuthorityType;
import com.hacktyki.mentoring.user.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByUsername(String username);

    List<User> getAllByStatus(UserStatus status);

    List<User> getAllByMentorId(Long id);

    User getUserById(Long id);

    List<User> getAllByRoleAndMentorIdAndStatus(AuthorityType role, Long id, UserStatus status);

    List<User> getByStatusAndDeactivationTimeBefore(UserStatus status, ZonedDateTime time);
}
