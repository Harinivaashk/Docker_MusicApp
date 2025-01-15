package com.example.music_system.service;

import com.example.music_system.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.builder;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String getUserEmail(String token){
        return extractClaim(token,Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] byteToken= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(byteToken);
    }

    public String generateToken(User user) {
       return builder()
               .setSubject(user.getUserEmail())
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis()+1000*60*2))
               .signWith(getSignInKey(),SignatureAlgorithm.HS256)
               .compact();
    }

    public boolean isTokenValid(String token, User existingUser) {
        String userEmail=getUserEmail(token);
        return (userEmail.equals(existingUser.getUserEmail())  && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }
}
