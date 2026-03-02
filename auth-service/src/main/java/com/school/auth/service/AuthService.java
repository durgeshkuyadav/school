package com.school.auth.service;

import com.school.auth.dto.*;
import com.school.auth.entity.RefreshToken;
import com.school.auth.entity.User;
import com.school.auth.exception.AuthException;
import com.school.auth.repository.RefreshTokenRepository;
import com.school.auth.repository.UserRepository;
import com.school.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.refresh-expiry-ms}")
    private long refreshExpiryMs;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new AuthException("Invalid username or password"));

        if (!user.isEnabled()) throw new AuthException("Account is disabled");
        if (!user.isAccountNonLocked()) throw new AuthException("Account is locked");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid username or password");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .classId(user.getClassId())
            .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshRequest request) {
        RefreshToken rt = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (rt.isExpired()) {
            refreshTokenRepository.delete(rt);
            throw new AuthException("Refresh token expired. Please login again.");
        }

        User user = rt.getUser();
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = createRefreshToken(user);
        refreshTokenRepository.delete(rt);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .classId(user.getClassId())
            .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
            .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AuthException("Username already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AuthException("Email already registered");

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(User.Role.valueOf(request.getRole()))
            .profileId(request.getProfileId())
            .classId(request.getClassId())
            .subjectIds(request.getSubjectIds())
            .enabled(true)
            .accountNonLocked(true)
            .build();

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new AuthException("Current password is incorrect");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        RefreshToken rt = RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(Instant.now().plusMillis(refreshExpiryMs))
            .build();
        return refreshTokenRepository.save(rt).getToken();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .classId(user.getClassId())
            .enabled(user.isEnabled())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
