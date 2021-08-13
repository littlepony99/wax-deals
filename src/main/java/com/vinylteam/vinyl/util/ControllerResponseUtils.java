package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.dao.jdbc.extractor.DefaultUserMapper;
import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.dto.UserDto;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;

import java.util.Map;

public class ControllerResponseUtils {

    private static final UserMapper userMapper = new DefaultUserMapper();

    public static UserSecurityResponse getResponseFromMap(Map<String, Object> responseMap){
        UserSecurityResponse response = new UserSecurityResponse();
        response.setMessage((String)responseMap.get("message"));
        response.setResultCode((String)responseMap.get("resultCode"));
        response.setToken((String)responseMap.get("token"));
        response.setUser((UserDto)responseMap.get("user"));
        return response;
    }

    public static Map<String, String> getStatusInfoMap(String code, String message) {
        return Map.of(
                "resultCode", code,
                "message", message);
    }

    public static UserSecurityResponse setStatusInfo(UserSecurityResponse response, String code, String message) {
        response.setMessage(message);
        response.setResultCode(code);
        return response;
    }

    public static UserSecurityResponse setSuccessStatusInfo(UserSecurityResponse response) {
        return setStatusInfo(response,"0", "");
    }

    public static  Map<String, Object> getUserCredentialsMap(String token, User user) {
        return Map.of(
                "user", userMapper.mapUserToDto(user),
                "token", token);
    }

}
