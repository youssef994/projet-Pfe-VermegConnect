package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.UserProfile;
import com.example.questionanwser.Service.UserProfileService;
import dto.UpdateUserProfileDTO;
import dto.UserStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // Define your upload directory for profile pictures
    private static final String UPLOAD_DIR = "src/main/resources/static/assets/";

    @PutMapping(value = "/{userId}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfile> updateUserProfile(
            @PathVariable int userId,
            @RequestPart("profileData") UpdateUserProfileDTO updateUserProfileDTO,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String profilePictureUrl = saveFile(profilePicture);
            updateUserProfileDTO.setProfilePictureUrl(profilePictureUrl);
        }

        UserProfile updatedUserProfile = userProfileService.updateUserProfile(userId, updateUserProfileDTO);
        return ResponseEntity.ok(updatedUserProfile);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable int userId) {
        UserProfile userProfile = userProfileService.getUserProfileById(userId);
        return ResponseEntity.ok(userProfile);
    }

    private String saveFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Construct the full path where the file will be saved
            Path filePath = Paths.get(UPLOAD_DIR + newFilename);

            // Create the directories if they don't exist
            Files.createDirectories(filePath.getParent());

            // Save the file to the specified path
            Files.write(filePath, file.getBytes());

            // Return the URL relative to the static directory
            return "/assets/" + newFilename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }




    @GetMapping("/all/{userId}")
    public UserStatistics getAllStatisticsByUserId(@PathVariable int userId, @RequestParam String username) {
        Long upvotes = userProfileService.getTotalUpvotesByUser(username);
        Long downvotes = userProfileService.getTotalDownvotesByUser(username);
        Long followedPosts = userProfileService.countFollowedPostsByUsername(username);
        Long posts = userProfileService.countPostsByUserId(userId);
        Long answers = userProfileService.countAnswersByUserId(userId);

        return new UserStatistics(upvotes, downvotes,followedPosts, posts, answers);
    }

    public static class UserStatistics {
        public Long upvotes;
        public Long downvotes;
        public Long followedPosts;
        public Long posts;
        public Long answers;

        public UserStatistics(Long upvotes, Long downvotes, Long followedPosts, Long posts, Long answers) {
            this.upvotes = upvotes;
            this.downvotes = downvotes;
            this.followedPosts = followedPosts;
            this.posts = posts;
            this.answers = answers;
        }
    }

    @GetMapping("/profile-picture/{username}")
    public ResponseEntity<String> getProfilePictureByUsername(@PathVariable String username) {
        String profilePictureUrl = userProfileService.getProfilePictureByUsername(username);
        if (profilePictureUrl != null) {
            return ResponseEntity.ok(profilePictureUrl);
        } else {
            return ResponseEntity.notFound().build(); // or return a default picture URL
        }
    }
    @GetMapping("/id/{username}")
    public ResponseEntity<Integer> getUserIdByUsername(@PathVariable String username) {
        Integer userId = userProfileService.getUserIdByUsername(username);
        if (userId != null) {
            return ResponseEntity.ok(userId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
