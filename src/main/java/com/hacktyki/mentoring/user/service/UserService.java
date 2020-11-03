package com.hacktyki.mentoring.user.service;

import com.hacktyki.mentoring.exceptions.PasswordNotMatchException;
import com.hacktyki.mentoring.exceptions.PasswordRulesNotMatchException;
import com.hacktyki.mentoring.exceptions.StudentAssignToAnotherMentorException;
import com.hacktyki.mentoring.exceptions.TokenExpiredException;
import com.hacktyki.mentoring.meeting.repository.MeetingRepository;
import com.hacktyki.mentoring.user.model.repository.UserRepository;
import com.hacktyki.mentoring.user.model.repository.VerificationTokenRepository;
import com.hacktyki.mentoring.meeting.repository.entity.Meeting;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.model.repository.entity.VerificationToken;
import com.hacktyki.mentoring.user.model.*;
import org.passay.CharacterData;
import org.passay.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MeetingRepository meetingRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, MeetingRepository meetingRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.meetingRepository = meetingRepository;
    }

    public User createEntity(UserAccount userAccount) {
        User user = new User();
        user.setUsername(generateUsername(userAccount));
        user.setEmail(userAccount.getEmail());
        user.setName(userAccount.getName());
        user.setLastName(userAccount.getLastName());
        user.setRole(userAccount.getRole());
        user.setStatus(UserStatus.PENDING);
        user.setDeactivationTime(ZonedDateTime.now());
        userRepository.save(user);
        return user;
    }

    public VerificationToken createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    public void activateUser(String token, String password) throws TokenExpiredException, PasswordRulesNotMatchException {
        VerificationToken verificationToken = getVerificationToken(token);
        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        user.setDeactivationTime(null);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    private VerificationToken getVerificationToken(String token) throws TokenExpiredException {
        VerificationToken verificationToken = verificationTokenRepository.getByToken(token);
        if (verificationToken.getExpiryDate().isBefore(ZonedDateTime.now().minusHours(72))) {
            throw new TokenExpiredException("Token is expired.");
        }
        return verificationToken;
    }

    private void validatePassword(String password) throws PasswordRulesNotMatchException {
        List<CharacterRule> rules = getPasswordRules();
        PasswordValidator passwordValidator = new PasswordValidator(rules);
        RuleResult result = passwordValidator.validate(new PasswordData(password));
        if (!result.isValid()) {
            String message = "";
            for (String msg : passwordValidator.getMessages(result)) {
                message = message + msg + "\n";
            }
            throw new PasswordRulesNotMatchException(message);
        }
    }

    public void changePassword(String oldPassword, String newPassword) throws PasswordNotMatchException, PasswordRulesNotMatchException {
        User user = authenticateUser();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            validatePassword(newPassword);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new PasswordNotMatchException("Wrong password.");
        }
    }

    private List<CharacterRule> getPasswordRules() {
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(3);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(1);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            @Override
            public String getErrorCode() {
                return "Password must contain 1 or more special characters.";
            }

            @Override
            public String getCharacters() {
                return "!@#$%^&*_";
            }
        };
        CharacterRule specialCharsRule = new CharacterRule(specialChars);
        specialCharsRule.setNumberOfCharacters(1);

        return Arrays.asList(lowerCaseRule, upperCaseRule, digitRule, specialCharsRule);
    }

    private String generateUsername(UserAccount userAccount) {
        String username = userAccount.getName().toLowerCase().substring(0, 3)
                + userAccount.getLastName().toLowerCase().substring(0, 3)
                + new Random().ints(3, 48, 57)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return username;
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.getUserByUsername(username);
        return user.orElseThrow(() -> (new UsernameNotFoundException(username + " not found")));
    }


    public List<SimpleUser> getSimpleUsers() {
        List<User> users =  userRepository.getAllByStatus(UserStatus.ACTIVE);
        return users.stream()
                .map(SimpleUser::new)
                .sorted((Comparator.comparing(SimpleUser::getLastName)))
                .collect(Collectors.toList());
    }

    public List<SimpleDeactivatedUser> getAllDeactivatedSimpleUsers() {
        final List<User> users = userRepository.getAllByStatus(UserStatus.INACTIVE);
        return users.stream()
                .map(SimpleDeactivatedUser::new)
                .sorted((Comparator.comparing(SimpleDeactivatedUser::getLastName)))
                .collect(Collectors.toList());
    }

    public List<SimplePendingConfirmationUser> getAllPendingConfirmationSimpleUsers() {
       List<User> users = userRepository.getAllByStatus(UserStatus.PENDING);
        for (User user : users) {
            VerificationToken token = verificationTokenRepository.getByUser(user);
            user.setDeactivationTime(token.getExpiryDate().minusHours(72));
        }
        return users.stream()
                .map(SimplePendingConfirmationUser::new)
                .sorted((Comparator.comparing(SimplePendingConfirmationUser::getLastName)))
                .collect(Collectors.toList());
    }

    public User authenticateUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return getUserByUsername(username);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.getUserById(id);
        user.setStatus(UserStatus.INACTIVE);
        user.setDeactivationTime(ZonedDateTime.now());
        userRepository.save(user);
        deleteUsersMeetings(user);
        deleteUsersVerificationToken(user);
    }

    private void deleteUsersMeetings(User user) {
        if (user.getRole() == AuthorityType.MENTOR) {
            for (Meeting meeting : meetingRepository.findMeetingsByMentorAndTimeAfter(user, ZonedDateTime.now())) {
                meetingRepository.delete(meeting);
            }
        } else if (user.getRole() == AuthorityType.STUDENT) {
            for (Meeting meeting : meetingRepository.findMeetingsByStudentAndTimeAfter(user, ZonedDateTime.now())) {
                meeting.setStudent(null);
                meetingRepository.save(meeting);
            }
        }
    }

    private void deleteUsersVerificationToken(User user) {
        VerificationToken verificationToken = verificationTokenRepository.getByUser(user);
        if (verificationToken != null) {
            verificationTokenRepository.delete(verificationToken);
        }
    }

    public void deleteUser(ZonedDateTime time) {
        List<User> users = userRepository.getByStatusAndDeactivationTimeBefore(UserStatus.ACTIVE, time);
        for (User user : users) {
            userRepository.delete(user);
        }
    }

    public void deleteVerificationToken(ZonedDateTime time) {
        List<VerificationToken> verificationTokens = verificationTokenRepository.getByExpiryDateIsBefore(time);
        for (VerificationToken verificationToken : verificationTokens) {
            verificationTokenRepository.delete(verificationToken);
        }
    }

    public void setMentorToStudent(Long studentId) throws StudentAssignToAnotherMentorException {
        User mentor = authenticateUser();
        User student = userRepository.getUserById(studentId);
        if (student.getRole() == AuthorityType.STUDENT && mentor.getRole() == AuthorityType.MENTOR) {
            if (student.getMentorId() == null) {
                student.setMentorId(mentor.getId());
                userRepository.save(student);
            } else throw new StudentAssignToAnotherMentorException("Student already assigned");
        } else throw new IllegalArgumentException("Wrong role.");
    }

    public User activateDeactivatedUser(Long id) {
        User user = userRepository.getUserById(id);
        user.setDeactivationTime(null);
        user.setStatus(UserStatus.PENDING);
        userRepository.save(user);
        return user;
    }

    public void deleteMentorFromStudent(Long id) {
        User student = userRepository.getUserById(id);
            student.setMentorId(null);
            userRepository.save(student);
            deleteUsersMeetings(student);
    }

    public List<SimpleUser> getMentorsStudentList(Long id) {
        return userRepository.getAllByMentorId(id).stream()
                .map(SimpleUser::new)
                .collect(Collectors.toList());
    }


    public void editUser(Long id, UserAccount userAccount) {
        User user = userRepository.getUserById(id);
        user.setEmail(userAccount.getEmail());
        user.setName(userAccount.getName());
        user.setLastName(userAccount.getLastName());
        user.setRole(userAccount.getRole());
        userRepository.save(user);
    }

    public String getUserRole() {
        User user = authenticateUser();
        AuthorityType role = user.getRole();
        return role.toString();
    }


    public List<SimpleUser> getUnassignedStudents() {
        return userRepository.getAllByRoleAndMentorId(AuthorityType.STUDENT, null)
                .stream()
                .map(SimpleUser::new)
                .sorted(Comparator.comparing(SimpleUser::getLastName))
                .collect(Collectors.toList());
    }

    public SimpleUser getStudentsMentor(Long id) {
        User student = userRepository.getUserById(id);
        User mentor = userRepository.getUserById(student.getMentorId());
        return new SimpleUser(mentor);
    }

    public SimpleUser getUserAccountDetails() {
        User user = authenticateUser();
        return new SimpleUser(user);
    }
}
