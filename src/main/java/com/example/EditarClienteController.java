package com.example;

import com.example.backends.classes.Client;
import com.example.backends.database.data.ClientDAO;
import com.example.utils.TelegramNotifier;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private ComboBox<String> cbHairType;
    @FXML private ComboBox<String> cbHairTexture;
    @FXML private ComboBox<String> cbScalp;
    @FXML private TextField txtAllergies;
    @FXML private CheckBox chkAtivo;
    @FXML private TextArea txtObservations;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private Client cliente; // cliente atual sendo editado

    // ==============================
    // 🔵 Chamado pelo ClientesController
    // ==============================
    public void setCliente(Client cliente) {
        this.cliente = cliente;

        // Preencher todos os campos com os dados do cliente
        txtNome.setText(cliente.getName() != null ? cliente.getName() : "");
        txtTelefone.setText(cliente.getPhoneNumber() != null ? cliente.getPhoneNumber() : "");
        txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
        txtAddress.setText(cliente.getAddress() != null ? cliente.getAddress() : "");
        txtAllergies.setText(cliente.getAllergies() != null ? cliente.getAllergies() : "");
        txtObservations.setText(cliente.getObservations() != null ? cliente.getObservations() : "");
        chkAtivo.setSelected(cliente.isActive());
        
        // Selecionar valores nos ComboBox
        if (cliente.getHairType() != null) {
            cbHairType.setValue(cliente.getHairType());
        }
        if (cliente.getHairTexture() != null) {
            cbHairTexture.setValue(cliente.getHairTexture());
        }
        if (cliente.getScalp() != null) {
            cbScalp.setValue(cliente.getScalp());
        }
    }

    @FXML
    public void initialize() {
        // Preencher ComboBoxes
        cbHairType.getItems().addAll("Liso", "Ondulado", "Cacheado", "Crespo");
        cbHairTexture.getItems().addAll("Fino", "Médio", "Grosso");
        cbScalp.getItems().addAll("Normal", "Oleoso", "Seco", "Misto", "Sensível");
        
        // Formatação automática do telefone
        txtTelefone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String formatted = formatPhone(newValue);
                if (!formatted.equals(newValue)) {
                    txtTelefone.setText(formatted);
                    txtTelefone.positionCaret(formatted.length());
                }
            }
        });

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
            String address = txtAddress.getText().trim();
            String allergies = txtAllergies.getText().trim();
            String observations = txtObservations.getText().trim();

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
            cliente.setAddress(address.isEmpty() ? null : address);
            cliente.setHairType(cbHairType.getValue());
            cliente.setHairTexture(cbHairTexture.getValue());
            cliente.setScalp(cbScalp.getValue());
            cliente.setAllergies(allergies.isEmpty() ? null : allergies);
            cliente.setObservations(observations.isEmpty() ? null : observations);
            cliente.setActive(chkAtivo.isSelected());

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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    /**
     * Formata o telefone automaticamente enquanto o usuário digita
     * Formato: (00) 00000-0000 ou (00) 0000-0000
     */
    private String formatPhone(String phone) {
        // Remove tudo que não é número
        String numbers = phone.replaceAll("[^0-9]", "");
        
        // Limita a 11 dígitos
        if (numbers.length() > 11) {
            numbers = numbers.substring(0, 11);
        }
        
        // Aplica a formatação progressiva
        StringBuilder formatted = new StringBuilder();
        
        if (numbers.length() > 0) {
            formatted.append("(");
            formatted.append(numbers.substring(0, Math.min(2, numbers.length())));
            
            if (numbers.length() > 2) {
                formatted.append(") ");
                
                if (numbers.length() <= 6) {
                    // (00) 0000
                    formatted.append(numbers.substring(2));
                } else if (numbers.length() <= 10) {
                    // (00) 0000-0000
                    formatted.append(numbers.substring(2, 6));
                    formatted.append("-");
                    formatted.append(numbers.substring(6));
                } else {
                    // (00) 00000-0000
                    formatted.append(numbers.substring(2, 7));
                    formatted.append("-");
                    formatted.append(numbers.substring(7));
                }
            }
        }
        
        return formatted.toString();
    }
}
