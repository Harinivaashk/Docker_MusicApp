package com.example.music_system.service;

import com.example.music_system.Exception.CustomException;
import com.example.music_system.model.*;
import com.example.music_system.repository.*;

import jakarta.persistence.EntityExistsException;

import org.hibernate.PropertyValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MusicService {
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JwtService jwtService;

    public MusicService(JwtService jwtService){
        this.jwtService=jwtService;
    }

    Logger logger = LoggerFactory.getLogger(MusicService.class);

    public void addArtist(Artist artist) throws Exception {
        try{
            logger.info("AddArtist method called");
            if (artist.getArtistName() == null || artist.getArtistName().trim().isEmpty()) {
                throw new IllegalArgumentException("Artist name cannot be empty.");
            }
            if (artist.getCountry() == null || artist.getCountry().trim().isEmpty()){
                throw new IllegalArgumentException("Country cannot be empty.");
            }

            artistRepository.save(artist);
        }catch(IllegalArgumentException e) {
            logger.error("Error in adding track " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch(Exception e){
            logger.error("Error while adding artist "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void addAlbum(Album album) throws Exception {
        try{
            logger.info("AddAlbum method called");
            if (album.getAlbumName() == null || album.getAlbumName().trim().isEmpty()) {
                throw new IllegalArgumentException("Album name cannot be empty.");
            }
            if(album.getReleaseDate() == null){
                throw new IllegalArgumentException("Release date cannot be empty");
            }
            if(album.getArtist() == null || album.getArtist().getArtistId() <= 0){
                throw new IllegalArgumentException("Artist Id cannot be empty");
            }
            albumRepository.save(album);
        }catch(IllegalArgumentException e) {
            logger.error("Error in adding track " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch(Exception e){
            logger.error("Error while adding album "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void addTrack(Track track) throws Exception {
        try {
            logger.info("AddTrack method called");
            if(track.getTrackName()== null || track.getTrackName().trim().isEmpty()){
                throw new IllegalArgumentException("Track name cannot be empty");
            }
            if(track.getTrackLength() <=0 ){
                throw new IllegalArgumentException("Track length should be greater than zero");
            }
            if(track.getTrackNumber() <=0 ){
                throw new IllegalArgumentException("Track Number should be greater than zero");
            }
            if(track.getAlbum() == null || track.getAlbum().getAlbumId() <=0 ){
                throw new IllegalArgumentException("Album id should be greater than zero");
            }
            if(track.getGenre()==null || track.getGenre().getGenreName() == null){
                throw new IllegalArgumentException("Genre Name cannot be empty");
            }

            Track trackNumberCheck = trackRepository.findByAlbum_AlbumIdAndTrackNumber(track.getAlbum().getAlbumId(), track.getTrackNumber());
            if (trackNumberCheck != null) {
                throw new CustomException("Already a album exists in this track number in this album");
            }
            Genre existingGenre = genreRepository.findByGenreName(track.getGenre().getGenreName());
            if (existingGenre == null) {
                existingGenre = new Genre();
                existingGenre.setGenreName(track.getGenre().getGenreName());
                genreRepository.save(existingGenre);
            }
            track.setGenre(existingGenre);
            trackRepository.save(track);
        }catch(IllegalArgumentException e) {
            logger.error("Error in adding track " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch (CustomException e) {
            logger.error("Error in adding track " + e.getMessage());
            throw new CustomException(e.getMessage());
        }catch(Exception e){
            logger.error("Error in adding track "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void registerUser(User user) throws Exception{
        try{
            logger.info("registerUser method was called");
            if(user.getUserName() == null || user.getUserName().trim().isEmpty()){
                throw new IllegalArgumentException("User name cannot be empty.");
            }
            if(user.getUserPassword() == null || user.getUserPassword().trim().isEmpty()){
                throw new IllegalArgumentException("User password cannot be empty.");
            }
            if(user.getUserEmail() == null || user.getUserEmail().trim().isEmpty()){
                throw new IllegalArgumentException("User email cannot be empty.");
            }
            if(userRepository.findByUserEmail(user.getUserEmail()) != null){
                throw new CustomException("Already email address exists");
            }else{
                userRepository.save(user);
            }
        }catch(IllegalArgumentException e) {
            logger.error("Error in adding track " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch(DataIntegrityViolationException e){
            logger.error("Already email address exists");
            throw new CustomException("Already email address exists");
        }
        catch(Exception e){
            logger.error("Error in adding user "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public String loginUser(User user) throws Exception{
        try{
            logger.info("loginUser method was called");
            if(user.getUserEmail() == null || user.getUserEmail().trim().isEmpty()){
                throw new IllegalArgumentException("User email cannot be empty.");
            }
            if(user.getUserPassword() == null || user.getUserPassword().trim().isEmpty()){
                throw new IllegalArgumentException("User password cannot be empty.");
            }
            User existingUser=userRepository.findByUserEmailAndUserPassword(user.getUserEmail(), user.getUserPassword());
            if(existingUser == null) {
                throw new CustomException("Invalid Email and Password");
            }

            Token isTokenExists=tokenRepository.findByUserId(existingUser.getUserId()).orElse(null);
            if(isTokenExists!=null){
                return isTokenExists.getToken();
            }else{
                String token=jwtService.generateToken(existingUser);
                Token userToken= new Token(existingUser.getUserId(),token,jwtService.extractExpiration(token));
                tokenRepository.save(userToken);
                return token;
            }
        }catch(IllegalArgumentException e) {
            logger.error("Error in adding track " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch (CustomException e) {
            logger.error("Error in loginUser " + e.getMessage());
            throw new CustomException(e.getMessage());
        }catch(Exception e){
            logger.error("Error in loginUser "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @Scheduled(fixedRate = 10000)
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens();
    }

    public List<Artist> viewAllArtist() throws Exception {
        try{
            logger.info("View All Artist method was called");
            return artistRepository.findAll();
        }catch(Exception e){
            logger.error("Error while retrieving data "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public List<Album> viewAlbumByArtist(String artistName) throws Exception {
        try{
            logger.info("viewAlbumByArtist method was called");
            return albumRepository.findByArtist_ArtistName(artistName);
        }catch(Exception e){
            logger.error("Error in retrieving album "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public List<Track> viewTracksByAlbum(String albumName) throws Exception {
        try{
            logger.info("viewTracksByAlbum method was called");
            return trackRepository.findByAlbum_AlbumName(albumName);
        }catch(Exception e){
            logger.error("Error in retrieving track "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public List<Track> viewTrackByGenre(String genreName) throws Exception {
        try{
            logger.info("viewTrackByGenre method was called");
            return trackRepository.findByGenre_GenreName(genreName);
        }catch(Exception e){
            logger.error("Error in retrieving track "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public List<Track> viewSortedTrackByDuration() throws Exception {
        try{
            logger.info("viewSortedTrackByDuration method was called");
            return trackRepository.findAllByOrderByTrackLengthAsc();
        }catch(Exception e){
            logger.error("Error in retrieving track "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public List<Genre> viewAllGenre() throws Exception{
        try{
            logger.info("viewAllGenre method was called");
            return genreRepository.findAll();
        }catch(Exception e){
            logger.error("Error in retrieving genre "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Album getAlbumById(int albumId) throws Exception{
        try{
            logger.info("getAlbumById method was called");
            return albumRepository.findById(albumId).orElse(null);
        }catch(Exception e){
            logger.error("Error in retrieving Album "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Track getTrackById(int trackId) throws Exception{
        try{
            logger.info("getTrackById method was called");
            return trackRepository.findById(trackId).orElse(null);
        }catch(Exception e){
            logger.error("Error in retrieving Track "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Artist getArtistById(int artistId) throws Exception{
        try{
            logger.info("getAlbumById method was called");
            return artistRepository.findById(artistId).orElse(null);
        }catch(Exception e){
            logger.error("Error in retrieving Album "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void updateArtist(int artistId , Artist artist) throws Exception {
        try{
            logger.info("updateArtist method was called");
            if (artist.getArtistName() == null || artist.getArtistName().trim().isEmpty()) {
                throw new IllegalArgumentException("Artist name cannot be empty.");
            }
            if (artist.getCountry() == null || artist.getCountry().trim().isEmpty()){
                throw new IllegalArgumentException("Country cannot be empty.");
            }
            Artist retrirvedArtist=artistRepository.findById(artistId).orElse(null);
            if(retrirvedArtist != null){
                retrirvedArtist.setArtistName(artist.getArtistName());
                retrirvedArtist.setCountry(artist.getCountry());
                artistRepository.save(retrirvedArtist);
            }else{
                throw new CustomException("Artist does not Exist");
            }
        }catch(IllegalArgumentException e) {
            logger.error("Error in updating artist " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch (CustomException e){
            logger.error("Error in updating artist "+e.getMessage());
            throw new CustomException(e.getMessage());
        }catch(Exception e){
            logger.error("Error in updating artist "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void updateAlbum(int albumId, Album album) throws Exception {
        try{
            logger.info("updateAlbum method was called");
            if (album.getAlbumName() == null || album.getAlbumName().trim().isEmpty()) {
                throw new IllegalArgumentException("Album name cannot be empty.");
            }
            if(album.getReleaseDate() == null){
                throw new IllegalArgumentException("Release date cannot be empty");
            }
            if(album.getArtist() == null || album.getArtist().getArtistId() <= 0){
                throw new IllegalArgumentException("Artist Id cannot be empty");
            }
            Album retrievedAlbum=albumRepository.findById(albumId).orElse(null);
            if(retrievedAlbum != null){
                retrievedAlbum.setAlbumName(album.getAlbumName());
                retrievedAlbum.setReleaseDate(album.getReleaseDate());
                if(album.getArtist() !=null) {
                    Artist artist=artistRepository.findById(album.getArtist().getArtistId()).orElse(null);
                    if(artist != null){
                        retrievedAlbum.setArtist(artist);
                    }else{
                        throw new CustomException("Artist does not Exist");
                    }
                }
                albumRepository.save(retrievedAlbum);
            }else{
                throw new CustomException("Album does not exist");
            }
        }catch(IllegalArgumentException e) {
            logger.error("Error in updating album " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch (CustomException e){
            logger.error("Error in updating album "+e.getMessage());
            throw new CustomException(e.getMessage());
        }catch(Exception e){
            logger.error("Error in updating album "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void updateTrack(int trackId, Track track) throws Exception {
        try {
            logger.info("updateTrack method was called");
    
            if (track.getTrackName() == null || track.getTrackName().trim().isEmpty()) {
                throw new IllegalArgumentException("Track name cannot be empty");
            }
            if (track.getTrackLength() <= 0) {
                throw new IllegalArgumentException("Track length should be greater than zero");
            }
            if (track.getTrackNumber() <= 0) {
                throw new IllegalArgumentException("Track Number should be greater than zero");
            }
            if (track.getAlbum() == null || track.getAlbum().getAlbumId() <= 0) {
                throw new IllegalArgumentException("Album id should be greater than zero");
            }
    
            Track trackNumberCheck = trackRepository.findByAlbum_AlbumIdAndTrackNumber(track.getAlbum().getAlbumId(), track.getTrackNumber());
            if (trackNumberCheck != null && trackNumberCheck.getTrackId() != trackId) {
                throw new CustomException("A track with this number already exists in this album");
            }
    
            Track retrievedTrack = trackRepository.findById(trackId).orElse(null);
            if (retrievedTrack != null) {
                retrievedTrack.setTrackName(track.getTrackName());
                retrievedTrack.setTrackNumber(track.getTrackNumber());
                retrievedTrack.setTrackLength(track.getTrackLength());
    
                // Update album if provided
                if (track.getAlbum() != null) {
                    Album album = albumRepository.findById(track.getAlbum().getAlbumId()).orElse(null);
                    if (album != null) {
                        retrievedTrack.setAlbum(album);
                    } else {
                        throw new CustomException("Album not found");
                    }
                }
    
                // Update genre if provided
                if (track.getGenre() != null) {
                    Genre existingGenre = genreRepository.findByGenreName(track.getGenre().getGenreName());
                    if (existingGenre == null) {
                        existingGenre = new Genre();
                        existingGenre.setGenreName(track.getGenre().getGenreName());
                        genreRepository.save(existingGenre);
                    }
                    retrievedTrack.setGenre(existingGenre);
                }
    
                // Save the updated track
                trackRepository.save(retrievedTrack);
            } else {
                throw new CustomException("Track not found");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error in updating track: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        } catch (CustomException e) {
            logger.error("Error in updating track: " + e.getMessage());
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error in updating track: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
    

    public void deleteTrack(int trackId) throws Exception {
        try {
            logger.info("deleteTrack method was called");
            if (!trackRepository.existsById(trackId)) {
                throw new CustomException("Track not found");
            }
            trackRepository.deleteById(trackId);
        }catch(CustomException e){
            logger.error("Error in deleting track "+e.getMessage());
            throw new CustomException(e.getMessage());
        }catch(Exception e){
            logger.error("Error in deleting track "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void deleteAlbum(int albumId) throws Exception {
        try{
            logger.info("deleteAlbum method was called");
            if(!albumRepository.existsById(albumId)){
                throw new CustomException("Album not found");
            }
            albumRepository.deleteById(albumId);
        }catch(CustomException e){
            logger.error("Error in deleting album "+e.getMessage());
            throw new CustomException(e.getMessage());
        }catch(Exception e){
            logger.error("Error in deleting album "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void deleteArtist(int artistId) throws Exception {
        try{
            logger.info("deleteArtist method was called");
            if(!artistRepository.existsById(artistId)){
                throw new CustomException("Artist not found");
            }
            artistRepository.deleteById(artistId);
        }catch(CustomException e){
            logger.error("Error in deleting artist "+e.getMessage());
            throw new CustomException(e.getMessage());
        }catch(Exception e){
            logger.error("Error in deleting artist "+e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
