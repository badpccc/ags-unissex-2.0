package com.example;

import com.example.models.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class NovoClienteController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtTelefone;

    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnSalvar;

    private Consumer<Cliente> onClienteSalvo;

    @FXML
    public void initialize() {
        aplicarMascaraTelefone(txtTelefone);

        // Nome apenas letras
        txtNome.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZÀ-ÿ ]*")) {
                txtNome.setText(newValue.replaceAll("[^a-zA-ZÀ-ÿ ]", ""));
            }
        });

        btnSalvar.setOnAction(e -> salvar());
    }

    /**
     * MASCARA TELEFONE (75) 98888-8888
     */
    private void aplicarMascaraTelefone(TextField campo) {
        campo.textProperty().addListener((obs, oldValue, newValue) -> {

            // Remove tudo que não for número
            String digits = newValue.replaceAll("[^0-9]", "");

            // Limita 11 dígitos
            if (digits.length() > 11) {
                digits = digits.substring(0, 11);
            }

            StringBuilder formatado = new StringBuilder();
            int len = digits.length();

            if (len > 0) {
                formatado.append("(")
                        .append(digits.substring(0, Math.min(2, len)));
            }

            if (len >= 3) {
                formatado.append(") ").append(digits.substring(2, Math.min(7, len)));
            }

            if (len >= 8) {
                formatado.append("-").append(digits.substring(7));
            }

            campo.setText(formatado.toString());
            campo.positionCaret(formatado.length());
        });
    }

    private void salvar() {

        String nome = txtNome.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();

        // Validação simples de e-mail
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println("Email inválido!");
            return;
        }

        Cliente cliente = new Cliente(nome, telefone, email);

        if (onClienteSalvo != null) {
            onClienteSalvo.accept(cliente);
        }

        btnSalvar.getScene().getWindow().hide();
    }

    public void setOnClienteSalvo(Consumer<Cliente> callback) {
        this.onClienteSalvo = callback;
    }
}
