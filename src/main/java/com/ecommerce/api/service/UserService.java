package com.ecommerce.api.service;

import com.ecommerce.api.dto.request.user.UpdateUserRequest;
import com.ecommerce.api.dto.response.PagedResponse;
import com.ecommerce.api.dto.response.UserResponse;
import com.ecommerce.api.entity.User;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.UserMapper;
import com.ecommerce.api.repository.RefreshTokenRepository;
import com.ecommerce.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for user management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieve user information by ID.
     * Users can only access their own profile.
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public UserResponse getUser(Long id) {

        log.info("fetching user with id: {}", id);


        var userResponse = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "user not found with id: " + id));

        return userMapper.toResponse(userResponse);
    }

    /**
     * Update user profile and password.
     */
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public boolean updateUser(Long id, UpdateUserRequest request) {
        log.info("updating user with id: {}", id);


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
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public boolean deleteUser(Long id, String passwordConfirmation) {
        log.info("deleting user with id: {}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "user not found with id: " + id));

        if (!passwordEncoder.matches(passwordConfirmation, user.getPassword())) {
            log.warn("failed password confirmation for user deletion, user id: {}", id);
            throw new EcommerceAppException(ErrorCode.INVALID_PASSWORD, "Password confirmation failed. Account not deleted.");
        }


        // imple soft delete
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setEnabled(false);

        int effectedRow = refreshTokenRepository.deleteTokensUsingUserId(id);
        log.info("deleted {} refresh tokens for user id: {}", effectedRow, id);

        boolean success = effectedRow > 0;

        if (success) {
            log.info("user id: {} successfully deleted", id);
        } else {
            log.error("failed to delete user id: {}", id);
        }

        return success;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<UserResponse> getAllUsers(int page, int size) {

        var pageRequest = PageRequest.of(page, size);

        var response = userRepository.findAllByDeleted(false, pageRequest)
                .map(userMapper::toResponse);
        return PagedResponse.from(response);
    }
}

