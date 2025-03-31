package com.ossant.photoapp.api.users.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossant.photoapp.api.users.dto.UserDto;
import com.ossant.photoapp.api.users.model.LoginRequestModel;
import com.ossant.photoapp.api.users.service.UsersService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UsersService usersService;

    private final Environment environment;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UsersService usersService, Environment environment) {
        super(authenticationManager);
        this.usersService = usersService;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestModel loginRequestModel = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestModel.class);
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestModel.getEmail(), loginRequestModel.getPassword(), new ArrayList<>()));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        UserDto userDto = usersService
                .getUserDetailsByEmail(((User) authResult.getPrincipal()).getUsername());

        long tokenExpirationInMs = Long
                .parseLong(Objects.requireNonNull(environment.getProperty("token.expiration.time")));

        byte[] tokenSecretKeyBytes = Base64.getEncoder()
                .encode(Objects.requireNonNull(environment.getProperty("token.secret.key")).getBytes());

        SecretKey secretKey = Keys.hmacShaKeyFor(tokenSecretKeyBytes);

        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject(userDto.getUserId())
                .expiration(Date.from(now.plusMillis(tokenExpirationInMs)))
                .issuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());
    }
}
