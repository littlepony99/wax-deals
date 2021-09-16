package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtTokenType;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.TokenPair;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.vinylteam.vinyl.security.SecurityConstants.*;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider implements JwtService {

    private AuthenticationManager authenticationManager;

    private final UserService userService;
    private final UserMapper userMapper;
    private SecretKey secretKey;

    @Autowired
    private LogoutTokenStorageService tokenStorageService;

    @Value("${jwt.token.expirationInSeconds:600}")
    private int accessTokenValidityInSeconds;

    @Value("${jwt.refreshtoken.expirationInSeconds:1800}")
    private int refreshTokenValidityInSeconds;

    private final UserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean isTokenValid(String token) {
        return isTokenValid(token, null);
    }

    @Override
    public boolean isTokenValid(String token, String expectedTokenType) {
        boolean isTokenTypeExpected = true;
        if (StringUtils.isBlank(token)) {
            log.debug("JWT token is invalid");
            return false;
        }
        try {
            Jws<Claims> claims = getClaims(token);
            String pairIdentifier = getPairIdentifier(claims);
            if (Objects.isNull(pairIdentifier) || getExpirationDate(claims).isBefore(LocalDateTime.now())) {
                return false;
            }
            if (!StringUtils.isBlank(expectedTokenType)) {
                log.debug("JWT token TYPE {'tokenType' :{}}", expectedTokenType);
                String actualTokenType = (String) claims.getBody().get("type");
                isTokenTypeExpected = expectedTokenType.equals(actualTokenType);
            }
            return isTokenTypeExpected && !tokenStorageService.isTokenPairBlocked(pairIdentifier);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT token is expired or invalid", e);
            return false;
        }
    }

    @Override
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER_NAME);
        return extractToken(bearerToken);
    }

    @Override
    public String createAccessToken(JwtUser user, String pairIdentifier) {
        return createToken(JwtTokenType.ACCESS, accessTokenValidityInSeconds, user, pairIdentifier);
    }

    @Override
    public String createRefreshToken(JwtUser user, String pairIdentifier) {
        return createToken(JwtTokenType.REFRESH, refreshTokenValidityInSeconds, user, pairIdentifier);
    }

    @Override
    public void tryJwtAuthorization(HttpServletRequest request) {
        String token = extractToken(request);
        tryJwtAuthorization(request, token);
    }

    @Override
    public boolean tryJwtAuthorization(HttpServletRequest request, String token) {
        String path = request.getRequestURI();
        String expectedTokenType = path.contains("/token/refresh-token") ? "refresh" : "access";
        if (isTokenValid(token, expectedTokenType)) {
            Authentication auth = getAuthentication(token);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
                JwtUser principal = (JwtUser) auth.getPrincipal();
                var user = userService.findByEmail(principal.getUsername());
                request.setAttribute("jwtToken", token);
                request.setAttribute("userEntity", user);
                return true;
            }
        }
        return false;
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public LocalDateTime getExpirationDate(String token) {
        Jws<Claims> claims = getClaims(token);
        return getExpirationDate(claims);
    }

    @Override
    public String getPairIdentifier(String token) {
        Jws<Claims> claims = getClaims(token);
        return getPairIdentifier(claims);
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
        return prepareUserSecurityResponse(authenticationManager.authenticate(preparedAuth));
    }

    @Override
    public TokenPair getTokenPair(JwtUser user) {
        var tokenPairIdentifier = UUID.randomUUID().toString();
        return TokenPair.builder()
                .id(tokenPairIdentifier)
                .jwtToken(createAccessToken(user, tokenPairIdentifier))
                .refreshToken(createRefreshToken(user, tokenPairIdentifier))
                .build();
    }

    @Override
    public UserSecurityResponse refreshByToken(String refreshToken) {
        Jws<Claims> claims = getClaims(refreshToken);
        String pairIdentifier = getPairIdentifier(claims);
        LocalDateTime expirationDate = getExpirationDate(claims);
        tokenStorageService.storePairIdentifier(pairIdentifier, expirationDate);
        return prepareUserSecurityResponse(SecurityContextHolder.getContext().getAuthentication());
    }

    private UserSecurityResponse prepareUserSecurityResponse(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof JwtUser)) {
            throw new JwtAuthenticationException("JWT Token is not valid");
        }
        var authUser = (JwtUser) authentication.getPrincipal();
        var newTokenPair = getTokenPair(authUser);
        return ControllerResponseUtils.getResponseFromMap(getUserCredentialsMap(newTokenPair.getJwtToken(), newTokenPair.getRefreshToken(), authUser));
    }

    private String createToken(JwtTokenType tokenType, int validityInSeconds, JwtUser user, String pairIdentifier) {
        Claims claims = Jwts.claims()
                .setSubject(user.getUsername());
        claims.put("authorities", user.getAuthorities());
        claims.put("type", tokenType.getType());
        claims.put(TOKEN_PAIR_IDENTIFIER_NAME, pairIdentifier);

        Date now = new Date();
        Date validity = new Date(now.getTime() + SECONDS.toMillis(validityInSeconds));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    private String getPairIdentifier(Jws<Claims> claims) {
        return (String) claims.getBody().get(TOKEN_PAIR_IDENTIFIER_NAME);
    }

    private LocalDateTime getExpirationDate(Jws<Claims> claims) {
        Date expirationDate = claims.getBody().getExpiration();
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private Map<String, Object> getUserCredentialsMap(String accessToken, String refreshToken, JwtUser authUser) {
        User byEmail = userService.findByEmail(authUser.getUsername());
        return Map.of(
                "user", userMapper.mapUserToDto(byEmail),
                "refreshToken", refreshToken,
                "accessToken", accessToken);
    }

    private Map<String, Object> getUserCredentialsMap(String token, JwtUser authUser) {
        User byEmail = userService.findByEmail(authUser.getUsername());
        return Map.of("user", userMapper.mapUserToDto(byEmail), "token", token);
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
