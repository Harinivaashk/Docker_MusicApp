package com.example.music_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.music_system.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
    User findByUserEmail(String userEmail);

    User findByUserEmailAndUserPassword(String userEmail, String userPassword);
}
