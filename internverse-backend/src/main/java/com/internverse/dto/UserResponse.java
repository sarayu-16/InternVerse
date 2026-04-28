package com.internverse.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internverse.model.Role;
import com.internverse.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String college;
    private List<String> skills;
    private String createdAt;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static UserResponse fromEntity(User u) {
        List<String> skills = Collections.emptyList();
        if (u.getSkillsJson() != null && !u.getSkillsJson().isBlank()) {
            try {
                skills = MAPPER.readValue(u.getSkillsJson(), new TypeReference<>() {});
            } catch (Exception ignored) {
                skills = List.of(u.getSkillsJson().split(","));
            }
        }
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .college(u.getCollege())
                .skills(skills)
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().toString() : null)
                .build();
    }
}
