package com.vinylteam.vinyl.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class ConfirmationToken {

    private long id;
    private long userId;
    private UUID token;
    private Timestamp timestamp;

}