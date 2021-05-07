package com.threeline.ng.cardverifier.models;

import lombok.Data;

@Data
public class Card {
    private String scheme;
    private String type;
    private String bank;
}
