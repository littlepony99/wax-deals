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

import static com.restfb.Version.VERSION_12_0;


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
        FacebookClient facebookClient = new DefaultFacebookClient(VERSION_12_0);

        FacebookClient tokenizedClient = facebookClient.createClientWithAccessToken(token);
        User fbUser = tokenizedClient.fetchObject("/me", User.class, Parameter.with("fields", "email,first_name,last_name,gender"));
        String newUserEmail = fbUser.getEmail();
        if (newUserEmail.isEmpty()) {
            return response;
        }
        var appUser = userDao.findByEmail(newUserEmail);
        if (appUser.isEmpty()) {//no appUser yet
            log.info("No appUser exists for token {}", token);
            UserInfoRequest registerRequest = UserInfoRequest.builder()
                    .email(newUserEmail)
                    .build();
            appUser = userService.registerExternally(registerRequest);
        }
        response.setUser(userMapper.mapUserToDto(appUser.get()));
        response.setMessage(response.getUser().getEmail());
        var newTokenPair = jwtService.getTokenPair(userMapper.mapToDto(appUser.get()));
        response.setTokenPair(newTokenPair);
        return response;
    }
}
