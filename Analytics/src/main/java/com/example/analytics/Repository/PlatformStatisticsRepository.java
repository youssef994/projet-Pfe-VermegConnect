package com.example.analytics.Repository;

import com.example.analytics.Model.PlatformStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlatformStatisticsRepository extends JpaRepository<PlatformStatistics, Long> {
    PlatformStatistics findTopByOrderByIdDesc();

    List<PlatformStatistics> findAllByOrderByCreatedDateAsc();


    @Query("SELECT COUNT(u) FROM UserCredentials u WHERE u.createdDate BETWEEN :startDate AND :endDate")
    long countNewUsersThisMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);




    @Query("SELECT COUNT(u) FROM UserCredentials u WHERE u.createdDate = :date")
    long countNewUsersToday(@Param("date") LocalDate date);


    @Query("SELECT COUNT(u) FROM UserCredentials u WHERE u.lastLoginDate BETWEEN :startDate AND :endDate")
    long countActiveUsersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    void deleteByCreatedDateBetween(LocalDateTime start, LocalDateTime end);


}