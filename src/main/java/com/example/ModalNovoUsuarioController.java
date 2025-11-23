package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalNovoUsuarioController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<String> cmbTipo;

    // ➕ Novos campos
    @FXML private TextField txtTelefone;
    @FXML private TextField txtCpf;
    @FXML private DatePicker dpDataContratacao;
    @FXML private CheckBox chkAtivo;
    @FXML private TextArea txtObservacoes;

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
        System.out.println("Senha: " + txtSenha.getText());
        System.out.println("Tipo: " + cmbTipo.getValue());

        System.out.println("Telefone: " + txtTelefone.getText());
        System.out.println("CPF: " + txtCpf.getText());
        System.out.println("Data de Contratação: " + dpDataContratacao.getValue());
        System.out.println("Ativo: " + chkAtivo.isSelected());
        System.out.println("Observações: " + txtObservacoes.getText());

        fecharModal();
    }
}
