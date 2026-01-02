package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.UserUpdateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<UserResponse> getById(
           @Positive(message = "id must be positive") Long id
    )
    {
        return ResponseEntity.ok(
                userService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Positive(message="id must be positive ") @PathVariable  Long id ,
            @Valid @RequestBody UserUpdateRequestDto requestDto
     ) {

        return ResponseEntity.ok(
                userService.updateUser(id , requestDto)
        );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable
            @Positive(message = "id must be positive")
            Long id
    )
    {
        userService.deleteUserById(id);
       return ResponseEntity.noContent().build();
    }

}
