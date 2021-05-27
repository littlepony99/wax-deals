package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.entity.User;

public interface SecurityService {

    User createUserWithHashedPassword(String email, char[] password, String discogsUserName);

    boolean checkPasswordAgainstUserPassword(User user, char[] password);

}
