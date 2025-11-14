package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUser; // Segunda aba

    @FXML
    private PasswordField txtPass; // Segunda aba

    @FXML
    private TextField usernameField; // Primeira aba

    @FXML
    private PasswordField passwordField; // Primeira aba


    // ===============================
    // LOGIN DA SEGUNDA TELA
    // ===============================
    @FXML
    private void handleLogin() {
        switchToSecondary();
    }

    // ===============================
    // LOGIN DA PRIMEIRA TELA
    // ===============================
    @FXML
    private void switchToSecondary() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("secondary.fxml"));
            Stage stage = (Stage) txtUser.getScene().getWindow(); // pega a janela atual
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // OPCIONAL
    @FXML
    private void handleRegister() {
        System.out.println("Registrar clicado (sem ação)");
    }
}
