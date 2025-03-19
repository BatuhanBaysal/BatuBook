package com.batubook.backend.repository;

import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    Page<UserProfileEntity> findByDateOfBirth(LocalDate dateOfBirth, Pageable pageable);
    Page<UserProfileEntity> findByGender(Gender gender, Pageable pageable);
}