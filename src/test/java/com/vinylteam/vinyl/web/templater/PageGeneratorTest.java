/*
package com.vinylteam.vinyl.web.templater;

import com.vinylteam.vinyl.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageGeneratorTest {

    @Test
    @DisplayName("Checks whether user context is set")
    void prepareUserContext() {
        Context context = new Context();
        Map<String, String> attributes = new HashMap<>();
        String role = Role.USER.toString();
        attributes.put("userRole", role);
        String email = "xyz@microsoft.com";
        attributes.put("email", email);
        String discogsUser = "discogsUser";
        attributes.put("discogsUserName", discogsUser);
        String discogsLink = "https://api.discogs.com";
        attributes.put("discogsLink", discogsLink);

        PageGenerator.getInstance().prepareUserContext(attributes, context);

        assertEquals(role, context.getVariable("userRole"));
        assertEquals(email, context.getVariable("email"));
        assertEquals(discogsUser, context.getVariable("discogsUserName"));
        assertEquals(discogsLink, context.getVariable("discogsLink"));
    }

}*/
