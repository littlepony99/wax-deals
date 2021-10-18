package com.vinylteam.vinyl.service.impl;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.service.ExternalUserService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


@Service("facebookUserService")
@RequiredArgsConstructor
@Slf4j
public class FacebookUserServiceImpl implements ExternalUserService {

    private final UserDao userDao;

    private final UserMapper userMapper;

    private final UserService userService;

    private final JwtService jwtService;

    @Override
    public UserSecurityResponse processExternalAuthorization(String token) throws ServerException {
        var response = new UserSecurityResponse();
        if (StringUtils.isBlank(token)) {
            return response;
        }
        FacebookClient facebookClient = new DefaultFacebookClient(Version.VERSION_12_0);

        FacebookClient tokenizedClient = facebookClient.createClientWithAccessToken(token);
        User fbUser = tokenizedClient.fetchObject("/me", User.class, Parameter.with("fields", "email,first_name,last_name,gender"));
        String newUserEmail = fbUser.getEmail();
        if (newUserEmail.isEmpty()) {
            return response;
        }
        var appUser = userDao.findByEmail(newUserEmail);
        if (appUser.isEmpty()) {//no appUser yet
            log.info("No user by this email, creating new user {'email':{}}", newUserEmail);
            String emailToRegister = newUserEmail;
            UserInfoRequest registerRequest = UserInfoRequest.builder()
                    .email(emailToRegister)
                    .build();
            appUser = userService.registerExternally(registerRequest);
        }
        com.vinylteam.vinyl.entity.User user = appUser.get();
        response.setMessage(user.getEmail());
        response.setUser(userMapper.mapUserToDto(user));
        var newTokenPair = jwtService.getTokenPair(userMapper.mapToDto(user));
        response.setJwtToken(newTokenPair.getJwtToken());
        response.setRefreshToken(newTokenPair.getRefreshToken());
        return response;
    }
}
