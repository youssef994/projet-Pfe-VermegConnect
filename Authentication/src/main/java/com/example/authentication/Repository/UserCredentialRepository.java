package com.example.authentication.Repository;

import com.example.authentication.Model.Role;
import com.example.authentication.Model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface UserCredentialRepository extends JpaRepository<UserCredentials, Integer> {

    Optional<UserCredentials> findByUsername(String username);

    Optional<UserCredentials> findByEmail(String email);
    Optional<UserCredentials> findByVerificationCode(String verificationCode);

    List<UserCredentials> findByRole(Role role);
}

