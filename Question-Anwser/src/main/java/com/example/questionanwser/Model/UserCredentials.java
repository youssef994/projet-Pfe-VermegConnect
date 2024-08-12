package com.example.questionanwser.Model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "user_credentials")
public class UserCredentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String email;
    private String password;
    private String verificationCode;
    @Column(name = "registration_date")
    private LocalDate createdDate;
    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    private int loginCount;
    private boolean isVerified = false;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.User;

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-posts")
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-answers")
    private List<Answer> answers;

    @OneToOne(mappedBy = "userCredentials", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JsonManagedReference(value = "userprofile-usercredentials")
    private UserProfile userProfile;

}
