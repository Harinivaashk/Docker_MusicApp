package com.example.music_system.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "genre")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int genreId;
    @Column(name = "genre_name")
    private String genreName;

    @OneToMany(mappedBy = "genre" ,cascade = CascadeType.ALL)
    List<Track> tracks;

    public Genre() {

    }

    public Genre(int genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public List<Track> getTrack() {
        return tracks;
    }

    public void setTrack(List<Track> track) {
        this.tracks = track;
    }
}
