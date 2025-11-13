package com.example.dao;

import com.example.models.Agendamento;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/local_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234"; // ajuste conforme seu ambiente

    private static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ===============================
    // LISTAR AGENDAMENTOS
    // ===============================
    public static List<Agendamento> listar() {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM agendamentos ORDER BY data_agendamento DESC, hora DESC";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Agendamento ag = new Agendamento(
                        rs.getInt("id"),
                        rs.getString("cliente_nome"),
                        rs.getString("servico"),
                        rs.getDate("data_agendamento").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getString("status"),
                        rs.getString("observacao")
                );
                lista.add(ag);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ===============================
    // SALVAR AGENDAMENTO
    // ===============================
    public static void salvar(Agendamento ag) {
        String sql = """
            INSERT INTO agendamentos (cliente_nome, servico, data_agendamento, hora, status, observacao)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ag.getClienteNome());
            stmt.setString(2, ag.getServico());
            stmt.setDate(3, Date.valueOf(ag.getDataAgendamento()));
            stmt.setTime(4, Time.valueOf(ag.getHora()));
            stmt.setString(5, ag.getStatus());
            stmt.setString(6, ag.getObservacoes());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // ATUALIZAR AGENDAMENTO
    // ===============================
    public static void atualizar(Agendamento ag) {
        String sql = """
            UPDATE agendamentos 
            SET cliente_nome = ?, servico = ?, data_agendamento = ?, hora = ?, status = ?, observacao = ?
            WHERE id = ?
        """;

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ag.getClienteNome());
            stmt.setString(2, ag.getServico());
            stmt.setDate(3, Date.valueOf(ag.getDataAgendamento()));
            stmt.setTime(4, Time.valueOf(ag.getHora()));
            stmt.setString(5, ag.getStatus());
            stmt.setString(6, ag.getObservacoes());
            stmt.setInt(7, ag.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // EXCLUIR AGENDAMENTO
    // ===============================
    public static void excluir(int id) {
        String sql = "DELETE FROM agendamentos WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // BUSCAR POR ID
    // ===============================
    public static Agendamento buscarPorId(int id) {
        String sql = "SELECT * FROM agendamentos WHERE id = ?";
        Agendamento ag = null;

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ag = new Agendamento(
                        rs.getInt("id"),
                        rs.getString("cliente_nome"),
                        rs.getString("servico"),
                        rs.getDate("data_agendamento").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getString("status"),
                        rs.getString("observacao")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ag;
    }
}
