package com.example.questionanwser.Repository;


import com.example.questionanwser.Model.Role;
import com.example.questionanwser.Model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UserCredentialRepository extends JpaRepository<UserCredentials, Integer> {






    @Query("SELECT COUNT(u) FROM UserCredentials u WHERE u.createdDate = :date")
    long countByCreatedDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(u) FROM UserCredentials u WHERE u.createdDate BETWEEN :startDate AND :endDate")
    long countByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(u) FROM UserCredentials u")
    long countTotalUsers();


    @Query("SELECT COUNT(u) FROM UserCredentials u WHERE u.lastLoginDate BETWEEN :startDate AND :endDate")
    long countActiveUsersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    Optional<UserCredentials> findByEmail(String email);

    Optional<UserCredentials> findByVerificationCode(String verificationCode);

    List<UserCredentials> findByRole(Role role);

    Optional<UserCredentials> findByUsername(String username);


    List<UserCredentials> findByUsernameContainingIgnoreCaseOrderByUsername(String username);

}

