package com.example.music_system.repository;

import com.example.music_system.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre,Integer> {
    Genre findByGenreName(String genreName);
}
