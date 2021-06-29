package com.vinylteam.vinyl.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserPost {

    private long id;
    private String name;
    private String email;
    private String theme;
    private String message;
    private LocalDateTime createdAt;

}