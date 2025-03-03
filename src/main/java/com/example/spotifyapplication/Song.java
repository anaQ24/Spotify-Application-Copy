package com.example.spotifyapplication;

public class Song {
    private int songID;
    private String songName;
    private String artistName;
    private String songLink;

    public Song(int songID, String songName, String artistName, String songLink) {
        this.songID = songID;
        this.songName = songName;
        this.artistName = artistName;
        this.songLink = songLink;
    }

    public int getSongId(){
        return songID;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongLink() {
        return songLink;
    }
}


