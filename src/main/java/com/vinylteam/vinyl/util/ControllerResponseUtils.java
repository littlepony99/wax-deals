package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.dao.jdbc.extractor.DefaultUserMapper;
import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.User;

import java.util.Map;

public class ControllerResponseUtils {

    private static UserMapper userMapper = new DefaultUserMapper();

    public static Map<String, String> getStatusInfoMap(String code, String message) {
        return Map.of(
                "resultCode", code,
                "message", message);
    }

    public static Map<String, String> getSuccessStatusInfoMap() {
        return getStatusInfoMap("0", "");
    }

    public static  Map<String, Object> getUserCredentialsMap(String token, User user) {
        return Map.of(
                "user", userMapper.mapUserToDto(user),
                "token", token);
    }

}
