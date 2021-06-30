package com.vinylteam.vinyl.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CaptchaResponse {

    private boolean success;
    private LocalDateTime challenge_ts;
    private String hostname;
    @JsonProperty("error-codes")
    private List<String> errorCodes;
}
