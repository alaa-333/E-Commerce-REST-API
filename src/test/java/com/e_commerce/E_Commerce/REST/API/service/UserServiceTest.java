package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerCreateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.SignupRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.UserCreateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.UserUpdateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.mapper.UserMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("User Service Test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // =============== mocking dependencies ==================

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("Create User Tests")
    class CreateUser {
        @Test
        @DisplayName("Should create user when email does not exist")
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
            when(userRepository.save(any(User.class))).thenReturn(user);

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
        @DisplayName("Should throw ValidationException when email already exists")
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

            // Verify
            verify(userRepository).existsByEmail(email);
            verify(userRepository, never()).save(any(User.class));
            verifyNoInteractions(userMapper, customerMapper, passwordEncoder);
        }
    }

    @Nested
    @DisplayName("Get User By ID Tests")
    class GetById {
        @Test
        @DisplayName("Should return user response when user exists")
        void getById_whenUserExists_shouldReturnUser() {

            // arrange
            Long userId = 1L;
            String email = "lol@gmail.com";
            User user = mock(User.class);
            // Use real object instead of mock for DTO
            UserResponse response = UserResponse.builder()
                    .id(userId)
                    .email(email)
                    .build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.getEmail()).thenReturn(email);
            // No need to mock user.getId() if we don't call it directly in service logic,
            // or if we do, we need to ensure it matches.
            // But let's assume service calls userMapper.
            when(userMapper.toResponse(user)).thenReturn(response);

            // act
            UserResponse actualResponse = userService.getById(userId);

            // assert
            assertThat(actualResponse).isNotNull();
            assertThat(actualResponse.id()).isEqualTo(userId);
            assertThat(actualResponse.email()).isEqualTo(email);

            // verify
            verify(userRepository).findById(anyLong());
            verify(userMapper).toResponse(any());
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user does not exist")
        void getById_whenUserDoesNotExists_shouldThrowException() {

            Long userId = 99L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // assert + act
            assertThatThrownBy(() -> userService.getById(userId))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("user not found");
        }
    }

    @Nested
    @DisplayName("Delete User By ID Tests")
    class DeleteById {
        @Test
        @DisplayName("Should delete user when user exists")
        void deleteUserById_whenUserExists_shouldBeDeleted() {
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.of(any(User.class)));

            // act
            userService.deleteUserById(userId);
            // verify
            verify(userRepository).deleteById(userId);
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user does not exist")
        void deleteUserById_whenUserDoesNotExist_shouldThrowException() {
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // assert + act
            assertThatThrownBy(() -> userService.deleteUserById(userId))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("user not found");
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUser {
        @Test
        @DisplayName("Should update user when user exists")
        void updateUser_whenUserExists_shouldUpdating() {

            Long userId = 2L;
            String userEmail = "oldEmail@gmail.com";
            String dtoEmail = "newEmail@gmail.com";

            User dbUser = mock(User.class);
            UserUpdateRequestDto dto = mock(UserUpdateRequestDto.class);
            User updatedUser = mock(User.class);

            // Real DTO for response
            UserResponse responseDTO = UserResponse.builder()
                    .email(dtoEmail)
                    .id(userId)
                    .build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(dbUser));
            when(dbUser.getEmail()).thenReturn(userEmail);
            when(dto.getEmail()).thenReturn(dtoEmail); // Logic might differ but assuming logic uses this
            when(userMapper.updateUserFromDto(dbUser, dto)).thenReturn(updatedUser);
            when(userMapper.toResponse(updatedUser)).thenReturn(responseDTO);

            // act
            UserResponse response = userService.updateUser(userId, dto);

            // assert
            assertThat(response.email()).isEqualTo(dtoEmail);

            // verify
            verify(userRepository).findById(userId);
            verify(userMapper).updateUserFromDto(dbUser, dto);
            verify(userMapper).toResponse(updatedUser);
            verify(userRepository).save(updatedUser);
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user does not exist")
        void updateUser_whenUserDoesNotExists_shouldThrowException() {
            Long userId = 99L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(userId, any()))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("user not found");

            verify(userRepository).findById(userId);
        }
    }
}