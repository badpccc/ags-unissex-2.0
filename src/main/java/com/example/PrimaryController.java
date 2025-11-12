package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void switchToSecondary() throws IOException {
        // Aqui você pode futuramente validar login
        App.setRoot("secondary");
    }
}
