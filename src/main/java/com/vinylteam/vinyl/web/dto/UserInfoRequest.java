package com.vinylteam.vinyl.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequest {

    private String email;
    private String password;
    @JsonAlias("confirmPassword")
    private String passwordConfirmation;
    private String newPassword;
    private String newPasswordConfirmation;
    private String discogsUserName;
    private String newDiscogsUserName;
    private String token;

}
