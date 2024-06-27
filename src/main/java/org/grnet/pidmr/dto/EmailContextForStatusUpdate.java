package org.grnet.pidmr.dto;

import lombok.Getter;
@Getter
public class EmailContextForStatusUpdate {

    private String userID;
    private String email;
    private Long requestID;
    private String requestStatus;

    public EmailContextForStatusUpdate(String userID, String email, Long requestID, String requestStatus) {
        this.userID = userID;
        this.email = email;
        this.requestID = requestID;
        this.requestStatus = requestStatus;
    }
}