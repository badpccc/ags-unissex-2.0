package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.example.backends.classes.Client; // <-- IMPORT QUE FALTAVA

public class DetalhesClienteController {

    @FXML private Label lblNome;
    @FXML private Label lblStatus;
    @FXML private Label lblTelefone;
    @FXML private Label lblEmail;
    @FXML private Label lblEndereco;
    @FXML private Label lblCabelo;
    @FXML private Label lblTextura;
    @FXML private Label lblCouro;
    @FXML private Label lblAlergias;
    @FXML private Label lblUltimaVisita;
    @FXML private Label lblObservacoes;

    public void setCliente(Client c) {
        lblNome.setText(c.getName());
        
        // Configurar status com cores
        if (c.isActive()) {
            lblStatus.setText("ATIVO");
            lblStatus.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            lblStatus.setText("NÃO ATIVO");
            lblStatus.setStyle("-fx-text-fill: #FF4444; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
        
        lblTelefone.setText("Telefone: " + c.getPhoneNumber());
        lblEmail.setText("Email: " + c.getEmail());
        lblEndereco.setText("Endereço: " + c.getAddress());
        lblCabelo.setText("Tipo de cabelo: " + c.getHairType());
        lblTextura.setText("Textura: " + c.getHairTexture());
        lblCouro.setText("Couro cabeludo: " + c.getScalp());
        lblAlergias.setText("Alergias: " + (c.getAllergies() != null ? c.getAllergies() : "Nenhuma"));
        lblUltimaVisita.setText("Última visita: " + (c.getLastVisit() != null ? c.getLastVisit() : "—"));
        lblObservacoes.setText("Observações: " + (c.getObservations() != null ? c.getObservations() : "—"));
    }

    @FXML
    private void fechar() {
        ((Stage) lblNome.getScene().getWindow()).close();
    }
}
