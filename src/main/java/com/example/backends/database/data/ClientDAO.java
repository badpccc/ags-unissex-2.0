package com.example.backends.database.data;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.backends.classes.Client;
import com.example.backends.database.connection.Connect;

public class ClientDAO {
    

    public static boolean insert(Client client) {
        String sql = """
            INSERT INTO clients (
                name, email, phone_number, address, notes,
                hair_type, hair_texture, scalp, allergies,
                observations
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Connect.getConnection()) {
            // Forçar transação manual
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
                System.out.println("=== DADOS SENDO INSERIDOS ===");
                System.out.println("1. Nome: '" + client.getName() + "'");
                System.out.println("2. Email: '" + client.getEmail() + "'");
                System.out.println("3. Telefone: '" + client.getPhoneNumber() + "'");
                System.out.println("4. Endereço: '" + client.getAddress() + "'");
                System.out.println("5. Notas: '" + client.getNotes() + "'");
                System.out.println("6. Tipo cabelo: '" + client.getHairType() + "'");
                System.out.println("7. Textura: '" + client.getHairTexture() + "'");
                System.out.println("8. Couro cabeludo: '" + client.getScalp() + "'");
                System.out.println("9. Alergias: '" + client.getAllergies() + "'");
                System.out.println("10. Observações: '" + client.getObservations() + "'");
                System.out.println("===============================");
                
                pstmt.setString(1, client.getName());
                pstmt.setString(2, client.getEmail());
                pstmt.setString(3, client.getPhoneNumber());
                pstmt.setString(4, client.getAddress());
                pstmt.setString(5, client.getNotes());
                
                pstmt.setString(6, client.getHairType());
                pstmt.setString(7, client.getHairTexture());
                pstmt.setString(8, client.getScalp());
                pstmt.setString(9, client.getAllergies());
                pstmt.setString(10, client.getObservations());
                
                System.out.println("Executando INSERT...");
                int affectedRows = pstmt.executeUpdate();
                System.out.println("Linhas afetadas: " + affectedRows);
                
                // Confirmar a transação
                conn.commit();
                System.out.println("Transação de inserção commitada!");
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            client.setId(generatedKeys.getLong(1));
                        }
                    }
                }
                
                return affectedRows > 0;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("=== ERRO DETALHADO NA INSERÇÃO ===");
            System.err.println("Erro SQL: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("====================================");
            e.printStackTrace();
            return false;
        }
    }


    public static boolean update(Client client) {
        String sql = """
            UPDATE clients SET 
                name = ?, email = ?, phone_number = ?, address = ?, notes = ?,
                hair_type = ?, hair_texture = ?, scalp = ?, allergies = ?,
                observations = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ? AND is_active = true
            """;

        try (Connection conn = Connect.getConnection()) {
            // Forçar transação manual
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setString(1, client.getName());
                pstmt.setString(2, client.getEmail());
                pstmt.setString(3, client.getPhoneNumber());
                pstmt.setString(4, client.getAddress());
                pstmt.setString(5, client.getNotes());
                
                pstmt.setString(6, client.getHairType());
                pstmt.setString(7, client.getHairTexture());
                pstmt.setString(8, client.getScalp());
                pstmt.setString(9, client.getAllergies());
                pstmt.setString(10, client.getObservations());
                
                pstmt.setLong(11, client.getId());
                
                int affectedRows = pstmt.executeUpdate();
                
                // Confirmar a transação
                conn.commit();
                
                return affectedRows > 0;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static boolean delete(Long clientID) {
        String sql = "DELETE FROM clients WHERE id = ?";

        System.out.println("=== TENTANDO EXCLUIR CLIENTE ===");
        System.out.println("ID para excluir: " + clientID);

        try (Connection conn = Connect.getConnection()) {
            // Forçar transação manual
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, clientID);
                int affectedRows = pstmt.executeUpdate();
                
                // Confirmar a transação
                conn.commit();
                
                System.out.println("Linhas afetadas: " + affectedRows);
                System.out.println("Exclusão " + (affectedRows > 0 ? "SUCESSO" : "FALHOU"));
                System.out.println("Transação commitada!");
                
                return affectedRows > 0;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Client getClientByID(Long clientID) {
        String sql = "SELECT * FROM clients WHERE id = ? AND is_active = true";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, clientID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClient(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }


    public static List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients ORDER BY name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clients;
    }
    

    public static List<Client> getActiveClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE is_active = true ORDER BY name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes ativos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clients;
    }
    

    public static List<Client> getClientsByName(String name) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE LOWER(name) LIKE LOWER(?) AND is_active = true ORDER BY name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(mapResultSetToClient(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar clientes por nome: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clients;
    }
    

    public static Client getClientByPhone(String phoneNumber) {
        String sql = "SELECT * FROM clients WHERE phone_number = ? AND is_active = true";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phoneNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClient(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por telefone: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public static boolean updateLastVisit(Long clientID, LocalDateTime lastVisit) {
        String sql = "UPDATE clients SET last_visit = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = Connect.getConnection()) {
            // Forçar transação manual
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setTimestamp(1, Timestamp.valueOf(lastVisit));
                pstmt.setLong(2, clientID);
                
                int affectedRows = pstmt.executeUpdate();
                
                // Confirmar a transação
                conn.commit();
                
                return affectedRows > 0;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar última visita: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static Client mapResultSetToClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        
        client.setId(rs.getLong("id"));
        client.setName(rs.getString("name"));
        client.setEmail(rs.getString("email"));
        client.setPhoneNumber(rs.getString("phone_number"));
        client.setAddress(rs.getString("address"));
        
        Date regDate = rs.getDate("registration_date");
        if (regDate != null) {
            client.setRegistrationDate(regDate.toLocalDate());
        }
        
        client.setActive(rs.getBoolean("is_active"));
        client.setNotes(rs.getString("notes"));
        
        client.setHairType(rs.getString("hair_type"));
        client.setHairTexture(rs.getString("hair_texture"));
        client.setScalp(rs.getString("scalp"));
        client.setAllergies(rs.getString("allergies"));
        client.setObservations(rs.getString("observations"));
        
        Timestamp lastVisitTs = rs.getTimestamp("last_visit");
        if (lastVisitTs != null) {
            client.setLastVisit(lastVisitTs.toLocalDateTime());
        }
        
        return client;
    }

}