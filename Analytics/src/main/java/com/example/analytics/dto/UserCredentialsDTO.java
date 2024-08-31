package com.example.analytics.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDTO {
    private Long id;
    private String username;
    private LocalDateTime lastLoginDate;
    private LocalDate createdDate;
    private int loginCount;


}