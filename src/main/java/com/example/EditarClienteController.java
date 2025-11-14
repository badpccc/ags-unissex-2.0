package com.example;

import com.example.dao.ClienteDAO;
import com.example.models.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private Cliente cliente;

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;

        txtNome.setText(cliente.getNome());
        txtTelefone.setText(cliente.getTelefone());
        txtEmail.setText(cliente.getEmail());
    }



    @FXML
    public void initialize() {
        btnSalvar.setOnAction(e -> salvar());
        btnCancelar.setOnAction(e -> fechar());
    }

    private void salvar() {
        cliente.setNome(txtNome.getText());
        cliente.setTelefone(txtTelefone.getText());
        cliente.setEmail(txtEmail.getText());

        ClienteDAO.atualizar(cliente);
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
}
