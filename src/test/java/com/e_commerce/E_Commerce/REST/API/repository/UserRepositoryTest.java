package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@DataJpaTest
class UserRepositoryTest {

    private final TestEntityManager entityManager;
    private final UserRepository repository;

    // ========== findByEmail testing =============
    @Test
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
        assertThat(result).hasValueSatisfying( u ->
                assertThat(u.getEmail())
                        .isEqualTo(email)
        );

        Mockito.verify(repository).save((User)any(User.class));
    }

    @Test
    void findByEmail_whenEmailDoesNotExist_shouldReturnEmpty()
    {
        String email = "333alaamo@gmail.com";
        // act
        Optional<User>  result= repository.findByEmail(email);

        // assert
        assertThat(result).isEmpty();
    }



    // ========== existsByEmail testing =============

    @Test
    void existsByEmail_whenEmailExist_shouldReturnTrue() {
        // arrange -> data needed
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
    void existsByEmail_whenEmailDoesNotExist_shouldReturnFalse() {
        // arrange -> data needed
        String email = "333alaamo@gmail.com";

        // act
        boolean isExist = repository.existsByEmail(email);

        assertThat(isExist).isFalse();
    }
}