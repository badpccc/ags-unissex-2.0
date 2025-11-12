package com.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/local_db";
    private static final String USER = "postgres";
    private static final String PASS = "12345";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao conectar ao banco.");
        }
    }
}
