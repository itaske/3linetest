package com.threeline.ng.cardverifier.responses;

import lombok.Data;

@Data
public class ErrorResponse {
    private int code;
    private String status;
    private String message;
    private String timestamp;
}
