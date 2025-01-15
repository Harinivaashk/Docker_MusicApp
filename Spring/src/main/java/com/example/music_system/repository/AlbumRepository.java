package com.example.music_system.repository;

import com.example.music_system.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    List<Album> findByArtist_ArtistName(String artistName);
}
