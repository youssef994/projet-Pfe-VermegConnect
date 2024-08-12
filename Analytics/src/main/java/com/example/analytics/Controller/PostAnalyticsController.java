package com.example.analytics.Controller;


import com.example.analytics.Model.PostAnalytics;
import com.example.analytics.Service.PostAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics/post-analytics")
public class PostAnalyticsController {

    @Autowired
    private PostAnalyticsService postAnalyticsService;

    @GetMapping
    public List<PostAnalytics> getAllPostAnalytics() {
        return postAnalyticsService.getAllPostAnalytics();
    }

    @GetMapping("/{id}")
    public PostAnalytics getPostAnalyticsById(@PathVariable Long id) {
        return postAnalyticsService.getPostAnalyticsById(id);
    }

    @PostMapping
    public PostAnalytics savePostAnalytics(@RequestBody PostAnalytics postAnalytics) {
        return postAnalyticsService.savePostAnalytics(postAnalytics);
    }
}
