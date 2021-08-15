package com.github.wirtzleg.scaling.dto;

import lombok.Data;

@Data
public class Subscription {
    private Long userId;

    private Status status;

    public enum Status {
        ACTIVE,
        PAST_DUE
    }
}
