package com.ecommerce.api.service;

import com.ecommerce.api.dto.request.user.UpdateUserRequest;
import com.ecommerce.api.dto.response.UserResponse;
import com.ecommerce.api.entity.User;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.UserMapper;
import com.ecommerce.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieve user information by ID.
     * Users can only access their own profile.
     */
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {

        log.info("fetching user with id: {}", id);

        checkUserAuthorization(id);

        var userResponse = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "user not found with id: " + id));

        return userMapper.toResponse(userResponse);
    }

    /**
     * Update user profile and password.
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(Long id, UpdateUserRequest request) {
        log.info("updating user with id: {}", id);

        checkUserAuthorization(id);

        var user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "user not found with id: " + id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("failed password verification for user id: {}", id);
            throw new EcommerceAppException(ErrorCode.INVALID_PASSWORD, "current password is incorrect");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            log.warn("new password same as current for user id: {}", id);
            throw new EcommerceAppException(ErrorCode.INVALID_PASSWORD, "new password must be different from current password");
        }

        // Update email
        user.setUsername(request.getEmail());

        String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hashedNewPassword);

        int effectedRows = userRepository.updateUser(user);

        boolean success = effectedRows > 0;

        if (success) {
            log.info("user id: {} successfully updated", id);
        } else {
            log.error("failed to update user id: {}", id);
        }

        return success;
    }

    /**
     * Delete user account.
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id, String passwordConfirmation) {
        log.info("deleting user with id: {}", id);

        checkUserAuthorization(id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "user not found with id: " + id));

        if (!passwordEncoder.matches(passwordConfirmation, user.getPassword())) {
            log.warn("failed password confirmation for user deletion, user id: {}", id);
            throw new EcommerceAppException(ErrorCode.INVALID_PASSWORD, "Password confirmation failed. Account not deleted.");
        }

        int effectedRow = userRepository.deleteUser(user.getId());

        boolean success = effectedRow > 0;

        if (success) {
            log.info("user id: {} successfully deleted", id);
        } else {
            log.error("failed to delete user id: {}", id);
        }

        return success;
    }

    /**
     * Get the currently authenticated user from SecurityContext.
     */
    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException(ErrorCode.UNAUTHORIZED, "user not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            log.error("invalid authentication principal type: {}",
                    principal != null ? principal.getClass().getName() : "null");
            throw new EcommerceAppException(
                    ErrorCode.UNAUTHORIZED,
                    "invalid authentication principal type");
        }

        return (User) principal;
    }



    /**
     * Check authorization - user can only access their own data.
     */
    private void checkUserAuthorization(Long userId) {
        var currentUser = getCurrentUser();

        if (!currentUser.getId().equals(userId)) {
            log.warn("unauthorized access occur by user {} to resource of user {}", currentUser.getId(), userId);
            throw new EcommerceAppException(ErrorCode.UNAUTHORIZED, "you can only access your own profile");
        }
    }
}

