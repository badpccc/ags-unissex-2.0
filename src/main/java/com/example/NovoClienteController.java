package com.example;

import com.example.models.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

        btnSalvar.setOnAction(e -> salvar());
    }

    private void salvar() {
        String nome = txtNome.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();

        Cliente cliente = new Cliente(nome, telefone, email);

        if (onClienteSalvo != null) {
            onClienteSalvo.accept(cliente);
        }

        // fecha o modal
        btnSalvar.getScene().getWindow().hide();
    }

    public void setOnClienteSalvo(Consumer<Cliente> callback) {
        this.onClienteSalvo = callback;
    }
}
