package com.example.music_system.repository;

import com.example.music_system.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track,Integer> {
    Track findByAlbum_AlbumIdAndTrackNumber(int albumId, int trackNumber);
    List<Track> findByAlbum_AlbumName(String albumName);
    List<Track> findByGenre_GenreName(String genreName);
    List<Track> findAllByOrderByTrackLengthAsc();
}
