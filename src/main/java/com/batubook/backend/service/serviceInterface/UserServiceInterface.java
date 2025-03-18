package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserServiceInterface {

    UserDTO registerUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    Page<UserDTO> getAllUsers(Pageable pageable);
    Page<UserDTO> getUsersByRole(Role role, Pageable pageable);
    Page<UserDTO> getUsersByUsernameAndEmail(String username, String email, Pageable pageable);
    List<UserDTO> searchUser(String searchTerm);
    UserDTO modifyUser(Long id, UserDTO userDTO);
    void removeUserById(Long id);
}