package com.example.questionanwser.config;

import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserCredentialRepository userCredentialRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserCredentials> userCredential = userCredentialRepository.findByUsername(username);
        return userCredential.map(CustomUserDetails::new).orElseThrow(()-> new UsernameNotFoundException("user not found with name :" + username));
    }
}
