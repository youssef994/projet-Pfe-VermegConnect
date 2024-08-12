package com.example.analytics.Service;




import com.example.analytics.Model.PostAnalytics;
import com.example.analytics.Repository.PostAnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostAnalyticsService {

    @Autowired
    private PostAnalyticsRepository postAnalyticsRepository;

    public List<PostAnalytics> getAllPostAnalytics() {
        return postAnalyticsRepository.findAll();
    }

    public PostAnalytics getPostAnalyticsById(Long id) {
        return postAnalyticsRepository.findById(id).orElse(null);
    }

    public PostAnalytics savePostAnalytics(PostAnalytics postAnalytics) {
        return postAnalyticsRepository.save(postAnalytics);
    }
}
