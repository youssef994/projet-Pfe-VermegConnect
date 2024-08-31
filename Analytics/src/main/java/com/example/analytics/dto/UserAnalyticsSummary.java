package com.example.analytics.dto;


import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAnalyticsSummary {
    private int totalUsers;
    private int totalPosts;
    private int totalAnswers;
    private int totalUpvotes;
    private int totalDownvotes;
    private int totalFollowers;

}
