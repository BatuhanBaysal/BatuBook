package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserServiceInterface {

    UserDTO registerUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    Page<UserDTO> getAllUsers(Pageable pageable);
    Page<UserDTO> getUsersByRole(Role role, Pageable pageable);
    Page<UserDTO> getUsersByUsernameAndEmail(String username, String email, Pageable pageable);
    Page<UserDTO> getUserByCriteria(String searchTerm, Pageable pageable);
    UserDTO modifyUser(Long id, UserDTO userDTO);
    void removeUser(Long id);
}