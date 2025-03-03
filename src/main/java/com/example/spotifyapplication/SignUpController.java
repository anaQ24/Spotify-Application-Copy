package com.example.spotifyapplication;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController implements Initializable  {
    @FXML
    private Button signUp_btn;

    @FXML
    private TextField email_txtfield;

    @FXML
    private TextField username_txtfield;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button button_logIn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signUp_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (isValidEmail(email_txtfield.getText()) && isValidPassword(password_field.getText()) && !email_txtfield.getText().trim().isEmpty() && !username_txtfield.getText().trim().isEmpty() && !password_field.getText().trim().isEmpty()){
                    Scenes.signUpUser(actionEvent, email_txtfield.getText(), username_txtfield.getText(), password_field.getText());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please fill in all information correctly to sign up!");
                    alert.show();
                }
            }
        });

        button_logIn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Scenes.changeScene(actionEvent, "spotify-login.fxml", "Log In", null);
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8 || password.length() > 16) {
            return false;
        }

        String passwordRegex = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!?.#$%^&*]).+";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

}
