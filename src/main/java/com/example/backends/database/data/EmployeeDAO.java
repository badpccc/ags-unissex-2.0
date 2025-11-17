package com.example.backends.database.data;

import java.sql.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.backends.classes.Employee;
import com.example.backends.database.connection.Connect;


public class EmployeeDAO {
    

    public static boolean insert(Employee employee) {
        String sql = """
            INSERT INTO employees (
                name, email, phone_number, cpf, notes,
                specialties, experience_level, base_salary, commission_rate,
                working_hours, working_days, position, certificates,
                can_perform_chemical_treatments, preferred_client_type
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Configura transação manual
            conn.setAutoCommit(false);
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getPhoneNumber());
            pstmt.setString(4, employee.getCpf());
            pstmt.setString(5, employee.getNotes());
            
            pstmt.setString(6, employee.getSpecialties());
            pstmt.setString(7, employee.getExperienceLevel());
            
            if (employee.getBaseSalary() != null) {
                pstmt.setBigDecimal(8, employee.getBaseSalary());
            } else {
                pstmt.setNull(8, Types.DECIMAL);
            }
            
            if (employee.getCommissionRate() != null) {
                pstmt.setBigDecimal(9, employee.getCommissionRate());
            } else {
                pstmt.setNull(9, Types.DECIMAL);
            }
            
            pstmt.setString(10, employee.getWorkingHours());
            
            String workingDaysJson = convertListToJson(employee.getWorkingDays());
            pstmt.setString(11, workingDaysJson);
            
            pstmt.setString(12, employee.getPosition());
            pstmt.setString(13, employee.getCertificates());
            pstmt.setBoolean(14, employee.isCanPerformChemicalTreatments());
            pstmt.setString(15, employee.getPreferredClientType());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linhas afetadas na inserção de funcionário: " + affectedRows);
            
            // Pega o ID gerado e atualiza o objeto
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setId(generatedKeys.getLong(1));
                    }
                }
                
                conn.commit();
                System.out.println("Transação commitada! Funcionário inserido com sucesso.");
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir funcionário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static boolean update(Employee employee) {
        String sql = """
            UPDATE employees SET 
                name = ?, email = ?, phone_number = ?, cpf = ?, notes = ?,
                specialties = ?, experience_level = ?, base_salary = ?, commission_rate = ?,
                working_hours = ?, working_days = ?, position = ?, certificates = ?,
                can_perform_chemical_treatments = ?, preferred_client_type = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ? AND is_active = true
            """;

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Configura transação manual
            conn.setAutoCommit(false);
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getPhoneNumber());
            pstmt.setString(4, employee.getCpf());
            pstmt.setString(5, employee.getNotes());
            
            pstmt.setString(6, employee.getSpecialties());
            pstmt.setString(7, employee.getExperienceLevel());
            
            if (employee.getBaseSalary() != null) {
                pstmt.setBigDecimal(8, employee.getBaseSalary());
            } else {
                pstmt.setNull(8, Types.DECIMAL);
            }
            
            if (employee.getCommissionRate() != null) {
                pstmt.setBigDecimal(9, employee.getCommissionRate());
            } else {
                pstmt.setNull(9, Types.DECIMAL);
            }
            
            pstmt.setString(10, employee.getWorkingHours());
            
            String workingDaysJson = convertListToJson(employee.getWorkingDays());
            pstmt.setString(11, workingDaysJson);
            
            pstmt.setString(12, employee.getPosition());
            pstmt.setString(13, employee.getCertificates());
            pstmt.setBoolean(14, employee.isCanPerformChemicalTreatments());
            pstmt.setString(15, employee.getPreferredClientType());
            
            pstmt.setLong(16, employee.getId());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linhas afetadas na atualização de funcionário: " + affectedRows);
            
            if (affectedRows > 0) {
                conn.commit();
                System.out.println("Transação commitada! Funcionário atualizado com sucesso.");
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar funcionário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static boolean delete(Long employeeID) {
        String sql = "UPDATE employees SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Configura transação manual
            conn.setAutoCommit(false);
            
            pstmt.setLong(1, employeeID);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linhas afetadas na remoção de funcionário: " + affectedRows);
            
            if (affectedRows > 0) {
                conn.commit();
                System.out.println("Transação commitada! Funcionário removido com sucesso.");
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover funcionário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public static Employee getEmployeeByID(Long employeeID) {
        String sql = "SELECT * FROM employees WHERE id = ? AND is_active = true";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, employeeID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    

    public static List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE is_active = true ORDER BY name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionários: " + e.getMessage());
            e.printStackTrace();
        }
        
        return employees;
    }
    

    public static List<Employee> getEmployeesByName(String name) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE LOWER(name) LIKE LOWER(?) AND is_active = true ORDER BY name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionários por nome: " + e.getMessage());
            e.printStackTrace();
        }
        
        return employees;
    }
    

    public static Employee getEmployeeByCpf(String cpf) {
        String sql = "SELECT * FROM employees WHERE cpf = ? AND is_active = true";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpf);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário por CPF: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    

    public static List<Employee> getEmployeesByPosition(String position) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE LOWER(position) LIKE LOWER(?) AND is_active = true ORDER BY name";

        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + position + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionários por posição: " + e.getMessage());
            e.printStackTrace();
        }
        
        return employees;
    }
    

    public static boolean updateLastTraining(Long employeeID, LocalDateTime lastTrainingDate) {
        String sql = "UPDATE employees SET last_training_date = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = Connect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Configura transação manual
            conn.setAutoCommit(false);
            
            pstmt.setTimestamp(1, Timestamp.valueOf(lastTrainingDate));
            pstmt.setLong(2, employeeID);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Linhas afetadas na atualização de último treinamento: " + affectedRows);
            
            if (affectedRows > 0) {
                conn.commit();
                System.out.println("Transação commitada! Último treinamento atualizado com sucesso.");
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar último treinamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        
        // Campos básicos
        employee.setId(rs.getLong("id"));
        employee.setName(rs.getString("name"));
        employee.setEmail(rs.getString("email"));
        employee.setPhoneNumber(rs.getString("phone_number"));
        employee.setCpf(rs.getString("cpf"));
        
        // Datas
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) {
            employee.setHireDate(hireDate.toLocalDate());
        }
        
        employee.setActive(rs.getBoolean("is_active"));
        employee.setNotes(rs.getString("notes"));
        
        // Campos específicos do funcionário
        employee.setSpecialties(rs.getString("specialties"));
        employee.setExperienceLevel(rs.getString("experience_level"));
        
        // BigDecimal pode ser null
        BigDecimal baseSalary = rs.getBigDecimal("base_salary");
        if (baseSalary != null) {
            employee.setBaseSalary(baseSalary);
        }
        
        BigDecimal commissionRate = rs.getBigDecimal("commission_rate");
        if (commissionRate != null) {
            employee.setCommissionRate(commissionRate);
        }
        
        employee.setWorkingHours(rs.getString("working_hours"));
        
        // Converte JSON string para List<String>
        String workingDaysJson = rs.getString("working_days");
        List<String> workingDays = convertJsonToList(workingDaysJson);
        employee.setWorkingDays(workingDays);
        
        employee.setPosition(rs.getString("position"));
        employee.setCertificates(rs.getString("certificates"));
        employee.setCanPerformChemicalTreatments(rs.getBoolean("can_perform_chemical_treatments"));
        employee.setPreferredClientType(rs.getString("preferred_client_type"));
        
        // Last training pode ser null
        Timestamp lastTrainingTs = rs.getTimestamp("last_training_date");
        if (lastTrainingTs != null) {
            employee.setLastTrainingDate(lastTrainingTs.toLocalDateTime());
        }
        
        return employee;
    }

    private static String convertListToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(list.get(i)).append("\"");
        }
        json.append("]");
        
        return json.toString();
    }
    

    private static List<String> convertJsonToList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        // Remove [ ] e split por ,
        String clean = json.replace("[", "").replace("]", "").replace("\"", "");
        if (clean.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] items = clean.split(",");
        List<String> result = new ArrayList<>();
        for (String item : items) {
            result.add(item.trim());
        }
        
        return result;
    }
}