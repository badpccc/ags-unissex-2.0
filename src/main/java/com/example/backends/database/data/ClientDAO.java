package com.example.backends.database.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.example.backends.classes.Client;
import com.example.backends.database.connection.Connect;

public class ClientDAO {
    public void insert(Client client){
        String sql = "INSERT INTO clients (name, email, phone_number, address, notes) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = Connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getEmail());
            pstmt.setString(3, client.getPhoneNumber());
            pstmt.setString(4, client.getAddress());
            pstmt.setString(5, client.getNotes());
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }

    }
}