package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void initialize() {
        // Aqui você pode colocar alguma lógica inicial se quiser
        // Por enquanto, a tela só respira sozinha.
    }

    @FXML
    private void onLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        System.out.println("Usuário digitado: " + user);
        System.out.println("Senha digitada: " + pass);
    }
}
