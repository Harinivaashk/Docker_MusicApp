package com.example.music_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;



@Entity
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id", nullable = false)
    private int trackId;
    @Column(name = "track_name", nullable = false)
    private String trackName;
    @Column(name = "track_length", nullable = false)
    private int trackLength;
    @Column(name = "track_number", nullable = false)
    private int trackNumber;

    @ManyToOne
    @JoinColumn(name = "album_id" , nullable = false)
    @JsonBackReference(value = "album-tracks")
    private Album album;

    @ManyToOne
    @JoinColumn(name = "genre_id" , nullable = false)
    @JsonBackReference  
    private Genre genre;

    public Track(String trackName,  int trackLength, int trackNumber,Album album, Genre genre ) {
        this.trackName = trackName;
        this.trackLength = trackLength;
        this.trackNumber = trackNumber;
        this.album=album;
        this.genre=genre;
    }

    public Track() {
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }


    public int getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(int trackLength) {
        this.trackLength = trackLength;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

}
