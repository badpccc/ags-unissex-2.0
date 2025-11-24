package com.example.backends.database.data;

import java.sql.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.example.backends.classes.Service;
import com.example.backends.database.connection.Connect;


public class ServicesDAO {
    

    public static boolean insert(Service service) {
        String sql = """
            INSERT INTO services (
                name, description, price, duration_minutes, category
            ) VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Configura transação manual
            conn.setAutoCommit(false);
            
            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setBigDecimal(3, service.getPrice());
            
            int durationMinutes = 0;
            if (service.getDuration() != null) {
                durationMinutes = (int) service.getDuration().toMinutes();
            }
            pstmt.setInt(4, durationMinutes);
            
            pstmt.setString(5, service.getCategory());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linhas afetadas na inserção de serviço: " + affectedRows);
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        service.setId(generatedKeys.getLong(1));
                    }
                }
                
                conn.commit();
                System.out.println("Transação commitada! Serviço inserido com sucesso.");
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir serviço: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static boolean update(Service service) {
        String sql = """
            UPDATE services SET 
                name = ?, description = ?, price = ?, duration_minutes = ?, category = ?,
                is_active = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Configura transação manual
            conn.setAutoCommit(false);
            
            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setBigDecimal(3, service.getPrice());
            
            // Converte Duration para minutos
            int durationMinutes = 0;
            if (service.getDuration() != null) {
                durationMinutes = (int) service.getDuration().toMinutes();
            }
            pstmt.setInt(4, durationMinutes);
            
            pstmt.setString(5, service.getCategory());
            pstmt.setBoolean(6, service.isActive());
            pstmt.setLong(7, service.getId());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linhas afetadas na atualização de serviço: " + affectedRows);
            
            if (affectedRows > 0) {
                conn.commit();
                System.out.println("Transação commitada! Serviço atualizado com sucesso.");
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar serviço: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static boolean delete(Long serviceID) {
        String sql = "UPDATE services SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Configura transação manual
            conn.setAutoCommit(false);
            
            pstmt.setLong(1, serviceID);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linhas afetadas na remoção de serviço: " + affectedRows);
            
            if (affectedRows > 0) {
                conn.commit();
                System.out.println("Transação commitada! Serviço removido com sucesso.");
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover serviço: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static Service getServiceByID(Long serviceID) {
        String sql = "SELECT * FROM services WHERE id = ? AND is_active = true";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, serviceID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToService(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar serviço por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    

    public static List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services ORDER BY is_active DESC, category, name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar serviços: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    

    public static List<Service> getServicesByName(String name) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE LOWER(name) LIKE LOWER(?) AND is_active = true ORDER BY name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar serviços por nome: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    

    public static List<Service> getServicesByCategory(String category) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE LOWER(category) = LOWER(?) AND is_active = true ORDER BY name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar serviços por categoria: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    

    public static List<Service> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Service> services = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM services WHERE is_active = true");
        
        if (minPrice != null) {
            sqlBuilder.append(" AND price >= ?");
        }
        if (maxPrice != null) {
            sqlBuilder.append(" AND price <= ?");
        }
        sqlBuilder.append(" ORDER BY price, name");
        
        String sql = sqlBuilder.toString();

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (minPrice != null) {
                pstmt.setBigDecimal(paramIndex++, minPrice);
            }
            if (maxPrice != null) {
                pstmt.setBigDecimal(paramIndex, maxPrice);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar serviços por faixa de preço: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    

    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM services WHERE is_active = true AND category IS NOT NULL ORDER BY category";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    

    public static long countActiveServices() {
        String sql = "SELECT COUNT(*) FROM services WHERE is_active = true";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao contar serviços: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    

    private static Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        
        service.setId(rs.getLong("id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getBigDecimal("price"));
        
        // Converte minutos para Duration
        int durationMinutes = rs.getInt("duration_minutes");
        if (durationMinutes > 0) {
            service.setDuration(Duration.ofMinutes(durationMinutes));
        }
        
        service.setCategory(rs.getString("category"));
        service.setActive(rs.getBoolean("is_active"));
        
        return service;
    }
}