package com.threeline.ng.cardverifier.responses;

import lombok.Data;

@Data
public class VerifyPayload {
    private String scheme;
    private String type;
    private String bank;
}
