package com.example.backends.database.data;

import java.sql.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.backends.classes.Appointment;
import com.example.backends.enums.AppointmentStatus;
import com.example.backends.database.connection.Connect;


public class AppointmentDAO {
    

    public static boolean insert(Appointment appointment) {
        String sql = """
            INSERT INTO appointments (
                client_id, stylist_id, appointment_date_time, status, 
                total_price, notes
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, appointment.getClientId());
            
            if (appointment.getStylistId() != null) {
                pstmt.setLong(2, appointment.getStylistId());
            } else {
                pstmt.setNull(2, Types.BIGINT);
            }
            
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            
            String statusString = appointment.getStatus() != null ? 
                appointment.getStatus().name() : AppointmentStatus.AGENDADO.name();
            pstmt.setString(4, statusString);
            
            if (appointment.getTotalPrice() != null) {
                pstmt.setBigDecimal(5, appointment.getTotalPrice());
            } else {
                pstmt.setNull(5, Types.DECIMAL);
            }
            
            pstmt.setString(6, appointment.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getLong(1));
                    }
                }
            }
            
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir agendamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static boolean update(Appointment appointment) {
        String sql = """
            UPDATE appointments SET 
                client_id = ?, stylist_id = ?, appointment_date_time = ?, status = ?,
                total_price = ?, notes = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, appointment.getClientId());
            
            if (appointment.getStylistId() != null) {
                pstmt.setLong(2, appointment.getStylistId());
            } else {
                pstmt.setNull(2, Types.BIGINT);
            }
            
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            
            String statusString = appointment.getStatus() != null ? 
                appointment.getStatus().name() : AppointmentStatus.AGENDADO.name();
            pstmt.setString(4, statusString);
            
            if (appointment.getTotalPrice() != null) {
                pstmt.setBigDecimal(5, appointment.getTotalPrice());
            } else {
                pstmt.setNull(5, Types.DECIMAL);
            }
            
            pstmt.setString(6, appointment.getNotes());
            pstmt.setLong(7, appointment.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar agendamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static boolean delete(Long appointmentID) {
        String sql = "DELETE FROM appointments WHERE id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, appointmentID);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover agendamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static Appointment getAppointmentByID(Long appointmentID) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, appointmentID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar agendamento por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date_time DESC";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar agendamentos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    

    public static List<Appointment> getAppointmentsByClient(Long clientID) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE client_id = ? ORDER BY appointment_date_time DESC";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, clientID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar agendamentos por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }

    public static List<Appointment> getAppointmentsByStylist(Long stylistID) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE stylist_id = ? ORDER BY appointment_date_time DESC";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, stylistID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar agendamentos por funcionário: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    public static List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE status = ? ORDER BY appointment_date_time DESC";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar agendamentos por status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    

    public static List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date_time BETWEEN ? AND ? ORDER BY appointment_date_time";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar agendamentos por período: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    

    public static List<Appointment> getTodayAppointments() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        
        return getAppointmentsByDateRange(startOfDay, endOfDay);
    }
    
    public static boolean updateStatus(Long appointmentID, AppointmentStatus newStatus) {
        String sql = "UPDATE appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus.name());
            pstmt.setLong(2, appointmentID);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status do agendamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean addServicesToAppointment(Long appointmentID, List<Long> serviceIDs, List<BigDecimal> servicePrices) {
        if (serviceIDs == null || servicePrices == null || serviceIDs.size() != servicePrices.size()) {
            return false;
        }
        
        String sql = "INSERT INTO appointment_services (appointment_id, service_id, service_price) VALUES (?, ?, ?)";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < serviceIDs.size(); i++) {
                pstmt.setLong(1, appointmentID);
                pstmt.setLong(2, serviceIDs.get(i));
                pstmt.setBigDecimal(3, servicePrices.get(i));
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar serviços ao agendamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean removeServicesFromAppointment(Long appointmentID) {
        String sql = "DELETE FROM appointment_services WHERE appointment_id = ?";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, appointmentID);
            pstmt.executeUpdate(); // Não precisa verificar affectedRows, pode ser 0
            return true;
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover serviços do agendamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Long> getServiceIDsByAppointment(Long appointmentID) {
        List<Long> serviceIDs = new ArrayList<>();
        String sql = "SELECT service_id FROM appointment_services WHERE appointment_id = ?";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, appointmentID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    serviceIDs.add(rs.getLong("service_id"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar serviços do agendamento: " + e.getMessage());
            e.printStackTrace();
        }
        
        return serviceIDs;
    }
    

    private static Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        
        appointment.setId(rs.getLong("id"));
        appointment.setClientId(rs.getLong("client_id"));
        
        long stylistId = rs.getLong("stylist_id");
        if (!rs.wasNull()) {
            appointment.setStylistId(stylistId);
        }
        
        Timestamp appointmentTs = rs.getTimestamp("appointment_date_time");
        if (appointmentTs != null) {
            appointment.setAppointmentDateTime(appointmentTs.toLocalDateTime());
        }
        
        String statusString = rs.getString("status");
        if (statusString != null) {
            try {
                AppointmentStatus status = AppointmentStatus.valueOf(statusString);
                appointment.setStatus(status);
            } catch (IllegalArgumentException e) {
                appointment.setStatus(AppointmentStatus.AGENDADO);
            }
        }
        
        BigDecimal totalPrice = rs.getBigDecimal("total_price");
        if (totalPrice != null) {
            appointment.setTotalPrice(totalPrice);
        }
        
        appointment.setNotes(rs.getString("notes"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            appointment.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            appointment.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        List<Long> serviceIDs = getServiceIDsByAppointment(appointment.getId());
        appointment.setServiceIds(serviceIDs);
        
        return appointment;
    }
}