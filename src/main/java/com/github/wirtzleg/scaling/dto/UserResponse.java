package com.github.wirtzleg.scaling.dto;

import lombok.Data;

@Data
public class UserResponse {
    private final Long id;
    private final String name;
    private final boolean online;

    public UserResponse(User user, boolean online) {
        this.id = user.getId();
        this.name = user.getNickName();
        this.online = online;
    }
}
