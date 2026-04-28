package com.internverse.service;

import com.internverse.dto.UserCreateRequest;
import com.internverse.dto.UserResponse;
import com.internverse.exception.ApiException;
import com.internverse.model.Role;
import com.internverse.model.User;
import com.internverse.repository.UserRepository;
import com.internverse.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userRepository.findById(id).map(UserResponse::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findByRole(Role role) {
        return userRepository.findByRole(role).stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createByAdmin(UserCreateRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .college(req.getCollege())
                .skillsJson(JsonUtil.skillsToJson(req.getSkills()))
                .build();
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }
}
