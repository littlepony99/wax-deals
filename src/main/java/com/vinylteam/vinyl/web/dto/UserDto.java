package com.vinylteam.vinyl.web.dto;

import com.vinylteam.vinyl.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String discogsUserName;
    private Role role;
    private boolean status;
}
