package com.vinylteam.vinyl.entity;

public enum JwtTokenType {
    ACCESS("access"),
    REFRESH("refresh");

    private String type;

    JwtTokenType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
