package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.*;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.exception.DuplicateResourceException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.mapper.UserMapper;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // -> enable mockito for JUnit5
class UserServiceTest {

    // =============== mocking dependencies ==================

    @Mock // -> create mock instance
    private  CustomerMapper customerMapper;

    @Mock
    private SignupRequestDto signupRequestDto ;

    @Mock
    private  UserMapper userMapper;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  PasswordEncoder passwordEncoder;

    @InjectMocks // -> create instance and inject mocked dependencies
    private UserService userService;


    // =============== createUser Test ==================
    @Test
    public void createUser_whenEmailNotExist_shouldCreateUser() {
        // Arrange -> create nessasary data
        String email = "333alaamo@gmail.com";
        String password = "369!@#lolLOL";
        SignupRequestDto dto = new SignupRequestDto(
                        new UserCreateRequestDto(email, password),
                        new CustomerCreateRequestDto("alaa", "mo", "01062315586",
                        new AddressRequestDTO("cairo","1s","123654","egypt"))
        );
        User user =  User.builder()
                .id(1l)
                .email(email)
                .createdAt(LocalDateTime.now())
                .password(password)
                .build();
        when(userRepository.existsByEmail(dto.getUserCreateRequestDto().getEmail())).thenReturn(false);
        when(userRepository.save((User) any(User.class))).thenReturn(user);
        // act
        User result = userService.createUser(dto);
        // assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("333alaamo@gmail.com");
        // verify interactions
        verify(userRepository).existsByEmail("333alaamo@gmail.com");
        verify(userRepository).save((User) any(User.class));
    }

    public void createUser_whenEmailExists_shouldThrowException()
    {
        // Arrange -> create nessasary data
        String email = "333alaamo@gmail.com";
        String password = "369!@#lolLOL";
        SignupRequestDto dto = new SignupRequestDto(
                        new UserCreateRequestDto(email, password),
                        new CustomerCreateRequestDto("alaa", "mo", "01062315586",
                        new AddressRequestDTO("cairo","1s","123654","egypt")
                )
        );
        User user =  User.builder()
                .id(1l)
                .email(email)
                .createdAt(LocalDateTime.now())
                .password(password)
                .build();

        when(userRepository.existsByEmail(dto.getUserCreateRequestDto().getEmail())).thenReturn(true);
        assertThrows(DuplicateResourceException.class , () -> {
            userService.createUser(dto);

            verify(userRepository, never()).save((User) any(User.class));
        });

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("user already exist");


    }


    // =============== getById test ==================
    @Test
    void getById_whenExistsEmail_shouldReturnUser() {

        Long userId = 1l;
        User expectedUser =  User.builder()
                .email("11alaamo@gmail.com")
                .password("password")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // act
        User result = userService.getById(userId);

        // assert
        assertThat(result).isEqualTo(expectedUser);
        verify(userRepository).findById(userId);
    }

    @Test
    void getById_whenUserDoesNotExists_shouldThrowException()
    {
        Long userId = 2l;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // assert + act
        assertThatThrownBy( () -> userService.getById(userId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("user not found");
    }



    // =============== deleteUserById test ==================
    @Test
    void deleteUserById_whenUserExists_shouldDeleted()
    {
        Long userId = 1l;
        User expectedUser =  User.builder()
                .email("11alaamo@gmail.com")
                .password("password")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        doNothing().when(userRepository).delete(expectedUser);


        //act
        userService.deleteUserById(userId);

        // assert


        // verify
        verify(userRepository).findById(userId);
        verify(userRepository).delete((User) any(User.class));
    }

    @Test
    void deleteUserById_whenUserDoesNotExist_shouldThrowException()
    {
        Long userId = 1l;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //assert + act
        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("user not found");


    }

    @Test
    void updateUser_whenUserExists_shouldUpdating()
    {
        Long userId = 1l;
        String email = "333alaamo@gmail.com";
        String password = "369!@#lolLOL";
        UserUpdateRequestDto dto = new UserUpdateRequestDto(email,password,true,true);
        User expectedUser =  User.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));


        // act
        UserResponse result = userService.updateUser(userId,
                dto
        );

        // assert
        assertThat(result.email()).isEqualTo(expectedUser.getEmail());
        assertThat(result.id()).isEqualTo(expectedUser.getId());

        // v
        verify(userRepository).findById(userId);
        verify(userRepository).save((User) any(User.class));
    }



}