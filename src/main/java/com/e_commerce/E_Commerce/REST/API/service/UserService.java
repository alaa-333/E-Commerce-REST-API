package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.SignupRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.UserUpdateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.mapper.UserMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(SignupRequestDto requestDto) {
        // 1. Validation
        if (userRepository.existsByEmail(requestDto.getUserCreateRequestDto().getEmail())) {
            throw new ValidationException(ErrorCode.DUPLICATE_ENTRY);
        }

        // 2. Mapping
        var user = userMapper.toEntity(requestDto.getUserCreateRequestDto());
        var customer = customerMapper.toEntity(requestDto.getCustomerCreateRequestDto());

        // 3. Security
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. Establishing relationship
        user.setCustomer(customer);
        customer.setUser(user);

        return userRepository.save(user);
    }

    public UserResponse getById(Long id)
    {
        var user = getUser(id);
        return userMapper.toResponse(user);
    }

    public void deleteUserById(Long id)
    {
        var user = getUser(id);

        userRepository.delete(user);
    }



    public UserResponse updateUser(Long id, UserUpdateRequestDto requestDto)
    {
        var user = getUser(id);
        var updatedUser = userMapper.updateUserFromDto(user , requestDto);
        return userMapper.toResponse(updatedUser);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public boolean existByEmail( String email) {

        return userRepository.existsByEmail(email);
    }
}
