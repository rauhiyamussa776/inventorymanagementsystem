
package com.inventorymanagement.inventory.controller;

import com.inventorymanagement.inventory.dto.ChangePasswordRequest;
import com.inventorymanagement.inventory.dto.LoginRequest;
import com.inventorymanagement.inventory.model.User;
import com.inventorymanagement.inventory.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userService.getUserByUsername(
                request.getUsername()
        );

        if (!userService.checkPassword(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid username or password");
        }

        return "Login successful";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestBody ChangePasswordRequest request) {

        userService.changePassword(
                request.getUsername(),
                request.getOldPassword(),
                request.getNewPassword()
        );

        return "Password changed successfully";
    }
}

