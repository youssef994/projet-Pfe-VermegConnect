package com.example.questionanwser.Repository;

import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    UserProfile findByUserCredentialsId(int userCredentialsId);


}
