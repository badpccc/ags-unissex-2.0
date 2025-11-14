package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;

    // Qualquer login entra
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void handleRegister() throws IOException {
        App.setRoot("registro");
    }

}
