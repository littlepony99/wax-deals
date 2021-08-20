package com.vinylteam.vinyl.web.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

@Data
@Builder
public class AddUserPostDto {

    @NonNull
    private String name;
    @NonNull
    private String email;
    @NonNull
    private String message;
    @NonNull
    private String captchaResponse;

}
