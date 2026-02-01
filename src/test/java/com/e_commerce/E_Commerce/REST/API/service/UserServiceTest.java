package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.*;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.exception.DuplicateResourceException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.mapper.UserMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // -> enable mockito for JUnit5
class UserServiceTest {

    // =============== mocking dependencies ==================

    @Mock // -> create mock instance
    private  CustomerMapper customerMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SignupRequestDto signupRequestDto ;

    @Mock
    private  UserMapper userMapper;

    @Mock
    private  UserRepository userRepository;


    @InjectMocks // -> create instance and inject mocked dependencies
    private UserService userService;


    // =============== createUser Test ==================
    @Test
    void createUser_whenEmailNotExist_shouldCreateUser() {
        // Arrange
        String email = "333alaamo@gmail.com";

        SignupRequestDto dto = mock(SignupRequestDto.class);
        UserCreateRequestDto userCreateDto = mock(UserCreateRequestDto.class);
        CustomerCreateRequestDto customerCreateDto = mock(CustomerCreateRequestDto.class);

        when(dto.getUserCreateRequestDto()).thenReturn(userCreateDto);
        when(dto.getCustomerCreateRequestDto()).thenReturn(customerCreateDto);
        when(userCreateDto.getEmail()).thenReturn(email);
        when(userCreateDto.getPassword()).thenReturn("raw-password");

        User user = new User();
        user.setEmail(email);
        user.setPassword("raw-password");

        Customer customer = new Customer();

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userMapper.toEntity(userCreateDto)).thenReturn(user);
        when(customerMapper.toEntity(customerCreateDto)).thenReturn(customer);
        when(passwordEncoder.encode("raw-password")).thenReturn("hashed-password");
        when(userRepository.save( any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);

        // Verify
        verify(userRepository).existsByEmail(email);
        verify(userMapper).toEntity(userCreateDto);
        verify(customerMapper).toEntity(customerCreateDto);
        verify(passwordEncoder).encode("raw-password");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_whenEmailExists_shouldThrowValidationException() {
        // Arrange
        String email = "333alaamo@gmail.com";

        SignupRequestDto dto = mock(SignupRequestDto.class);
        UserCreateRequestDto userCreateDto = mock(UserCreateRequestDto.class);

        when(dto.getUserCreateRequestDto()).thenReturn(userCreateDto);
        when(userCreateDto.getEmail()).thenReturn(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_ENTRY.name());

        // Verif
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save((User) any(User.class));
        verifyNoInteractions(userMapper, customerMapper, passwordEncoder);
    }





    // =============== getById test ==================
    @Test
    void getById_whenUserExists_shouldReturnUser() {

        // arrangee
        Long userId = 1l;
        String email = "lol@gmail.com";
        User user = mock(User.class);
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(user.getEmail()).thenReturn(email);
        when(user.getId()).thenReturn(userId);
        when(userMapper.toResponse(user)).thenReturn(response);

        // act
       userService.getById(userId);

        // assert
        assertThat(response.id()).isEqualTo(user.getId());
        assertThat(response.email()).isEqualTo(user.getEmail());

        // verify
        verify(userRepository).findById(anyLong());
        verify(userMapper).toResponse(any());

    }

    @Test
    void getById_whenUserDoesNotExists_shouldThrowException()
    {

        Long userId = 99L;
        // option 1
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // assert + act
        assertThatThrownBy( () -> userService.getById(userId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("user not found");
    }






    // =============== deleteUserById test ==================
    @Test
    void deleteUserById_whenUserExists_shouldBeDeleted()
    {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(any()));

        //act
        userService.deleteUserById(userId);
        // verify
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserById_whenUserDoesNotExist_shouldThrowException()
    {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //assert + act
        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("user not found");


    }

    @Test
    void updateUser_whenUserExists_shouldUpdating()
    {

        Long userId = 2L;
        String userEmail = "oldEmail@gmail.com";
        String dtoEmail = "newEmail@gmail.com";
        User dbUser = mock(User.class);
        UserUpdateRequestDto dto = mock(UserUpdateRequestDto.class);
        User updatedUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(dbUser));
        when(dbUser.getEmail()).thenReturn(userEmail);
        when(dto.getEmail()).thenReturn(dtoEmail);
        when(userMapper.updateUserFromDto(dbUser,dto)).thenReturn(updatedUser);

        // act
        UserResponse response = userService.updateUser(userId,dto);

        // assert
        assertThat(response.email()).isEqualTo(dtoEmail);

        // verivy
        verify(userRepository.findById(userId));
        verify(userMapper.updateUserFromDto(dbUser, dto));
        verify(userMapper.toResponse(updatedUser));
        verify(userRepository.save(updatedUser));
    }

    @Test
    void updateUser_whenUserDoesNotExists_shouldThrowException()
    {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, any()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("user not found");

        verify(userRepository).findById(userId);

    }



}