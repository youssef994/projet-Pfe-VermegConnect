package com.example.authentication.Service;

import com.example.authentication.Model.Role;
import com.example.authentication.Model.UserCredentials;
import com.example.authentication.Repository.UserCredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public String generateToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return jwtService.generateToken(userDetails);
    }

    public List<UserCredentials> getAllUsersByRole(Role role) {
        return userCredentialRepository.findByRole(role);
    }

    public void validate(String token) {
        jwtService.validateToken(token);
    }
    public void deleteUser(int id) {
        userCredentialRepository.deleteById(id);
    }
    public Optional<UserCredentials> getUserByUsername(String username) {
        return userCredentialRepository.findByUsername(username);
    }

    public Optional<UserCredentials> getUserById(Integer id) {
        return userCredentialRepository.findById(id);
    }
}
