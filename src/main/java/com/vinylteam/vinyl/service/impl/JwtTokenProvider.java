package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.JwtAuthenticationException;
import com.vinylteam.vinyl.security.LogoutTokenStorageService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.ControllerResponseUtils;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.vinylteam.vinyl.security.SecurityConstants.AUTHORIZATION_HEADER_NAME;
import static com.vinylteam.vinyl.security.SecurityConstants.TOKEN_PREFIX;
import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider implements JwtService {

    private AuthenticationManager authenticationManager;

    private final UserService userService;
    private final UserMapper userMapper;
    private SecretKey secretKey;

    private LogoutTokenStorageService tokenStorageService;

    @Value("${jwt.token.expirationInSeconds:600}")
    private int validityInSeconds;

    private final UserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    @Autowired
    public void setTokenStorageService(LogoutTokenStorageService tokenStorageService) {
        this.tokenStorageService = tokenStorageService;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean isTokenValid(String token) {
        if (StringUtils.isBlank(token)) {
            log.debug("JWT token is invalid");
            return false;
        }
        try {
            Jws<Claims> claims = getClaims(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }
            return !tokenStorageService.isTokenBlocked(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT token is expired or invalid", e);
            //return false;
            throw new JwtAuthenticationException("JWT token is incorrect");
        }
    }


    @Override
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER_NAME);
        return extractToken(bearerToken);
    }

    @Override
    public String createToken(String userEmail, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims()
                .setSubject(userEmail);
        claims.put("authorities", authorities);

        Date now = new Date();
        Date validity = new Date(now.getTime() + SECONDS.toMillis(validityInSeconds));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public LocalDateTime getExpirationDate(String token) {
        Jws<Claims> claims = getClaims(token);

        Date expirationDate = claims.getBody().getExpiration();
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    public UserSecurityResponse getCheckResponseIfTokenValid(String token) {
        UserSecurityResponse response = new UserSecurityResponse();
        if (isTokenValid(token)) {
            var auth = getAuthentication(token);
            var authUser = (JwtUser) auth.getPrincipal();
            var map = getUserCredentialsMap(token, authUser);
            response = ControllerResponseUtils.getResponseFromMap(map);
        }
        return response;
    }

    @Override
    public UserSecurityResponse authenticateByRequest(LoginRequest loginRequest) {
        Authentication preparedAuth = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(preparedAuth);
        var authUser = (JwtUser) authentication.getPrincipal();
        var token = createToken(authUser.getUsername(), authUser.getAuthorities());
        return ControllerResponseUtils.getResponseFromMap(getUserCredentialsMap(token, authUser));
    }

    private Map<String, Object> getUserCredentialsMap(String token, JwtUser authUser) {
        String username = authUser.getUsername();
        User byEmail = userService.findByEmail(username);
        return Map.of(
                "user", userMapper.mapUserToDto(byEmail),
                "token", token);
    }

    private String getUsername(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private String extractToken(String bearerToken) {
        if (!Objects.isNull(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            bearerToken = bearerToken.replace(TOKEN_PREFIX, "");
        }
        return bearerToken;
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }

}
