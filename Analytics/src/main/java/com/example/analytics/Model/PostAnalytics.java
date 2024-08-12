package com.example.analytics.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PostAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;
    private int viewCount;
    private int likeCount;
    private LocalDateTime lastViewedTime;
    private int commentsCount;
    private int upvotesCount;
    private int downvotesCount;



}
