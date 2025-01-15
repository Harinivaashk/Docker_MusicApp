package com.example.music_system.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id", nullable = false,unique = true)
    private int userId;
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "expiry_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // Indicates a date-time column
    private Date expiryTime;


    public Token(int userId, String token, Date expiryTime) {
        this.userId = userId;
        this.token = token;
        this.expiryTime = expiryTime;
    }

    public Token() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
