package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.enums.Role;

import java.util.List;

public interface UserServiceInterface {

    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    List<UserDTO> getUsersByRole(Role role);
    List<UserDTO> getUsersByUsernameAndEmail(String username, String email);
    List<UserDTO> searchUser(String searchTerm);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUserById(Long id);
}