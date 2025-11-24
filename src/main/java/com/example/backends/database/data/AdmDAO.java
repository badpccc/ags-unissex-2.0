package com.example.backends.database.data;

import com.example.backends.classes.Adm;
import com.example.backends.database.connection.Connect;

import java.sql.*;

public class AdmDAO {

    // ============================================================
    // INSERT
    // ============================================================
    public static boolean insert(Adm adm) {
        String sql = """
            INSERT INTO adm (
                full_name, username, password_hash,
                email, phone_number, notes
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Connect.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                System.out.println("=== INSERINDO ADMINISTRADOR ===");
                System.out.println("Nome: " + adm.getFullName());
                System.out.println("Username: " + adm.getUsername());
                System.out.println("Email: " + adm.getEmail());
                System.out.println("===============================");

                pstmt.setString(1, adm.getFullName());
                pstmt.setString(2, adm.getUsername());
                pstmt.setString(3, adm.getPasswordHash());
                pstmt.setString(4, adm.getEmail());
                pstmt.setString(5, adm.getPhoneNumber());
                pstmt.setString(6, adm.getNotes());

                int affected = pstmt.executeUpdate();
                conn.commit();

                if (affected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            adm.setId(rs.getLong(1));
                        }
                    }
                }

                return affected > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir administrador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ============================================================
    // UPDATE
    // ============================================================
    public static boolean update(Adm adm) {
        String sql = """
            UPDATE adm SET
                full_name = ?, username = ?, password_hash = ?,
                email = ?, phone_number = ?, notes = ?, is_active = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        try (Connection conn = Connect.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, adm.getFullName());
                pstmt.setString(2, adm.getUsername());
                pstmt.setString(3, adm.getPasswordHash());
                pstmt.setString(4, adm.getEmail());
                pstmt.setString(5, adm.getPhoneNumber());
                pstmt.setString(6, adm.getNotes());
                pstmt.setBoolean(7, adm.isActive());
                pstmt.setLong(8, adm.getId());

                int affected = pstmt.executeUpdate();
                conn.commit();

                return affected > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar administrador: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // DELETE
    // ============================================================
    public static boolean delete(Long admID) {
        String sql = "DELETE FROM adm WHERE id = ?";

        try (Connection conn = Connect.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, admID);

                int affected = pstmt.executeUpdate();
                conn.commit();
                return affected > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao remover administrador: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // GET BY ID
    // ============================================================
    public static Adm getAdmByID(Long id) {
        String sql = "SELECT * FROM adm WHERE id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar adm por ID: " + e.getMessage());
        }

        return null;
    }
    
    // ============================================================
    // GET ALL
    // ============================================================
    public static java.util.List<Adm> getAll() {
        String sql = "SELECT * FROM adm ORDER BY full_name";
        java.util.List<Adm> lista = new java.util.ArrayList<>();

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os administradores: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    // ============================================================
    // LOGIN → buscar por username
    // ============================================================
    public static Adm getByUsername(String username) {
        String sql = "SELECT * FROM adm WHERE username = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar adm por username: " + e.getMessage());
        }

        return null;
    }

    // ============================================================
    // FUNÇÃO ESPECIAL SOLICITADA
    // ============================================================
    public static boolean hasAdmin() {
        String sql = "SELECT COUNT(*) AS total FROM adm";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("Quantidade de administradores cadastrados: " + total);
                return total > 0;  
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar administradores: " + e.getMessage());
        }

        return false;
    }

    // ============================================================
    // MAP RESULTSET → Adm
    // ============================================================
    private static Adm map(ResultSet rs) throws SQLException {
        Adm adm = new Adm();

        adm.setId(rs.getLong("id"));
        adm.setFullName(rs.getString("full_name"));
        adm.setUsername(rs.getString("username"));
        adm.setPasswordHash(rs.getString("password_hash"));
        adm.setEmail(rs.getString("email"));
        adm.setPhoneNumber(rs.getString("phone_number"));
        adm.setNotes(rs.getString("notes"));
        adm.setActive(rs.getBoolean("is_active"));

        Timestamp lastLoginTs = rs.getTimestamp("last_login");
        if (lastLoginTs != null) adm.setLastLogin(lastLoginTs.toLocalDateTime());

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) adm.setCreatedAt(createdTs.toLocalDateTime());

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) adm.setUpdatedAt(updatedTs.toLocalDateTime());

        return adm;
    }
}
