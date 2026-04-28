package com.internverse.service;

import com.internverse.dto.AuthRequest;
import com.internverse.dto.AuthResponse;
import com.internverse.dto.RegisterRequest;
import com.internverse.exception.ApiException;
import com.internverse.model.Role;
import com.internverse.model.User;
import com.internverse.repository.UserRepository;
import com.internverse.security.JwtService;
import com.internverse.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.INTERN)
                .college(req.getCollege())
                .skillsJson(JsonUtil.skillsToJson(req.getSkills()))
                .build();
        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        org.springframework.security.core.userdetails.UserDetails ud =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build();
        String token = jwtService.generateToken(ud, user.getRole(), user.getId());
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
