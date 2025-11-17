package com.example;

import com.example.backends.classes.Client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.function.Consumer;

public class NovoClienteController {

    // --- CAMPOS BÁSICOS ---
    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;

    @FXML private CheckBox chkAtivo;

    // --- CAMPOS CAPILARES ---
    @FXML private ComboBox<String> cbHairType;
    @FXML private ComboBox<String> cbHairTexture;
    @FXML private ComboBox<String> cbScalp;

    @FXML private TextField txtAllergies;

    @FXML private DatePicker dpLastVisit;

    // --- OBSERVAÇÕES ---
    @FXML private TextArea txtObservations;

    // --- BOTÕES ---
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private Consumer<Client> onClienteSalvo;



    // ===============================================================
    // INITIALIZE
    // ===============================================================
    @FXML
    public void initialize() {


        aplicarMascaraTelefone(txtTelefone);

        // Permitir apenas letras no nome
        txtNome.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZÀ-ÿ ]*")) {
                txtNome.setText(newValue.replaceAll("[^a-zA-ZÀ-ÿ ]", ""));
            }
        });

        // Botões
        btnSalvar.setOnAction(e -> salvar());
        btnCancelar.setOnAction(e -> fechar());
    }



    // ===============================================================
    // MÁSCARA DE TELEFONE
    // ===============================================================
    private void aplicarMascaraTelefone(TextField campo) {
        campo.textProperty().addListener((obs, oldValue, newValue) -> {

            String digits = newValue.replaceAll("[^0-9]", "");

            if (digits.length() > 11)
                digits = digits.substring(0, 11);

            StringBuilder formatado = new StringBuilder();
            int len = digits.length();

            if (len > 0)
                formatado.append("(").append(digits.substring(0, Math.min(2, len)));

            if (len >= 3)
                formatado.append(") ").append(digits.substring(2, Math.min(7, len)));

            if (len >= 8)
                formatado.append("-").append(digits.substring(7));

            campo.setText(formatado.toString());
            campo.positionCaret(formatado.length());
        });
    }



    // ===============================================================
    // SALVAR CLIENTE
    // ===============================================================
    private void salvar() {
        System.out.println("=== INICIANDO SALVAMENTO ===");

        String nome = txtNome.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();

        System.out.println("Dados coletados - Nome: " + nome + ", Telefone: " + telefone + ", Email: " + email);

        // Validação simples
        if (nome.isEmpty() || telefone.isEmpty()) {
            System.out.println("ERRO: Nome e telefone são obrigatórios!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println("ERRO: Email inválido!");
            return;
        }

        System.out.println("Validação passou. Criando objeto Client...");

        Client cliente = new Client();

        // CAMPOS BÁSICOS
        cliente.setName(nome);
        cliente.setPhoneNumber(telefone);
        cliente.setEmail(email);
        cliente.setAddress(txtAddress.getText());
        cliente.setActive(chkAtivo.isSelected());
        cliente.setRegistrationDate(LocalDate.now());

        // CAMPOS CAPILARES
        cliente.setHairType(cbHairType.getValue());
        cliente.setHairTexture(cbHairTexture.getValue());
        cliente.setScalp(cbScalp.getValue());
        cliente.setAllergies(txtAllergies.getText());
        cliente.setLastVisit(
                dpLastVisit.getValue() != null
                        ? dpLastVisit.getValue().atStartOfDay()
                        : null
        );

        // OBSERVAÇÕES
        cliente.setObservations(txtObservations.getText());

        // RETORNAR O CLIENTE PARA O CONTROLLER PRINCIPAL
        System.out.println("Cliente criado com sucesso. Executando callback...");
        if (onClienteSalvo != null)
            onClienteSalvo.accept(cliente);
        else
            System.out.println("ERRO: Callback não foi definido!");

        System.out.println("Fechando modal...");
        fechar();
    }


    // ===============================================================
    // FECHAR MODAL
    // ===============================================================
    private void fechar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }


    // ===============================================================
    // CALLBACK
    // ===============================================================
    public void setOnClienteSalvo(Consumer<Client> callback) {
        this.onClienteSalvo = callback;
    }
}
