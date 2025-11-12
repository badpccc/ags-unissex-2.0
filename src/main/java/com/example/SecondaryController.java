package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class SecondaryController {

    @FXML private Button menuClientes;
    @FXML private Button menuAgendamentos;
    @FXML private Button menuServicos;
    @FXML private StackPane conteudoArea;

    @FXML
    private void initialize() {
        // Carrega uma tela inicial (opcional)
        carregarTela("clientes");

        // Eventos dos botões
        menuClientes.setOnAction(e -> carregarTela("clientes"));
        menuAgendamentos.setOnAction(e -> carregarTela("agendamentos"));
        menuServicos.setOnAction(e -> carregarTela("servicos"));
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    private void carregarTela(String nome) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(nome + ".fxml"));
            conteudoArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
