package com.vinylteam.vinyl.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserInfoRequest {

    private String email;
    private String password;
    private String passwordConfirmation;
    private String newPassword;
    private String newPasswordConfirmation;
    private String discogsUserName;
    private String newDiscogsUserName;
    private String token;

}
