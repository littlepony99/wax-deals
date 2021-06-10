package com.vinylteam.vinyl.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class RecoveryToken {

    private long id;
    private long userId;
    private UUID token;
    private Timestamp createdAt;

}

