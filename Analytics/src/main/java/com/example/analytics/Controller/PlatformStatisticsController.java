package com.example.analytics.Controller;

import com.example.analytics.Model.PlatformStatistics;
import com.example.analytics.Service.PlatformStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics/platform-statistics")
public class PlatformStatisticsController {

    @Autowired
    private PlatformStatisticsService platformStatisticsService;

    @GetMapping
    public PlatformStatistics getPlatformStatistics() {

        return platformStatisticsService.getLatestStatistics();
    }

    @GetMapping("/all")
    public List<PlatformStatistics> getAllStatistics() {
        return platformStatisticsService.getAllStatistics();
    }
    @GetMapping("/day")
    public List<PlatformStatistics> getAllDayStatistics() {
        return platformStatisticsService.getDayStatistics();
    }

    @GetMapping("/NewUsersday")
    public ResponseEntity<List<PlatformStatistics>> getDayPlatformStatistics(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<PlatformStatistics> stats = platformStatisticsService.getStatisticsByDate(date);
        return ResponseEntity.ok(stats);
    }

}