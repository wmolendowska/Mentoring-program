package com.hacktyki.mentoring.exceptions;

public class MeetingAlreadyBookedException extends RuntimeException{

    public MeetingAlreadyBookedException(String message) {
        super(message);
    }
}
