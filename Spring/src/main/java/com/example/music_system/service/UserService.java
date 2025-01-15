package com.example.music_system.service;

import com.example.music_system.model.User;
import com.example.music_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User loadByUserEmail(String userEmail){
        return userRepository.findByUserEmail(userEmail);
    }
}
