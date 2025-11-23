package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.example.backends.database.data.AdmDAO;
import com.example.backends.classes.Adm;

public class RegisterController {

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPass;

    @FXML
    private PasswordField txtPass2;

    @FXML
    private Label lblMessage;

    private Adm adm = new Adm();

    @FXML
    private void handleRegister() {

        String fullName   = txtFullName.getText();
        String email      = txtEmail.getText();
        String phone      = txtPhone.getText();
        String username   = txtUser.getText();
        String pass       = txtPass.getText();
        String pass2      = txtPass2.getText();

        adm.setFullName(fullName);
        adm.setEmail(email);
        adm.setPhoneNumber(phone);
        adm.setUsername(username);
        adm.setPasswordHash(pass);


        // ======== VALIDAÇÕES ========
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                username.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            showError("Preencha todos os campos!");
            return;
        }

        if (!pass.equals(pass2)) {
            showError("As senhas não coincidem!");
            return;
        }

        // ======== SALVAR NO BANCO ========
        try {
            boolean sucesso = AdmDAO.insert(adm);

            if (sucesso) {
                lblMessage.setText("Registrado com sucesso!");
                lblMessage.setStyle("-fx-text-fill: #4CAF50;");
                clearFields();
            } else {
                showError("Erro: usuário ou email já existe!");
            }
            App.setRoot("primary"); // Voltar para login
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao registrar no banco!");
        }
    }

    private void showError(String msg) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: #FF4444;");
    }

    private void clearFields() {
        txtFullName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtUser.clear();
        txtPass.clear();
        txtPass2.clear();
    }

    @FXML
    private void handleBack() throws Exception {
        App.setRoot("primary"); // Voltar para login
    }
}
