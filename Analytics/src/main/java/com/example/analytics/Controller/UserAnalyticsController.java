package com.example.analytics.Controller;

import com.example.analytics.Model.UserAnalytics;
import com.example.analytics.Service.UserAnalyticsService;
import com.example.analytics.Repository.UserAnalyticsRepository;
import com.example.analytics.dto.UserAnalyticsSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/analytics")
public class UserAnalyticsController {

    @Autowired
    private UserAnalyticsService userAnalyticsService;

    @Autowired
    private UserAnalyticsRepository userAnalyticsRepository;

    @GetMapping("/user/{userId}")
    public UserAnalytics getUserAnalytics(@PathVariable Long userId) {
        Optional<UserAnalytics> userAnalytics = userAnalyticsRepository.findByUserId(userId);
        return userAnalytics.orElseGet(UserAnalytics::new); // Return an empty UserAnalytics if not found
    }

    @GetMapping("/all")
    public List<UserAnalytics> getAllUserAnalytics() {
        return userAnalyticsRepository.findAll();
    }

    @GetMapping("/user-summary")
    public UserAnalyticsSummary getUserAnalyticsSummary() {
        return userAnalyticsService.getUserAnalyticsSummary();
    }
}