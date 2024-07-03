package com.example.questionanwser.Repository;


import com.example.questionanwser.Model.Role;
import com.example.questionanwser.Model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserCredentialRepository extends JpaRepository<UserCredentials, Integer> {


    Optional<UserCredentials> findByEmail(String email);

    Optional<UserCredentials> findByVerificationCode(String verificationCode);

    List<UserCredentials> findByRole(Role role);

    Optional<UserCredentials> findByUsername(String username);


}

