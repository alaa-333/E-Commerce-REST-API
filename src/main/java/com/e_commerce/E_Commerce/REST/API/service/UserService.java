package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.SignupRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.UserUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.mapper.UserMapper;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.model.enums.Role;
import com.e_commerce.E_Commerce.REST.API.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.emails:}")
    private List<String> adminEmails;

    @Transactional
    public User createUser(SignupRequestDTO requestDto) {
        log.debug("Creating user with email: {}", requestDto.getUserCreateRequestDto().getEmail());
        
        // 1. Validation
        if (userRepository.existsByEmail(requestDto.getUserCreateRequestDto().getEmail())) {
            throw new ValidationException(ErrorCode.DUPLICATE_ENTRY);
        }

        // 2. Mapping
        User user = userMapper.toEntity(requestDto.getUserCreateRequestDto());
        var customer = customerMapper.toEntity(requestDto.getCustomerCreateRequestDto());

        // 3. Security - Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. Role assignment - Check if email is in admin list
        if (adminEmails != null && adminEmails.contains(requestDto.getUserCreateRequestDto().getEmail())) {
            user.setRoles(Set.of(Role.ROLE_ADMIN));
            log.info("Admin role assigned to user: {}", user.getEmail());
        } else {
            user.setRoles(Set.of(Role.ROLE_USER));
        }
        
        // 5. Establishing relationship
        user.setCustomer(customer);
        customer.setUser(user);

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public UserResponse getById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        User user = getUser(id);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        log.debug("Deleting user by ID: {}", id);
        User user = getUser(id);
        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequestDTO requestDto) {
        log.debug("Updating user with ID: {}", id);
        User user = getUser(id);
        User updatedUser = userMapper.updateUserFromDto(user, requestDto);
        userRepository.save(updatedUser);
        log.info("User updated successfully with ID: {}", id);
        return userMapper.toResponse(updatedUser);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
    }

    public boolean isExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
