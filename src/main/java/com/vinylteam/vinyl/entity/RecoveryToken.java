package com.vinylteam.vinyl.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class RecoveryToken {

    private int id;
    private long userId;
    private String token;
    private Timestamp createdAt;

}

