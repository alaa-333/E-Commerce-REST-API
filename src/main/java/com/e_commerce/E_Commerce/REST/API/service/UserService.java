package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.SignupRequestDto;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.mapper.UserMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private CustomerMapper customerMapper;
    private UserMapper userMapper;
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Transactional // Essential for data integrity
    public User createUser(SignupRequestDto requestDto) {
        // 1. Validation
        if (userRepository.existsByEmail(requestDto.getUserCreateRequestDto().getEmail())) {
            throw new ValidationException(ErrorCode.DUPLICATE_ENTRY);
        }

        // 2. Mapping
        User user = userMapper.toEntity(requestDto.getUserCreateRequestDto());
        Customer customer = customerMapper.toEntity(requestDto.getCustomerCreateRequestDto());

        // 3. Security
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. Establishing relationship
        user.setCustomer(customer);
        customer.setUser(user);

        return userRepository.save(user);
    }

    public boolean existByEmail(String email)
    {
        return userRepository.existsByEmail(email);
    }


}
