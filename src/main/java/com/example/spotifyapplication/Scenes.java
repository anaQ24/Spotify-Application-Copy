package com.example.spotifyapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Scenes {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username){
        Parent root = null;
        if (username != null){
            try{
                FXMLLoader loader = new FXMLLoader(Scenes.class.getResource(fxmlFile));
                root = loader.load();
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInformation(username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try{
                root = FXMLLoader.load(Scenes.class.getResource(fxmlFile));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String email, String username, String password){
        Connection connection = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();;
            ps2 = connection.prepareStatement("SELECT * FROM users WHERE email = ? OR username = ?");
            ps2.setString(1, email);
            ps2.setString(2, username);
            resultSet = ps2.executeQuery();

            if(resultSet.isBeforeFirst()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("User with this email or username already exists!");
                alert.show();
            } else {
                ps = connection.prepareStatement("INSERT INTO users (email, username, password) VALUES (?, ?, ?)");
                ps.setString(1, email);
                ps.setString(2, username);
                ps.setString(3, password);
                ps.executeUpdate();

                LoggedInController loggedInController = new LoggedInController();
                loggedInController.setUserInformation(username);

                Scene scene = new Scene(loggedInController.createContent());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Welcome!");
                stage.setScene(scene);
                stage.show();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null){
                try{
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null){
                try{
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps2 != null){
                try{
                    ps2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try{
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void logInUser(ActionEvent event, String emailOrUsername, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT username, password FROM users WHERE email = ? OR username = ?");
            preparedStatement.setString(1, emailOrUsername);
            preparedStatement.setString(2, emailOrUsername);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Email or username is incorrect!");
                alert.show();
            } else {
                String username = null;
                String retrievedPass = null;
                while (resultSet.next()){
                    username = resultSet.getString("username");
                    retrievedPass = resultSet.getString("password");
                
                    if (retrievedPass.equals(password)){
                        LoggedInController loggedInController = new LoggedInController();
                        loggedInController.setUserInformation(username);

                        Scene scene = new Scene(loggedInController.createContent());
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setTitle("Spotify");
                        stage.setScene(scene);
                        stage.show();

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Password is incorrect!!!");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (resultSet != null){
                try{
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null){
                try{
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try{
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
