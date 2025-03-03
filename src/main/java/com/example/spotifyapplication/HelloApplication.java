package com.example.spotifyapplication;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("spotify-signup.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sign Up");
        Image icon = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Spotify_logo_without_text.svg/1200px-Spotify_logo_without_text.svg.png");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }

    public ObservableList<Song> getSongsFromDatabase() {
        ObservableList<Song> songList = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT song_id, song_title, artist, song_link FROM songs";
            try (Statement stmt = connection.createStatement(); ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    int songID = resultSet.getInt("song_id");
                    String songName = resultSet.getString("song_title");
                    String artistName = resultSet.getString("artist");
                    String songUrl = resultSet.getString("song_link");

                    Song song = new Song(songID, songName, artistName, songUrl);
                    songList.add(song);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songList;
    }

    public static void main(String[] args) {
        launch();
    }
}