package com.vinylteam.vinyl.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

// Lombok annotations
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaRequestDto {

    @NonNull
    private String name;
    @NonNull
    private String email;
    @NonNull
    private String subject;
    @NonNull
    private String message;
    @NonNull
    private String captchaResponse;
}
