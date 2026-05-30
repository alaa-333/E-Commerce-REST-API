package com.ecommerce.api.controller;


import com.ecommerce.api.dto.request.user.UpdateUserRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.UserResponse;
import com.ecommerce.api.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable
            @Positive(message = "id must be a positive value")
            @NotNull(message = "id must be not null") Long id
    ) {

        var response = userService.getUser(id);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateUserById(
            @PathVariable
            @Positive(message = "id must be a positive value")
            @NotNull(message = "id must be not null") Long id,
            @RequestBody @Valid UpdateUserRequest request
    ) {

        var responseStatus = userService.updateUser(id, request);

        if (!responseStatus) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update user"));
        }
        return ResponseEntity.ok()
                .body(ApiResponse.success("User updated successfully"));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUserById(
            @PathVariable
            @Positive(message = "id must be a positive value")
            @NotNull(message = "id must be not null") Long id,
            @RequestParam String passwordConfirmation
    ) {
        var responseStatus = userService.deleteUser(id, passwordConfirmation);
        if (!responseStatus) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete user"));
        }
        return ResponseEntity.ok()
                .body(ApiResponse.success("User deleted successfully"));
    }

}
