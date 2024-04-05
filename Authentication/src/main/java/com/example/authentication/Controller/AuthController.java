package com.example.authentication.Controller;

import com.example.authentication.Model.Role;
import com.example.authentication.Model.UserCredentials;
import com.example.authentication.Service.AuthenticationService;
import dto.AuthRequest;
import dto.TokenValidationRequest;
import dto.UserCredentialResponse;
import dto.UserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserCredentials> registerUser(@RequestBody UserCredentials user) {
        try {
            UserCredentials registeredUser = authService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String code) {
        boolean isVerified = authService.verifyEmail(code);
        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
        }
    }

    @PostMapping("/token")
    public ResponseEntity<UserCredentialResponse> getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authenticate.isAuthenticated()) {
            String token = authService.generateToken(authRequest.getUsername());
            Optional<UserCredentials> userOptional = authService.getUserByUsername(authRequest.getUsername());

            if (!userOptional.isPresent()) {
                throw new RuntimeException("User not found.");
            }

            UserCredentialResponse response = new UserCredentialResponse();
            response.setToken(token);
            UserCredentials safeUser = new UserCredentials();
            BeanUtils.copyProperties(userOptional.get(), safeUser);
            safeUser.setPassword(null);
            response.setUser(safeUser);

            return ResponseEntity.ok(response);
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping("/byRole")
    public List<UserCredentials> getAllUsersByRole(@RequestParam Role role) {
        return authService.getAllUsersByRole(role);
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestBody TokenValidationRequest request) {
        try {
            authService.validate(request.getToken());
            return new ResponseEntity<>("Token is valid", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Token is invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            authService.deleteUser(id);
            return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to delete user!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        Optional<UserCredentials> userOptional = authService.getUserById(id);

        if (userOptional.isPresent()) {
            UserCredentials user = userOptional.get();
            UserResponse userResponce = new UserResponse();
            userResponce.setId(user.getId());
            userResponce.setUsername(user.getUsername());
            userResponce.setEmail(user.getEmail());

            return new ResponseEntity<>(userResponce, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}