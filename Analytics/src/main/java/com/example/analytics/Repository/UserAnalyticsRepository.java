package com.example.analytics.Repository;



import com.example.analytics.Model.UserAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserAnalyticsRepository extends JpaRepository<UserAnalytics, Long> {

    Optional<UserAnalytics> findByUserId(Long userId);

}
