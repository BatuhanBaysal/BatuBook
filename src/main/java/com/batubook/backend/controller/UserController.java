package com.batubook.backend.controller;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.service.serviceInterface.UserServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceInterface userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Creating user: {}", userDTO.getUsername());
        UserDTO createdUser = userService.registerUser(userDTO);
        logger.info("User created successfully: {}", userDTO.getUsername());
        return ResponseEntity.status(201).body(createdUser);
    }

    @GetMapping("/userId/{id}")
    public ResponseEntity<UserDTO> fetchUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);
        UserDTO userDTO = userService.getUserById(id);
        logger.info("Successfully retrieved user with ID: {}", id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<Page<UserDTO>> fetchAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Fetching all users");
        Page<UserDTO> users = userService.getAllUsers(pageable);
        logger.info("Successfully fetched all users");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search-username-email")
    public ResponseEntity<Page<UserDTO>> fetchUsersByUsernameAndEmail(
            @RequestParam String username,
            @RequestParam String email,
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Fetching users for username: {} and email: {}", username, email);
        Page<UserDTO> users = userService.getUsersByUsernameAndEmail(username, email, pageable);
        logger.info("Successfully fetched users for username: {} and email: {}", username, email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search-role")
    public ResponseEntity<Page<UserDTO>> fetchUsersByRole(
            @RequestParam String role,
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Fetching users with role: {}", role);
        Role roleEnum = Role.fromString(role);
        Page<UserDTO> users = userService.getUsersByRole(roleEnum, pageable);
        logger.info("Successfully fetched {} users with role: {}", users.getSize(), roleEnum);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search-Term")
    public ResponseEntity<List<UserDTO>> fetchSearchUser(@RequestParam String searchTerm) {
        logger.info("Searching users with term: {}", searchTerm);
        List<UserDTO> users = userService.searchUser(searchTerm);
        logger.info("Successfully found {} users for search term: {}", users.size(), searchTerm);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);
        UserDTO updatedUser = userService.modifyUser(id, userDTO);
        logger.info("Successfully updated user with ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        userService.removeUserById(id);
        logger.info("Successfully deleted user with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}