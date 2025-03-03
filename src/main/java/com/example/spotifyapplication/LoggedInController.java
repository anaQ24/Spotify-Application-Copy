package com.example.spotifyapplication;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.*;

public class LoggedInController {

    private int currentSongIndex;
    private int songChangeCount;
    private long lastSongChangeTime;
    private boolean isPremium;
    private boolean isLiked;

    private Label songName, artistName, premiumActivated;
    private ObservableList<Song> songsList;
    private ImageView imgView;
    private Image albumImage;
    private Button premiumBtn;
    private String imageAddress;
    private String username;
    private String songLink;

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Media media;

    private HelloApplication app = new HelloApplication();

    public void setUserInformation(String username){
        this.username = username;
    }

    public AnchorPane createContent() {
        songsList = app.getSongsFromDatabase();

        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #000000;");
        root.setPrefSize(812, 465);

        AnchorPane sidebar = new AnchorPane();
        sidebar.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
        sidebar.setPrefSize(240, 465);

        Image image = new Image("file:C:/Users/anaqu/Desktop/Spotify/src/main/java/com/example/spotify/abstract-user-flat-4.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        AnchorPane.setTopAnchor(imageView, 50.0);
        AnchorPane.setLeftAnchor(imageView, 70.0);

        Label usernameLabel = new Label(username);
        usernameLabel.setFont(Font.font("System Bold", 20));
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setPrefSize(182, 53);
        usernameLabel.setLayoutX(28);
        usernameLabel.setLayoutY(180);
        usernameLabel.setAlignment(Pos.CENTER);

        premiumActivated = new Label("");
        if (getUserPremiumStatusFromDatabase(username)){
            premiumBtn = new Button("Deactivate Premium");
            premiumActivated.setText("Premium Activated!");
            premiumActivated.setFont(Font.font("System Bold", 15));
            premiumActivated.setTextFill(Color.WHITE);
            premiumActivated.setPrefSize(182, 53);
            premiumActivated.setLayoutX(28);
            premiumActivated.setLayoutY(300);
            premiumActivated.setAlignment(Pos.CENTER);
            sidebar.getChildren().add(premiumActivated);
        } else {
            premiumBtn = new Button("Activate Premium");
        }
        premiumBtn.setFont(Font.font("System Bold", 18));
        premiumBtn.setTextFill(Color.WHITE);
        premiumBtn.setStyle("-fx-background-color: #063000;");
        premiumBtn.setPrefSize(190, 40);
        premiumBtn.setLayoutX(30);
        premiumBtn.setLayoutY(260);
        premiumBtn.setAlignment(Pos.CENTER);

        premiumBtn.setOnAction(event -> {
            isPremium = getUserPremiumStatusFromDatabase(username);

            if (!isPremium) {
                activatePremium(username);
                premiumBtn.setText("Deactivate Premium");
                premiumActivated.setText("Premium Activated!");
                premiumActivated.setFont(Font.font("System Bold", 15));
                premiumActivated.setTextFill(Color.WHITE);
                premiumActivated.setPrefSize(182, 53);
                premiumActivated.setLayoutX(28);
                premiumActivated.setLayoutY(300);
                premiumActivated.setAlignment(Pos.CENTER);
                sidebar.getChildren().add(premiumActivated);
            } else {
                deactivatePremium(username);
                premiumBtn.setText("Activate Premium");
                sidebar.getChildren().remove(premiumActivated);
            }
        });

        Button logoutBtn = new Button("Log out");
        logoutBtn.setFont(Font.font("System Bold", 20));
        logoutBtn.setTextFill(Color.WHITE);
        logoutBtn.setStyle("-fx-background-color: #062613;");
        logoutBtn.setPrefSize(113, 46);
        logoutBtn.setLayoutX(62);
        logoutBtn.setLayoutY(392);
        logoutBtn.setAlignment(Pos.CENTER);
        logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                Scenes.changeScene(actionEvent, "spotify-login.fxml", "Log In", null);
            }
        });

        sidebar.getChildren().addAll(imageView, usernameLabel, premiumBtn, logoutBtn);

        AnchorPane controlBar = new AnchorPane();
        controlBar.setStyle("-fx-background-color: #062613;");
        controlBar.setPrefSize(585, 60);
        controlBar.setLayoutX(241);
        controlBar.setLayoutY(405);

        Button likeBtn = new Button("");
        checkIfLiked(likeBtn, songsList.get(currentSongIndex).getSongId());
        likeBtn.setFont(Font.font("System Bold", 12));
        likeBtn.setTextFill(Color.web("#062613"));
        likeBtn.setStyle("-fx-background-color: #ffffff;");
        likeBtn.setPrefSize(50, 26);
        likeBtn.setLayoutX(15);
        likeBtn.setLayoutY(17);

        likeBtn.setOnAction(e -> {
            isLiked = checkIfLikedFromDB(username, songsList.get(currentSongIndex).getSongId());
            if (isLiked) {
                unlikeSong(username, songsList.get(currentSongIndex).getSongId());
                likeBtn.setText("Like");
            } else {
                likeSong(username, songsList.get(currentSongIndex).getSongId());
                likeBtn.setText("Unlike");
            }
        });

        songLink = getSongURL(songsList.get(currentSongIndex).getSongId());
        media = new Media(songLink);
        mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);

        Button playBtn = new Button("Play");
        playBtn.setFont(Font.font("System Bold", 12));
        playBtn.setTextFill(Color.web("#062613"));
        playBtn.setStyle("-fx-background-color: #ffffff;");
        playBtn.setPrefSize(42, 26);
        playBtn.setLayoutX(70);
        playBtn.setLayoutY(17);
        if (!songsList.isEmpty()) {
            playBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    mediaPlayer.play();
                }
            });
        }

        Button stopBtn = new Button("Stop");
        stopBtn.setFont(Font.font("System Bold", 12));
        stopBtn.setTextFill(Color.web("#062613"));
        stopBtn.setStyle("-fx-background-color: #ffffff;");
        stopBtn.setPrefSize(42, 26);
        stopBtn.setLayoutX(120);
        stopBtn.setLayoutY(17);
        stopBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mediaPlayer.pause();
            }
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setFont(Font.font("System Bold", 12));
        exitBtn.setTextFill(Color.web("#062613"));
        exitBtn.setStyle("-fx-background-color: #ffffff;");
        exitBtn.setPrefSize(42, 26);
        exitBtn.setLayoutX(170);
        exitBtn.setLayoutY(17);
        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mediaPlayer.stop();
            }
        });

        Button previousBtn = new Button("Previous");
        previousBtn.setFont(Font.font("System Bold", 12));
        previousBtn.setTextFill(Color.web("#062613"));
        previousBtn.setStyle("-fx-background-color: #ffffff;");
        previousBtn.setLayoutX(436);
        previousBtn.setLayoutY(17);
        previousBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentSongIndex > 0) {
                    currentSongIndex--;
                    songChangeCount--;
                }

                checkIfLiked(likeBtn, songsList.get(currentSongIndex).getSongId());
                updateSongInfo();
            }
        });

        Button nextBtn = new Button("Next");
        nextBtn.setFont(Font.font("System Bold", 12));
        nextBtn.setTextFill(Color.web("#062613"));
        nextBtn.setStyle("-fx-background-color: #ffffff;");
        nextBtn.setLayoutX(510);
        nextBtn.setLayoutY(17);
        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long currentTime = System.currentTimeMillis();
                if (!getUserPremiumStatusFromDatabase(username)) {
                    if (songChangeCount >= 4 && currentTime - lastSongChangeTime < 3600000) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Limit Reached");
                        alert.setContentText("You can change the song only 4 times per hour.");
                        alert.show();
                        return;
                    } else if (currentTime - lastSongChangeTime >= 3600000) {
                        songChangeCount = 0;
                    }
                }

                if (currentSongIndex < songsList.size() - 1) {
                    currentSongIndex++;
                } else {
                    currentSongIndex = 0;
                }

                updateSongInfo();

                checkIfLiked(likeBtn, songsList.get(currentSongIndex).getSongId());

                songChangeCount++;
                lastSongChangeTime = currentTime;
            }
        });

        controlBar.getChildren().addAll(mediaView, likeBtn, playBtn, stopBtn, exitBtn, previousBtn, nextBtn);

        albumImage = new Image("https://news.djcity.com/wp-content/uploads/2016/12/spotify-600.jpg");
        imgView = new ImageView(albumImage);

        imgView.setPreserveRatio(true);

        double maxWidth = 400;
        double maxHeight = 200;

        imgView.setFitWidth(Math.min(albumImage.getWidth(), maxWidth));
        imgView.setFitHeight(Math.min(albumImage.getHeight(), maxHeight));

        imgView.setSmooth(true);

        root.setTopAnchor(imgView, 60.0);
        root.setLeftAnchor(imgView, 387.0);

        songName = new Label(songsList.get(currentSongIndex).getSongName());
        songName.setFont(Font.font("Arial Bold", 20));
        songName.setTextFill(Color.WHITE);
        songName.setPrefSize(190, 46);
        songName.setLayoutX(430);
        songName.setLayoutY(284);
        songName.setAlignment(Pos.CENTER);

        artistName = new Label(songsList.get(currentSongIndex).getArtistName());
        artistName.setFont(Font.font("Arial Bold", 15));
        artistName.setTextFill(Color.WHITE);
        artistName.setPrefSize(190, 46);
        artistName.setLayoutX(430);
        artistName.setLayoutY(330);
        artistName.setAlignment(Pos.CENTER);

        root.getChildren().addAll(sidebar, controlBar, imgView, songName, artistName);

        return root;
    }

    public void activatePremium(String username) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            String updateQuery = "UPDATE users SET premium_status = 1 WHERE username = ?";
            preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, username);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Premium status activated for " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deactivatePremium(String username) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            String updateQuery = "UPDATE users SET premium_status = 0 WHERE username = ?";
            preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, username);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Premium status deactivated for " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getUserPremiumStatusFromDatabase(String username) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            String query = "SELECT premium_status FROM users WHERE username = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int premiumStatus = resultSet.getInt("premium_status");
                isPremium = (premiumStatus == 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isPremium;
    }

    private void updateSongInfo() {
        if (songsList != null && !songsList.isEmpty() && currentSongIndex >= 0 && currentSongIndex < songsList.size()) {
            songName.setText(songsList.get(currentSongIndex).getSongName());
            artistName.setText(songsList.get(currentSongIndex).getArtistName());

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            songLink = getSongURL(songsList.get(currentSongIndex).getSongId());
            media = new Media(songLink);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.setOnReady(() -> mediaPlayer.play());

//            if (albumImage != null){
//                createContent().getChildren().remove(imgView);
//            }
//            albumImage = new Image(getImageURL(songsList.get(currentSongIndex).getSongId()));
//            imgView = new ImageView(albumImage);

        } else {
            System.out.println("No songs to display.");
        }
    }

    private boolean checkIfLikedFromDB(String username, int songId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT 1 FROM user_songs WHERE user_id = (SELECT user_id FROM users WHERE username = ?) AND song_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, songId);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isLiked = true;
            } else {
                isLiked = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isLiked;
    }

    private void likeSong(String username, int songId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            String insertQuery = "INSERT INTO user_songs (user_id, song_id) VALUES ((SELECT user_id FROM users WHERE username = ?), ?)";
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, songId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void unlikeSong(String username, int songId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            String deleteQuery = "DELETE FROM user_songs WHERE user_id = (SELECT user_id FROM users WHERE username = ?) AND song_id = ?";
            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, songId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkIfLiked(Button likeBtn, int songId) {
        isLiked = checkIfLikedFromDB(username, songId);
        if (isLiked) {
            likeBtn.setText("Unlike");
        } else {
            likeBtn.setText("Like");
        }
    }

    private String getSongURL(int songId) {
        String query = "SELECT song_link FROM songs WHERE song_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, songId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    songLink = resultSet.getString("song_link");
                } else {
                    System.out.println("No song found with the given song_id.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songLink;
    }

    private String getImageURL(int songId) {
        String query = "SELECT image_address FROM songs WHERE song_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, songId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    imageAddress = resultSet.getString("image_address");
                } else {
                    System.out.println("No song found with the given song_id.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imageAddress;
    }

}
