package com.example.questionanwser.Service;

import com.example.questionanwser.Model.Role;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Repository.UserCredentialRepository;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    UserCredentialRepository userCredentialRepository;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public UserCredentials registerUser(UserCredentials user) {
        try {
            Optional<UserCredentials> userByUsername = userCredentialRepository.findByUsername(user.getUsername());
            Optional<UserCredentials> userByEmail = userCredentialRepository.findByEmail(user.getEmail());

            if (userByUsername.isPresent()) {
                throw new RuntimeException("Username taken.");
            }

            if (userByEmail.isPresent()) {
                throw new RuntimeException("Email taken.");
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Generate verif code
            user.setVerificationCode(generateNumericCode(6));

            // Set created date
            user.setCreatedDate(LocalDate.now());

            // Save
            UserCredentials savedUser = userCredentialRepository.save(user);

            // Send verif email
            sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationCode());

            logger.info("User registered successfully: {}", savedUser.getUsername());
            return savedUser;
        } catch (Exception ex) {
            logger.error("Error occurred during user registration: {}", ex.getMessage());
            throw new RuntimeException("User registration failed", ex);
        }
    }
    @Async
    public void sendVerificationEmail(String email, String code) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("Email Verification");
            mailMessage.setText("Your verification code is: " + code);

            logger.info("Sending verification email to {}", email);

            javaMailSender.send(mailMessage);

            logger.info("Verification email sent successfully to {}", email);
        } catch (Exception ex) {

            logger.error("Failed to send verification email to {}: {}", email, ex.getMessage());
        }
    }

    public boolean verifyEmail(String code) {
        Optional<UserCredentials> user = userCredentialRepository.findByVerificationCode(code);
        if (user.isPresent()) {
            UserCredentials verifiedUser = user.get();
            verifiedUser.setIsVerified(true);
            verifiedUser.setVerificationCode(null);
            userCredentialRepository.save(verifiedUser);
            return true;
        }
        return false;
    }

    private String generateNumericCode(int length) {
        int number = (int) (Math.random() * Math.pow(10, length));
        return String.format("%0" + length + "d", number);
    }

    // AuthenticationService.java

    public String generateToken(String username) {
        Optional<UserCredentials> userOptional = getUserByUsername(username);
        if (userOptional.isPresent()) {
            UserCredentials user = userOptional.get();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtService.generateToken(userDetails, user.getId());
        } else {
            throw new RuntimeException("User not found: " + username);
        }
    }


    public List<UserCredentials> getAllUsersByRole(Role role) {
        return userCredentialRepository.findByRole(role);
    }

    public void validate(String token) {
        jwtService.validateToken(token);
    }


    public Optional<UserCredentials> getUserById(Integer id) {
        return userCredentialRepository.findById(id);
    }

    public Optional<UserCredentials> getUserByUsername(String username) {
        return userCredentialRepository.findByUsername(username);
    }

    public Optional<UserCredentials> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userCredentialRepository.findByUsername(username);
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();

        Optional<UserCredentials> userOptional = userCredentialRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            UserCredentials user = userOptional.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                logger.info("Old password matched for user: " + username);
                user.setPassword(passwordEncoder.encode(newPassword));
                userCredentialRepository.save(user);
                logger.info("Password changed successfully for user: " + username);
                return true;
            } else {
                logger.error("Old password is incorrect for user: " + username);
                throw new RuntimeException("Old password is incorrect");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<UserCredentials> getAllUsers() {
        return userCredentialRepository.findAll();
    }

    public List<UserCredentials> getUsersByRole(Role role) {
        return userCredentialRepository.findByRole(role);
    }

    public Optional<UserCredentials> getUserById(int id) {
        return userCredentialRepository.findById(id);
    }

    public UserCredentials createUser(UserCredentials user) {
        return userCredentialRepository.save(user);
    }

    public UserCredentials updateUser(int id, UserCredentials userDetails) {
        Optional<UserCredentials> userOptional = userCredentialRepository.findById(id);
        if (userOptional.isPresent()) {
            UserCredentials user = userOptional.get();
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            user.setIsVerified(userDetails.isVerified());
            user.setLastLoginDate(userDetails.getLastLoginDate());
            return userCredentialRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void deleteUser(int id) {
        userCredentialRepository.deleteById(id);
    }

    public List<UserCredentials> findByUsernameContainingIgnoreCase(String username) {
        return userCredentialRepository.findByUsernameContainingIgnoreCaseOrderByUsername(username);
    }

    public long countNewUsersToday() {
        LocalDate today = LocalDate.now();
        return userCredentialRepository.countByCreatedDate(today);
    }



    public long countNewUsersThisMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        return userCredentialRepository.countByDateRange(startOfMonth, endOfMonth);
    }

    public long countActiveUsers() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userCredentialRepository.countActiveUsersBetween(thirtyDaysAgo, LocalDateTime.now());
    }



    public long countTotalUsers() {
        return userCredentialRepository.countTotalUsers();
    }
}
