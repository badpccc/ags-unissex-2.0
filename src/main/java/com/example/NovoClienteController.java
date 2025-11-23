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

    // --- LABELS DE ERRO ---
    @FXML private Label lblErroNome;
    @FXML private Label lblErroTelefone;
    @FXML private Label lblErroEmail;
    @FXML private Label lblErroEndereco;

    // --- CAMPOS CAPILARES ---
    @FXML private ComboBox<String> cbHairType;
    @FXML private ComboBox<String> cbHairTexture;
    @FXML private ComboBox<String> cbScalp;

    @FXML private Label lblErroHairType;
    @FXML private Label lblErroHairTexture;
    @FXML private Label lblErroScalp;

    // --- OUTROS CAMPOS ---
    @FXML private TextField txtAllergies;
    @FXML private DatePicker dpLastVisit;
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

        // Limpar erro ao digitar
        adicionarLimpezaDeErro(txtNome, lblErroNome);
        adicionarLimpezaDeErro(txtTelefone, lblErroTelefone);
        adicionarLimpezaDeErro(txtEmail, lblErroEmail);
        adicionarLimpezaDeErro(txtAddress, lblErroEndereco);
        adicionarLimpezaDeErro(cbHairType, lblErroHairType);
        adicionarLimpezaDeErro(cbHairTexture, lblErroHairTexture);
        adicionarLimpezaDeErro(cbScalp, lblErroScalp);

        // Permitir apenas letras no nome
        txtNome.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZÀ-ÿ ]*")) {
                txtNome.setText(newValue.replaceAll("[^a-zA-ZÀ-ÿ ]", ""));
            }
        });

        // Populando ComboBoxes
        cbHairType.setItems(javafx.collections.FXCollections.observableArrayList(
                "Liso", "Ondulado", "Cacheado", "Crespo"
        ));
        cbHairTexture.setItems(javafx.collections.FXCollections.observableArrayList(
                "Fino", "Médio", "Grosso"
        ));
        cbScalp.setItems(javafx.collections.FXCollections.observableArrayList(
                "Oleoso", "Seco", "Misto", "Sensível"
        ));

        btnSalvar.setOnAction(e -> salvar());
        btnCancelar.setOnAction(e -> fechar());
    }



    // ===============================================================
    // FUNÇÃO PARA LIMPAR ERRO AO DIGITAR / MODIFICAR
    // ===============================================================
    private void adicionarLimpezaDeErro(Control campo, Label erroLabel) {
        if (campo instanceof TextField tf) {
            tf.textProperty().addListener((obs, oldValue, newValue) -> removerErro(campo, erroLabel));
        } else if (campo instanceof ComboBox<?> cb) {
            cb.valueProperty().addListener((obs, oldValue, newValue) -> removerErro(campo, erroLabel));
        }
    }


    private void removerErro(Control campo, Label erro) {
        campo.getStyleClass().remove("error");
        erro.setText("");
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
    // VALIDAR CAMPOS
    // ===============================================================
    private boolean validar() {
        boolean valido = true;

        // Nome
        if (txtNome.getText().trim().isEmpty()) {
            mostrarErro(txtNome, lblErroNome, "O nome é obrigatório.");
            valido = false;
        }

        // Telefone
        if (txtTelefone.getText().trim().length() < 14) {
            mostrarErro(txtTelefone, lblErroTelefone, "Telefone inválido.");
            valido = false;
        }

        // Email
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            mostrarErro(txtEmail, lblErroEmail, "E-mail inválido.");
            valido = false;
        }

        // Endereço
        if (txtAddress.getText().trim().isEmpty()) {
            mostrarErro(txtAddress, lblErroEndereco, "O endereço é obrigatório.");
            valido = false;
        }

        // Capilares
        if (cbHairType.getValue() == null) {
            mostrarErro(cbHairType, lblErroHairType, "Selecione o tipo de cabelo.");
            valido = false;
        }

        if (cbHairTexture.getValue() == null) {
            mostrarErro(cbHairTexture, lblErroHairTexture, "Selecione a textura.");
            valido = false;
        }

        if (cbScalp.getValue() == null) {
            mostrarErro(cbScalp, lblErroScalp, "Selecione o couro cabeludo.");
            valido = false;
        }

        return valido;
    }


    private void mostrarErro(Control campo, Label erro, String msg) {
        if (!campo.getStyleClass().contains("error")) {
            campo.getStyleClass().add("error");
        }
        erro.setText(msg);
    }


    // ===============================================================
    // SALVAR CLIENTE
    // ===============================================================
    private void salvar() {

        if (!validar()) {
            System.out.println("Erro: falha na validação.");
            return;
        }

        Client cliente = new Client();

        // CAMPOS BÁSICOS
        cliente.setName(txtNome.getText());
        cliente.setPhoneNumber(txtTelefone.getText());
        cliente.setEmail(txtEmail.getText());
        cliente.setAddress(txtAddress.getText());
        cliente.setActive(chkAtivo.isSelected());
        cliente.setRegistrationDate(LocalDate.now());

        // CAPILARES
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

        if (onClienteSalvo != null)
            onClienteSalvo.accept(cliente);

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
