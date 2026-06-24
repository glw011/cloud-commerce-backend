package com.garrettw011.orderflow.auth;

import com.garrettw011.orderflow.auth.dto.*;
import com.garrettw011.orderflow.common.exception.DuplicateResourceException;
import com.garrettw011.orderflow.customer.CustomerService;
import com.garrettw011.orderflow.user.Role;
import com.garrettw011.orderflow.user.User;
import com.garrettw011.orderflow.user.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserService userService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    public AuthService(UserService userService, CustomerService customerService, PasswordEncoder passwordEncoder,
                       JwtService jwtService, RefreshTokenService refreshTokenService, JwtProperties jwtProperties) {
        this.userService = userService;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.jwtProperties = jwtProperties;
    }

    private AuthResponse issueTokens(User user) {
        String access = jwtService.generateAccessToken(user);
        String refresh = refreshTokenService.issue(user.getId());
        return new AuthResponse(access, refresh, "Bearer", jwtProperties.accessTokenTtl().toSeconds());
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userService.emailExists(req.email())) {
            throw new DuplicateResourceException("Email already associated with another profile.");
        }

        User user = userService.createUser(req.email(), req.password(), Role.CUSTOMER);
        customerService.createProfile(user, req.firstName(), req.lastName(), req.phone());
        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userService.getByEmail(req.email());
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshTokenRequest req) {
        Long userId = refreshTokenService.consume(req.refreshToken());
        User user = userService.getById(userId);
        return issueTokens(user);
    }

    public CurrentUserResponse currentUser() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        User user = userService.getById(userId);
        return new CurrentUserResponse(user.getId(), user.getEmail(), user.getRole().name());
    }
}




