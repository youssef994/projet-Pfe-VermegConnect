package com.example.analytics.Model;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username; // New field
    private int loginCount;
    private LocalDateTime lastLoginTime;
    private int totalPosts;
    private int totalAnswers;
    private int totalUpvotes;
    private int totalDownvotes;
    private int followersCount;
    @Column(name = "registration_date")
    private LocalDate createdDate;


}
