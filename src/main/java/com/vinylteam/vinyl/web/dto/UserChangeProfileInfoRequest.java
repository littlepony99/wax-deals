package com.vinylteam.vinyl.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserChangeProfileInfoRequest {

    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
    private String newDiscogsUserName;
    private String token;

}
