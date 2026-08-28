
package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.User;
import com.inventorymanagement.inventory.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create user
    public User createUser(User user) {

        if (user.getUsername() == null ||
                user.getUsername().trim().isEmpty()) {

            throw new RuntimeException("Username is required");
        }

        if (user.getPassword() == null ||
                user.getPassword().trim().isEmpty()) {

            throw new RuntimeException("Password is required");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        user.setUsername(user.getUsername().trim());

        // Hash password before saving
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        user.setRole("ADMINISTRATOR");

        return userRepository.save(user);
    }

    // Find user by username
    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // Change password
    public void changePassword(
            String username,
            String oldPassword,
            String newPassword) {

        User user = getUserByUsername(username);

        // Check old password
        if (!passwordEncoder.matches(
                oldPassword,
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }

        // Validate new password
        if (newPassword == null ||
                newPassword.trim().isEmpty()) {

            throw new RuntimeException(
                    "New password is required"
            );
        }

        if (newPassword.length() < 6) {

            throw new RuntimeException(
                    "New password must be at least 6 characters"
            );
        }

        // Prevent same password
        if (passwordEncoder.matches(
                newPassword,
                user.getPassword())) {

            throw new RuntimeException(
                    "New password must be different from current password"
            );
        }

        // Save new hashed password
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }
    
public boolean checkPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
}


}

