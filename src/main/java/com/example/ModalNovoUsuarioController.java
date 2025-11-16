package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalNovoUsuarioController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<String> cmbTipo;

    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll("Administrador", "Funcionário");
    }

    @FXML
    private void fecharModal() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvarUsuario() {
        System.out.println("Usuário cadastrado:");
        System.out.println("Nome: " + txtNome.getText());
        System.out.println("Email: " + txtEmail.getText());
        System.out.println("Tipo: " + cmbTipo.getValue());

        fecharModal();
    }
}
