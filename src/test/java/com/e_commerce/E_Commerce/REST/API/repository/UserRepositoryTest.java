package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@DataJpaTest
@DisplayName("User Repository Test")
class UserRepositoryTest {

    private final TestEntityManager entityManager;
    private final UserRepository repository;

    @Nested
    @DisplayName("Find By Email Tests")
    class FindByEmail {
        @Test
        @DisplayName("Should return user when email exists")
        void findByEmail_whenEmailExists_shouldReturnUser() {
            // arrange
            String email = "333alaamo@gmail.com";
            User user = User.builder()
                    .email(email)
                    .build();

            User persisted = entityManager.persistAndFlush(user);
            // act
            Optional<User> result = repository.findByEmail(email);

            // assert
            assertThat(result).hasValueSatisfying(u -> assertThat(u.getEmail())
                    .isEqualTo(email));
        }

        @Test
        @DisplayName("Should return empty when email does not exist")
        void findByEmail_whenEmailDoesNotExist_shouldReturnEmpty() {
            String email = "333alaamo@gmail.com";
            // act
            Optional<User> result = repository.findByEmail(email);

            // assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Exists By Email Tests")
    class ExistsByEmail {
        @Test
        @DisplayName("Should return true when email exists")
        void existsByEmail_whenEmailExist_shouldReturnTrue() {
            // arrange
            String email = "333alaamo@gmail.com";
            User user = User.builder()
                    .email(email)
                    .build();

            entityManager.persistAndFlush(user);

            // act
            boolean isExist = repository.existsByEmail(email);

            assertThat(isExist).isTrue();
        }

        @Test
        @DisplayName("Should return false when email does not exist")
        void existsByEmail_whenEmailDoesNotExist_shouldReturnFalse() {
            // arrange
            String email = "333alaamo@gmail.com";

            // act
            boolean isExist = repository.existsByEmail(email);

            assertThat(isExist).isFalse();
        }
    }
}