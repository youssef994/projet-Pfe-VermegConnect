package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.UserProfile;
import com.example.questionanwser.Service.UserProfileService;
import dto.UpdateUserProfileDTO;
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
}
