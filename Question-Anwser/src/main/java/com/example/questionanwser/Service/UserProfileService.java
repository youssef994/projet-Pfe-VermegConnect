package com.example.questionanwser.Service;

import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Model.UserProfile;
import com.example.questionanwser.Repository.AnswerRepository;
import com.example.questionanwser.Repository.PostRepository;
import com.example.questionanwser.Repository.UserCredentialRepository;
import com.example.questionanwser.Repository.UserProfileRepository;
import dto.UpdateUserProfileDTO;
import dto.UserStatisticsDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {
    @Autowired
    private UserCredentialRepository userCredentialsRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AnswerRepository answerRepository;


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

    public Long getTotalUpvotesByUser(String username) {
        Long postUpvotes = postRepository.sumUpvotesForPostsByUser(username);
        Long answerUpvotes = answerRepository.sumUpvotesForAnswersByUser(username);
        return (postUpvotes != null ? postUpvotes : 0) + (answerUpvotes != null ? answerUpvotes : 0);
    }

    public Long getTotalDownvotesByUser(String username) {
        Long postDownvotes = postRepository.sumDownvotesForPostsByUser(username);
        Long answerDownvotes = answerRepository.sumDownvotesForAnswersByUser(username);
        return (postDownvotes != null ? postDownvotes : 0) + (answerDownvotes != null ? answerDownvotes : 0);
    }

    public Long countFollowedPostsByUsername(String username) {
        return postRepository.countFollowedPostsByUser(username);
    }


    public Long countPostsByUserId(int userId) {
        return postRepository.countPostsByUserId(userId);
    }

    public Long countAnswersByUserId(int userId) {
        return answerRepository.countAnswersByUserId(userId);
    }

    public String getProfilePictureByUsername(String username) {
        UserCredentials userCredentials = userCredentialsRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile userProfile = userCredentials.getUserProfile();
        if (userProfile != null) {
            return userProfile.getProfilePictureUrl();
        } else {
            throw new RuntimeException("User profile not found");
        }
    }
    public Integer getUserIdByUsername(String username) {
        Optional<UserCredentials> userOptional = userCredentialsRepository.findByUsername(username);
        return userOptional.map(UserCredentials::getId).orElse(null);
    }

}

