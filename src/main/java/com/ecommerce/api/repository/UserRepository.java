package com.ecommerce.api.repository;

import com.ecommerce.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.username = :#{#updatedUser.email}, u.password = :#{#updatedUser.password} WHERE u.id = :#{#updatedUser.id}")
    int updateUser(User updatedUser);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    int deleteUser(long id);
}
