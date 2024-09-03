package org.grnet.pidmr.dto;

import lombok.Getter;
@Getter
public class EmailContextForStatusUpdate {

    private String userID;
    private String email;
    private Long requestID;
    private String requestStatus;
    private String pidType;

    private String timestamp;


    public EmailContextForStatusUpdate(String userID, String email, Long requestID, String requestStatus) {
        this.userID = userID;
        this.email = email;
        this.requestID = requestID;
        this.requestStatus = requestStatus;
    }

    public EmailContextForStatusUpdate(String userID, String email, Long requestID, String requestStatus, String pidType, String timestamp) {
        this.userID = userID;
        this.email = email;
        this.requestID = requestID;
        this.requestStatus = requestStatus;
        this.pidType = pidType;
        this.timestamp = timestamp;
    }
    public EmailContextForStatusUpdate(String userID, String email, Long requestID, String requestStatus, String timestamp) {
        this.userID = userID;
        this.email = email;
        this.requestID = requestID;
        this.requestStatus = requestStatus;
        this.timestamp = timestamp;
    }
}