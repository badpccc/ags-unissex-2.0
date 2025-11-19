package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import com.example.backends.database.connection.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private VBox homeContent;

    @FXML
    private Label lblTotalClientes;

    @FXML
    private Label lblTotalAgendamentos;

    @FXML
    private Label lblTotalServicos;


    @FXML
    public void initialize() {
        carregarDadosDashboard();
    }

    private void carregarDadosDashboard() {
        lblTotalClientes.setText(String.valueOf(getTotalClientes()));
        lblTotalAgendamentos.setText(String.valueOf(getTotalAgendamentos()));
        lblTotalServicos.setText(String.valueOf(getTotalServicos()));
    }

    private int getTotalClientes() {
        String sql = "SELECT COUNT(*) FROM clients WHERE is_active = true";

        try (Connection conn = Connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            System.err.println("Erro ao contar clientes: " + e.getMessage());
            return 0;
        }
    }

    private int getTotalAgendamentos() {
        String sql = "SELECT COUNT(*) FROM appointments"; // Ajuste se o nome da tabela for diferente

        try (Connection conn = Connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            System.err.println("Erro ao contar agendamentos: " + e.getMessage());
            return 0;
        }
    }

    private int getTotalServicos() {
        String sql = "SELECT COUNT(*) FROM services"; // Ajuste se sua tabela tiver outro nome

        try (Connection conn = Connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            System.err.println("Erro ao contar serviços: " + e.getMessage());
            return 0;
        }
    }
}
