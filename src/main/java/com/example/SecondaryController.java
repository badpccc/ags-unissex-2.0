package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class SecondaryController {

    @FXML private HBox menuClientes;
    @FXML private HBox menuAgendamentos;
    @FXML private HBox menuServicos;
    @FXML private HBox menuFinancas;   // <-- ADICIONADO
    @FXML private HBox menuLogout;

    @FXML private StackPane conteudoArea;

    @FXML
    private void initialize() {
        carregarTela("dashboard"); // Tela inicial, opcional
    }
    @FXML
    private void showDashboard(MouseEvent e) {
        carregarTela("dashboard");
    }

    @FXML
    private void showClientes(MouseEvent e) {
        carregarTela("clientes");
    }

    @FXML
    private void showAgendamentos(MouseEvent e) {
        carregarTela("agendamentos");
    }

    @FXML
    private void showServicos(MouseEvent e) {
        carregarTela("servicos");
    }

    @FXML
    private void showFinancas(MouseEvent e) {   // <-- NOVO MÉTODO
        carregarTela("financas");
    }

    @FXML
    private void handleLogout(MouseEvent e) throws IOException {
        App.setRoot("primary");
    }
    @FXML
    private void showUsuarios() {
        carregarTela("usuarios"); // usa usuarios.fxml
    }


    private void carregarTela(String nome) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/" + nome + ".fxml"));
            conteudoArea.getChildren().setAll(root);
        } catch (IOException e) {
            System.out.println("Erro ao carregar: " + nome + ".fxml");
            e.printStackTrace();
        }
    }

}
