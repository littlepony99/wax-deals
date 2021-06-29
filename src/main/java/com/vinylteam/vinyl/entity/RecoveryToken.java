package com.vinylteam.vinyl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryToken {

    private long id;
    private long userId;
    private UUID token;
    private Timestamp createdAt;

}

