package com.example.spotifyapplication;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;


public class LogInController implements Initializable {

    @FXML
    private Button logIn_btn;

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField pf_password;

    @FXML
    private Button button_signUp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logIn_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Scenes.logInUser(actionEvent, tf_email.getText(), pf_password.getText());
            }
        });

        button_signUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Scenes.changeScene(actionEvent, "spotify-signup.fxml", "Sign Up", null);
            }
        });
    }
}

