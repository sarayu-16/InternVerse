package com.internverse.util;

import com.internverse.model.User;
import com.internverse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String email = auth.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public User requireCurrentUser() {
        User u = currentUser();
        if (u == null) {
            throw new com.internverse.exception.ApiException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return u;
    }
}
