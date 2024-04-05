package com.example.authentication.Model;


import jakarta.persistence.*;
import lombok.*;


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
    private boolean isVerified = false;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.User;

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

}
