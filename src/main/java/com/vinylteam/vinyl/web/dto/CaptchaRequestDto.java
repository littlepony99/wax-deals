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
    String name;
    @NonNull
    String email;
    @NonNull
    String subject;
    @NonNull
    String message;
    @NonNull
    String captchaResponse;
}
