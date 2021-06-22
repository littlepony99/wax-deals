package com.vinylteam.vinyl.web.util;

/*
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebUtilsTest {

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);

    @Test
    @DisplayName("Session is null")
    void setUserAttributesNoSession() {
        Map<String, String> attributes = new HashMap<>();
        when(mockedRequest.getSession(false)).thenReturn(null);
        //when
        WebUtils.setUserAttributes(mockedRequest, attributes);
        //then
        assertNull(attributes.get("userRole"));
    }

    @Test
    @DisplayName("Session is existing, user is null")
    void setUserAttributesNoUser() {
        Map<String, String> attributes = new HashMap<>();

        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        //when
        WebUtils.setUserAttributes(mockedRequest, attributes);
        //then
        assertNull(attributes.get("userRole"));
    }

    @Test
    @DisplayName("User is loged in")
    void setUserAttributesForUser() {
        Map<String, String> attributes = new HashMap<>();

        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);

        //when
        WebUtils.setUserAttributes(mockedRequest, attributes);
        //then
        assertEquals(Role.USER.toString(), attributes.get("userRole"));
    }

    @Test
    @DisplayName("Admin is loged in")
    void setUserAttributesForAdmin() {
        Map<String, String> attributes = new HashMap<>();

        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.ADMIN);

        //when
        WebUtils.setUserAttributes(mockedRequest, attributes);
        //then
        assertEquals(Role.ADMIN.toString(), attributes.get("userRole"));
    }
}*/
