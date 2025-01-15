package com.example.music_system.controller;

import com.example.music_system.Exception.CustomException;
import com.example.music_system.model.Album;
import com.example.music_system.model.Artist;
import com.example.music_system.model.Genre;
import com.example.music_system.model.Track;
import com.example.music_system.model.User;
import com.example.music_system.service.MusicService;

import jakarta.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class MusicController {
    @Autowired
    private MusicService service;

    public MusicController(MusicService service){
        this.service=service;
    }

    Logger logger= LoggerFactory.getLogger(MusicController.class);

    @PostMapping("/addArtist")
    public ResponseEntity<Object> addArtist(@RequestBody Artist artist){
        try{
            service.addArtist(artist);
            logger.info("Artist added Successfully");
            Map<String, String> response = new HashMap<>();
            response.put("message", "Artist added Successfully ");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addAlbum")
    public ResponseEntity<Object> addAlbum(@RequestBody Album album){
        try{
            service.addAlbum(album);
            logger.info("Album added successfully");
            Map<String, String> response = new HashMap<>();
            response.put("message", "Album added successfully ");
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addTrack")
    public ResponseEntity<Object> addTrack(@RequestBody Track trackInput){
        try {
            service.addTrack(trackInput);
            logger.info("Track added Successfully");
            Map<String, String> response = new HashMap<>();
            response.put("message", "Track added successfully");
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        }catch(IllegalArgumentException | CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/registerUser")
    public ResponseEntity<Object> registerUser(@RequestBody User user){
        try{
            service.registerUser(user);
            logger.info("User added successfully");
            Map<String, String> response = new HashMap<>();
            response.put("message", "User added successfully ");
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        }catch(IllegalArgumentException | CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }         
    }

    @PostMapping("/loginUser")
    public ResponseEntity<Object> loginUser(@RequestBody User user){
        try{
            String token=service.loginUser(user);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch(IllegalArgumentException | CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/allArtist")
    public ResponseEntity<Object> viewAllArtist(){
        try {
            List<Artist> artistList=service.viewAllArtist();
            Map<String,List<Artist>> response=new HashMap<>();
            response.put("data",artistList);
            if(artistList != null){
                logger.info("Artist data fetched");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }else{
                logger.warn("No Artist data found");
                return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/album/{artist}")
    public ResponseEntity<Object> viewAlbumByArtist(@PathVariable("artist") String artistName){
        try{
            List<Album> albumList= service.viewAlbumByArtist(artistName);
            Map<String,List<Album>> response=new HashMap<>();
            response.put("data",albumList);
            if(!albumList.isEmpty()){
                logger.info("Album data fetched");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }else{
                logger.warn("No album data found for this artist");
                return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/track/{album}")
    public ResponseEntity<Object> viewTracksByAlbum(@PathVariable("album") String albumName){
        try{
            List<Track> trackList= service.viewTracksByAlbum(albumName);
            Map<String,List<Track>> response=new HashMap<>();
            response.put("data",trackList);
            if(!trackList.isEmpty()){
                logger.info("Track data fetched");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }else{
                logger.warn("No track data found for this album");
                return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getTrackByGenre/{genreName}")
    public ResponseEntity<Object> viewTrackByGenre(@PathVariable("genreName") String genreName){
        try{
            List<Track> trackList=  service.viewTrackByGenre(genreName);
            Map<String,List<Track>> response=new HashMap<>();
            response.put("data",trackList);
            if(!trackList.isEmpty()){
                logger.info("Track data fetched");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }else{
                logger.warn("No track data found for this genre "+genreName);
                return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getTrackByOrder")
    public ResponseEntity<Object> viewSortedTrackByDuration(){
        try{
            List<Track> trackList = service.viewSortedTrackByDuration();
            Map<String,List<Track>> response=new HashMap<>();
            response.put("data",trackList);
            if(!trackList.isEmpty()){
                logger.info("Track data fetched");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }else{
                logger.warn("No track data found");
                return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getAllGenre")
    public ResponseEntity<Object> getAllGenre(){
        try{    
            List<Genre> genreList=service.viewAllGenre();
            Map<String,List<Genre>> response=new HashMap<>();
            response.put("data",genreList);
            if(!genreList.isEmpty()){
                logger.info("Genre data fetched");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }else{
                logger.warn("No genre data found");
                return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getArtistById/{artistId}")
    public ResponseEntity<Object> getArtistById(@PathVariable("artistId") int artistId){
        try{
            Artist artist= service.getArtistById(artistId);
            if(artist == null){
                logger.warn("No Artist data found");
                return new ResponseEntity<>(artist,HttpStatus.NO_CONTENT);
            }else{
                logger.info("Artist data fetched");
                return new ResponseEntity<>(artist,HttpStatus.OK);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getAlbumByID/{albumId}")
    public ResponseEntity<Object> getAlbumById(@PathVariable("albumId") int albumId){
        try{
            Album album= service.getAlbumById(albumId);
            if(album == null){
                logger.warn("No Album data found");
                return new ResponseEntity<>(album,HttpStatus.NO_CONTENT);
            }else{
                logger.info("Album data fetched");
                return new ResponseEntity<>(album,HttpStatus.OK);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getTrackById/{trackId}")
    public ResponseEntity<Object> getTrackById(@PathVariable("trackId") int trackId){
        try{
            Track track= service.getTrackById(trackId);
            if(track == null){
                logger.warn("No Track data found");
                return new ResponseEntity<>(track,HttpStatus.NO_CONTENT);
            }else{
                logger.info("Track data fetched");
                return new ResponseEntity<>(track,HttpStatus.OK);
            }
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateArtist/{artistId}")
    public ResponseEntity<Object> updateArtist(@PathVariable("artistId") int artistId , @RequestBody Artist artist){
        try {
            service.updateArtist(artistId, artist);
            logger.info("Artist updated Successfully");
            Map<String, String> response = new HashMap<>();
            response.put("message", "Artist updated Successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch(IllegalArgumentException | CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateAlbum/{albumId}")
    public ResponseEntity<Object> updateAlbum(@PathVariable("albumId") int albumId, @RequestBody Album album){
        try{
            service.updateAlbum(albumId,album);
            logger.info("Album updated Successfully");
            Map<String, String> response = new HashMap<>();
            response.put("message", "Album updated Successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch(IllegalArgumentException | CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateTrack/{trackId}")
    public ResponseEntity<Object> updateTrack(@PathVariable(name = "trackId") int trackId, @RequestBody Track track){
        try{
            service.updateTrack(trackId,track);
            logger.info("Track updated Successfully");
            Map<String, String> response = new HashMap<>();
            response.put("message","Track updated Successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch(IllegalArgumentException | CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteTrack/{trackId}")
    public ResponseEntity<Object> deleteTrack(@PathVariable(name = "trackId") int trackId){
        try{
            service.deleteTrack(trackId);
            logger.info("Track deleted successfully");
            Map<String,String> response=new HashMap<>();
            response.put("message","Track deleted successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch(CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAlbum/{albumId}")
    public ResponseEntity<Object> deleteAlbum(@PathVariable(name = "albumId") int albumId){
        try{
            service.deleteAlbum(albumId);
            logger.info("Album deleted successfully");
            Map<String,String> response=new HashMap<>();
            response.put("message","Album deleted successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch(CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteArtist/{artistId}")
    public ResponseEntity<Object> deleteArtist(@PathVariable(name = "artistId") int artistId){
        try{
            service.deleteArtist(artistId);
            logger.info("Artist deleted successfully");
            Map<String,String> response=new HashMap<>();
            response.put("message","Artist deleted successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch(CustomException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
