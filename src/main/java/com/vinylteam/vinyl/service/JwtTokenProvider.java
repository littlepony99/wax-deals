package com.vinylteam.vinyl.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

import static com.vinylteam.vinyl.security.SecurityConstants.AUTHORIZATION_HEADER_NAME;
import static com.vinylteam.vinyl.security.SecurityConstants.TOKEN_PREFIX;

@Service
@Slf4j
public class JwtTokenProvider implements JwtService {

    private SecretKey secretKey;

    @Value("${jwt.token.expired:120000}")
    private int validityInMilliseconds;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    @Override
    public boolean validateToken(String token) {
        if (StringUtils.isBlank(token)) {
            log.debug("JWT token is  invalid");
            return false;
        }
        try {
            Jws<Claims> claims = Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT token is expired or invalid", e);
            return false;
        }
    }

    @Override
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (!Objects.isNull(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            bearerToken = bearerToken.replace(TOKEN_PREFIX, "");
        }
        return bearerToken;
    }

    @Override
    public String createToken(String userEmail) {

        Claims claims = Jwts.claims().setSubject(userEmail);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

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

    private String getUsername(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}