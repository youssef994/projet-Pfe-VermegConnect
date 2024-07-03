package com.example.questionanwser.Service;

import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Model.UserProfile;
import com.example.questionanwser.Repository.UserCredentialRepository;
import com.example.questionanwser.Repository.UserProfileRepository;
import dto.UpdateUserProfileDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    @Autowired
    private UserCredentialRepository userCredentialsRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile updateUserProfile(int userId, UpdateUserProfileDTO updateUserProfileDTO) {
        UserCredentials user = userCredentialsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUserCredentials(user);
        }

        // Copy properties excluding profilePictureUrl if it is null
        if (updateUserProfileDTO.getProfilePictureUrl() != null) {
            userProfile.setProfilePictureUrl(updateUserProfileDTO.getProfilePictureUrl());
        }

        // Copy other properties
        BeanUtils.copyProperties(updateUserProfileDTO, userProfile, "profilePictureUrl");
        user.setUserProfile(userProfile);

        userCredentialsRepository.save(user);
        return userProfileRepository.save(userProfile);
    }

    public UserProfile getUserProfileById(int userId) {
        return userProfileRepository.findByUserCredentialsId(userId);
    }
}
