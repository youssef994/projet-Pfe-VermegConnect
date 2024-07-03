package com.example.questionanwser.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fullName;
    private String jobTitle;
    private String bio;
    private LocalDate birthDate;
    private String phoneNumber;
    private String profilePictureUrl;

    @OneToOne
    @JoinColumn(name = "user_credentials_id")
    @JsonBackReference(value = "userprofile-usercredentials")
    private UserCredentials userCredentials;
}
