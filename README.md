# Mentoring

Mentoring is a Java web application that helps to make an appointment between mentor and student.

### Requirements
For building and running the application you need JDK 11

### How to run it?

Clone the repository from gitlab

```
https://gitlab.com/hacktyki-2020/wiomol-java-mentoring
```

Navigate to the cloned repository directory:

```
cd wiomol-java-mentoring/mentoring
```

The project uses Gradle to build, so you have to have Gradle installed.

If you are under linux/mac:

```
./gradlew bootrun
```

And for windows:

```
gradlew.bat bootrun
```

After the server is running, go to

```
http://localhost:8080/login

ADMIN
user: admin
password: admin 

MENTOR
user: mentor
password: mentor

STUDENT
user: student
password: student  
```

## Paths
* Paths starting with "/admin" require admin role.
* Paths starting with "/mentor" require mentor role.
* Paths starting with "/student" require student role.

### Users

Creates new account. - Also requires admin role
After creating account, new user gets the message with confirmation link. User has 3 days to confirm registration.
```
POST "/newUser" 

RequestBody :
{
"email": String,
"name": String,
"lastName": String,
"role": "STUDENT" or "MENTOR" or "ADMIN"
}
```

Updates user.
```
PUT "/admin/editUser/{id}" 

PathVariable: id 
RequestBody:
{
"email": String,
"name": String,
"lastName": String,
"role": "STUDENT" or "MENTOR" or "ADMIN"
}
```

Deactivates user (user cannot be deleted immediately, there is thread running 
every 10 days which deletes user who are deactivated more than a year).
```
POST "/admin/deactivateUser"  

RequestParam:
username
```

Displays list of all deactivated users.
```
GET "/admin/deactivatedUsers" 
```

Activates user who has been deactivated. 
After activating account, user gets the message with confirmation link. User has 3 days to confirm registration.

```
POST "/admin/activateUser" 

RequestParam:
username
```

Displays list of all students that are assigned to logged in mentor.
```
GET "/mentor/students" 
```

Assigns student to logged in mentor.
```
POST "/mentor/assignStudent" 

RequestParam:
username (of student)
```

Release student from logged in mentor.
```
POST "/mentor/releaseStudent"  

RequestParam:
username (of student)
```

Confirms registration - after creating new account, new user receives email with activation link. User has to 
click it to confirm registration. Role: Any role
```
GET "/newUser/registrationConfirm/{token}" 
```

Changes password. Role: Any role
```
POST "/changePassword" 

RequestParams:
oldPassword, newPassword
```


### Meetings

Displays list of all meetings.
```
GET "/admin/meetings" 
```

Displays list of all meetings during one day.
```
GET "/admin/meetingsForDate" 
```

Displays list of all past meetings.
```
GET "admin/pastMeetings" 
```

Displays list of all past meetings.
```
DELETE "admin/deleteMeeting" 

RequestParam: id
```

Displays list of meetings for logged in mentor.
```
GET "/mentor/meetings" 
```

Displays list of meetings for logged in mentor during one day.
```
GET "/mentor/meetingsForDate" 
```

Displays list of past meetings for logged in mentor.
```
GET "/mentor/pastMeetings" 
```

Allows mentor add the time frame during which the mentor is available for consultation.
```
POST "mentor/addMeetings" 

RequestParams: ZonedDateTime from, ZonedDateTime to
```

Displays list of meetings for logged in student.
```
GET "/student/meetings" 
```

Displays list of past meetings for logged in student.
```
GET "student/pastMeetings" 
```

Displays list of meetings for logged in student during one day.
```
GET "/student/meetingsForDate" 
```

Displays list of meetings for logged in student mentor's and show if it is booked.
```
GET "/student/mentorCalendar" 
```

Displays list of meetings for logged in student mentor's and show if it is booked 
during one day.
```
GET "/student/mentorCalendarForDate" 
```

Allows booking meeting by students with the mentor to whom they are assigned.
After the meeting is booked, student and mentor receive confirmation email.
```
POST "student/bookMeeting" 

RequestParam: id (meeting id)
```

Allows canceling meeting by student and mentor - Role: STUDENT, MENTOR
After the meeting is canceled, student and mentor receive confirmation email.

If meeting is cancelled by student, meeting is still available for booking to other student.
If meeting is cancelled by mentor, meeting is deleted.
```
POST "/cancelMeeting" 

RequestParam: id (meeting id)
```

