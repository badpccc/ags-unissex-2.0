package com.example;

import com.example.backends.classes.Client;
import com.example.backends.database.data.ClientDAO;
import com.example.utils.TelegramNotifier;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarClienteController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtTelefone;

    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnSalvar;

    @FXML
    private Button btnCancelar;

    private Client cliente; // cliente atual sendo editado

    // ==============================
    // 🔵 Chamado pelo ClientesController
    // ==============================
    public void setCliente(Client cliente) {
        this.cliente = cliente;

        // Preencher os campos
        txtNome.setText(cliente.getName());
        txtTelefone.setText(cliente.getPhoneNumber());
        txtEmail.setText(cliente.getEmail());
    }

    @FXML
    public void initialize() {

        // Botão cancelar fecha a janela
        btnCancelar.setOnAction(e -> fecharJanela());

        // Botão salvar faz todo o processo
        btnSalvar.setOnAction(e -> salvarAlteracoes());
    }

    // ==============================
    // 🔵 SALVAR ALTERAÇÕES
    // ==============================
    private void salvarAlteracoes() {

        try {
            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim();

            // ----------------------------
            // 1. VALIDAÇÕES SIMPLES
            // ----------------------------
            if (nome.isEmpty()) {
                alertar("O nome não pode ficar vazio.");
                return;
            }

            if (telefone.isEmpty()) {
                alertar("O telefone não pode ficar vazio.");
                return;
            }

            if (!email.contains("@")) {
                alertar("E-mail inválido.");
                return;
            }

            // ----------------------------
            // 2. Atualizar objeto cliente
            // ----------------------------
            cliente.setName(nome);
            cliente.setPhoneNumber(telefone);
            cliente.setEmail(email);

            // ----------------------------
            // 3. Atualizar no banco
            // ----------------------------
            boolean sucesso = ClientDAO.update(cliente);

            if (!sucesso) {

                TelegramNotifier.sendError(
                        "Erro ao editar cliente:\n" +
                                "ID: " + cliente.getId() + "\n" +
                                "Nome: " + cliente.getName()
                );

                alertar("Erro ao salvar alterações no banco.");
                return;
            }

            // ----------------------------
            // 4. Notificar sucesso
            // ----------------------------
            TelegramNotifier.send(
                    "✏ Cliente atualizado!\n\n" +
                            "👤 Nome: " + cliente.getName() + "\n" +
                            "📞 Telefone: " + cliente.getPhoneNumber() + "\n" +
                            "📧 Email: " + cliente.getEmail()
            );

            fecharJanela();

        } catch (Exception ex) {

            TelegramNotifier.sendError(
                    "Exceção ao editar cliente:\n```\n" +
                            ex.getMessage() + "\n```"
            );

            ex.printStackTrace();
            alertar("Erro inesperado ao salvar.");
        }
    }

    // ==============================
    // 🔵 FECHAR JANELA
    // ==============================
    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // ==============================
    // 🔵 ALERTA SIMPLES
    // ==============================
    private void alertar(String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
