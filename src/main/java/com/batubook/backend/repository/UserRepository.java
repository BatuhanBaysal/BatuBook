package com.batubook.backend.repository;

import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findByUsernameAndEmailIgnoreCase(String username, String email);
    List<UserEntity> findByRole(Role role);
}