package com.vinylteam.vinyl.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {

    USER("USER"),
    ADMIN("ADMIN");

    private String name;

}
