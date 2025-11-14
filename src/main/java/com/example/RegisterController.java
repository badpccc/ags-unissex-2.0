package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPass;

    @FXML
    private PasswordField txtPass2;

    @FXML
    private Label lblMessage;

    @FXML
    private void handleRegister() {
        String user = txtUser.getText();
        String pass = txtPass.getText();
        String pass2 = txtPass2.getText();

        if (user.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            lblMessage.setText("Preencha todos os campos!");
            return;
        }

        if (!pass.equals(pass2)) {
            lblMessage.setText("As senhas não coincidem!");
            return;
        }

        lblMessage.setText("Registrado com sucesso!");
        lblMessage.setStyle("-fx-text-fill: #4CAF50;");
    }

    @FXML
    private void handleBack() throws Exception {
        App.setRoot("primary"); // Volta para o login
    }
}
