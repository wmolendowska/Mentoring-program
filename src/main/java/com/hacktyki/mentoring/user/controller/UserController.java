package com.hacktyki.mentoring.user.controller;

import com.hacktyki.mentoring.exceptions.PasswordNotMatchException;
import com.hacktyki.mentoring.exceptions.PasswordRulesNotMatchException;
import com.hacktyki.mentoring.exceptions.StudentAssignToAnotherMentorException;
import com.hacktyki.mentoring.exceptions.TokenExpiredException;
import com.hacktyki.mentoring.user.model.repository.entity.User;
import com.hacktyki.mentoring.user.service.OnRegistrationCompleteEvent;
import com.hacktyki.mentoring.user.service.UserService;
import com.hacktyki.mentoring.user.model.SimpleDeactivatedUser;
import com.hacktyki.mentoring.user.model.SimplePendingConfirmationUser;
import com.hacktyki.mentoring.user.model.SimpleUser;
import com.hacktyki.mentoring.user.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
public class UserController {
    private final UserService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserController(UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

   @RequestMapping(method = RequestMethod.OPTIONS, value = "/login")
   public void login(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Credentials", "true");
//       if(CorsUtils.isPreFlightRequest(request)) {
//           response.setStatus(HttpServletResponse.SC_OK);
//       }
//        return ResponseEntity.ok("ok");
   }

   @GetMapping("userAccountDetails")
   public ResponseEntity<SimpleUser> getUserAccountDetails() {
        return ResponseEntity.ok(userService.getUserAccountDetails());
   }

    @GetMapping("admin/users")
    public ResponseEntity<List<SimpleUser>> getUsersList() {
        List<SimpleUser> simpleUsers = userService.getSimpleUsers();
        return ResponseEntity.ok(simpleUsers);
    }

    @PostMapping("newUser")
    public void addNewUser(@RequestBody UserAccount userAccount) {
        User user = userService.createEntity(userAccount);
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent("localhost:4200", user));
    }

    @PutMapping("admin/editUser/{id}")
    public void editUser(@RequestBody UserAccount userAccount, @PathVariable Long id) {
        userService.editUser(id, userAccount);
    }

    @GetMapping("admin/deactivatedUsers")
    public ResponseEntity<List<SimpleDeactivatedUser>> getNewInactiveUsersList() {
        List<SimpleDeactivatedUser> simpleInactiveUsers = userService.getAllDeactivatedSimpleUsers();
        return ResponseEntity.ok(simpleInactiveUsers);
    }

    @GetMapping("admin/pendingUsers")
    public ResponseEntity<List<SimplePendingConfirmationUser>> getDeactivatedUsersList() {
        List<SimplePendingConfirmationUser> simpleUsers = userService.getAllPendingConfirmationSimpleUsers();
        return ResponseEntity.ok(simpleUsers);
    }

    @PostMapping("admin/deactivateUser")
    public void deactivateUser(@RequestParam Long id) {
        userService.deactivateUser(id);
    }

    @PostMapping("admin/activateUser")
    public void activateDeactivatedUser(@RequestParam Long id,
                                                          HttpServletRequest request) {
        String appUrl = request.getContextPath();
        User user = userService.activateDeactivatedUser(id);
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(appUrl, user));
    }

    @GetMapping("admin/studentsMentor/{id}")
    public ResponseEntity<SimpleUser> getStudentsMentor(@PathVariable Long id) {
        SimpleUser mentor = userService.getStudentsMentor(id);
        return ResponseEntity.ok(mentor);
    }

    @GetMapping({"students/{mentorId}", "students"})
    public ResponseEntity<List<SimpleUser>> getMentorsStudentList(@PathVariable(required = false) Long mentorId) {
        List<SimpleUser> simpleUsers;
        if (mentorId != null) {
            simpleUsers = userService.getMentorsStudentList(mentorId);
        } else {
            User mentor = userService.authenticateUser();
            simpleUsers = userService.getMentorsStudentList(mentor.getId());
        }
        return ResponseEntity.ok(simpleUsers);
    }

    @GetMapping("unassignedStudents")
    public ResponseEntity<List<SimpleUser>> getUnassignedStudents() {
        List<SimpleUser> unassignedStudents = userService.getUnassignedStudents();
        return ResponseEntity.ok(unassignedStudents);
    }

    @PostMapping("mentor/assignStudent")
    public void setMentorToStudent(@RequestParam Long id) throws StudentAssignToAnotherMentorException {
        userService.setMentorToStudent(id);
    }

    @PostMapping("releaseStudent")
    public void setNewMentorToStudent(@RequestParam Long id) throws StudentAssignToAnotherMentorException {
        userService.deleteMentorFromStudent(id);
    }

    @PostMapping("/newUser/registrationConfirm/{token}")
    public void confirmRegistration(@PathVariable String token, @RequestParam String password) throws PasswordRulesNotMatchException, TokenExpiredException {
        userService.activateUser(token, password);
    }

    @PostMapping("changePassword")
    public void changePassword(@RequestParam String oldPassword, @RequestParam String newPassword)
            throws PasswordNotMatchException, PasswordRulesNotMatchException {
        userService.changePassword(oldPassword, newPassword);
    }

}
