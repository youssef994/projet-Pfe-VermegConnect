package com.example.questionanwser.Repository;

import com.example.questionanwser.Model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    UserProfile findByUserCredentialsId(int userCredentialsId);
}
