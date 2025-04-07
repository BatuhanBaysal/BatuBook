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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceInterface userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Creating user: {}", userDTO.getUsername());
        UserDTO createdUser = userService.registerUser(userDTO);
        logger.info("User created successfully: {}", userDTO.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> fetchUserById(@PathVariable Long id) {
        logger.info("Received GET request for /api/users/{}", id);
        UserDTO userDTO = userService.getUserById(id);
        logger.info("Successfully retrieved user with ID: {}", id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> fetchAllUsers(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/users called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserDTO> allUsers = userService.getAllUsers(pageable);
        logger.info("Successfully fetched {} users", allUsers.getNumberOfElements());
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/search-username-email")
    public ResponseEntity<Page<UserDTO>> fetchUsersByUsernameAndEmail(
            @RequestParam String username,
            @RequestParam String email,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to fetch users for username: {} and email: {} with pagination: page {}, size {}",
                username, email, pageable.getPageNumber(), pageable.getPageSize());
        Page<UserDTO> users = userService.getUsersByUsernameAndEmail(username, email, pageable);
        logger.info("Successfully fetched {} users for username: {} and email: {}",
                users.getTotalElements(), username, email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search-role")
    public ResponseEntity<Page<UserDTO>> fetchUsersByRole(
            @RequestParam String role,
            @PageableDefault(size = 5) Pageable pageable) {
        Role roleEnum = Role.fromString(role);
        logger.info("Received request to fetch users with role: {} and pagination: page {}, size {}",
                roleEnum, pageable.getPageNumber(), pageable.getPageSize());
        Page<UserDTO> users = userService.getUsersByRole(roleEnum, pageable);
        logger.info("Successfully fetched {} users with role: {} on page {}",
                users.getTotalElements(), roleEnum, pageable.getPageNumber());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search-user")
    public ResponseEntity<Page<UserDTO>> fetchUsersBySearchTerm(
            @RequestParam String searchTerm,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to fetch users with search term: {}", searchTerm);
        Page<UserDTO> users = userService.getUserByCriteria(searchTerm, pageable);
        logger.info("Successfully fetched {} users for search term: {}", users.getSize(), searchTerm);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);
        UserDTO updatedUser = userService.modifyUser(id, userDTO);
        logger.info("Successfully updated user with ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Received request to delete user with ID: {}", id);
        userService.removeUser(id);
        logger.info("Successfully deleted user with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}