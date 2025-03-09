package com.batubook.backend.controller;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.service.serviceInterface.UserServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserServiceInterface userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            logger.info("Creating user: {}", userDTO.getUsername());
            UserDTO createUserDTO = userService.createUser(userDTO);
            logger.info("User created successfully: {}", userDTO.getUsername());
            return ResponseEntity.ok(createUserDTO);
        } catch (Exception e) {
            logger.error("Error occurred while creating user: {}", userDTO.getUsername(), e);
            throw e;
        }
    }

    @GetMapping("/userId/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        try {
            logger.info("Fetching user with ID: {}", id);
            UserDTO userDTO = userService.getUserById(id);
            logger.info("Successfully retrieved user with ID: {}", id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.error("Error occurred while fetching user with ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            logger.info("Fetching all users");
            List<UserDTO> users = userService.getAllUsers();
            logger.info("Successfully fetched {} users.", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/userRole/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        try {
            logger.info("Fetching users with role: {}", role);
            List<UserDTO> users = userService.getUsersByRole(Role.valueOf(role.toUpperCase()));
            logger.info("Successfully fetched {} users with role {}", users.size(), role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error occurred while fetching users by role {}: {}", role, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUser(@RequestParam String searchTerm) {
        try {
            logger.info("Searching users with term: {}", searchTerm);
            List<UserDTO> users = userService.searchUser(searchTerm);
            logger.info("Successfully found {} users for search term: {}", users.size(), searchTerm);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error occurred while searching users with term {}: {}", searchTerm, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/search-username-email")
    public ResponseEntity<List<UserDTO>> getUsersByUsernameAndEmail(@RequestParam String username, @RequestParam String email) {
        try {
            logger.info("Fetching users for username: {} and email: {}", username, email);
            List<UserDTO> users = userService.getUsersByUsernameAndEmail(username, email);
            logger.info("Successfully fetched {} users for username: {} and email: {}", users.size(), username, email);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error occurred while fetching users for username: {} and email: {}: {}", username, email, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            logger.info("Updating user with ID: {}", id);
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            logger.info("Successfully updated user with ID: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error occurred while updating user with ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            logger.info("Deleting user with ID: {}", id);
            userService.deleteUserById(id);
            logger.info("Successfully deleted user with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error occurred while deleting user with ID: {}", id, e);
            throw e;
        }
    }
}