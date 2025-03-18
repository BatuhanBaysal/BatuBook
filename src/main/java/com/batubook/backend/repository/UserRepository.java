package com.batubook.backend.repository;

import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Page<UserEntity> findByUsernameAndEmailIgnoreCase(String username, String email, Pageable pageable);
    Page<UserEntity> findByRole(Role role, Pageable pageable);
}