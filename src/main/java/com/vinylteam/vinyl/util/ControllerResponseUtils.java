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
        response.setToken((String)responseMap.get("token"));
        response.setJwtToken((String)responseMap.get("accessToken"));
        response.setRefreshToken((String)responseMap.get("refreshToken"));
        response.setUser((UserDto)responseMap.get("user"));
        return response;
    }

    public static Map<String, String> getStatusInfoMap(String code, String message) {
        return Map.of("message", message);
    }

    public static UserSecurityResponse getResponseWithMessage(String message) {
        return setStatusInfo(new UserSecurityResponse(), null, message);
    }

    public static UserSecurityResponse setStatusInfo(UserSecurityResponse response, String code, String message) {
        response.setMessage(message);
        return response;
    }

    public static UserSecurityResponse setSuccessStatusInfo(UserSecurityResponse response) {
        return setStatusInfo(response,"0", "");
    }

}
