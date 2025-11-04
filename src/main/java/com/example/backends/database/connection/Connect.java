package com.example.backends.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class Connect {
    private static final Dotenv dotenv = Dotenv.configure()
                                               .filename(".env.development")
                                               .ignoreIfMissing()
                                               .load();
    
    private static final String JDBCURL = dotenv.get("DATABASE_URL_JDBC");
    private static final String USER = dotenv.get("POSTGRES_USER");
    private static final String PASSWORD = dotenv.get("POSTGRES_PASSWORD");
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static int retryCount = 0;
    private static Connection conn = null;

    public static Connection startConnection() {
        while (retryCount < MAX_RECONNECT_ATTEMPTS) {
            try {
                conn = DriverManager.getConnection(JDBCURL, USER, PASSWORD);
                System.out.println("✅ Connected to the database!");
                System.out.println("URL: " + conn.getMetaData().getURL());
                System.out.println("User: " + conn.getMetaData().getUserName());
                return conn;

            } catch (SQLException e) {
                System.err.println("❌ Erro ao conectar ao banco de dados:");
                System.err.println("Mensagem: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Código do erro: " + e.getErrorCode());

                retryCount++;
                System.out.println("Tentando reconectar... (" + retryCount + "/" + MAX_RECONNECT_ATTEMPTS + ")");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.err.println("❌ Não foi possível conectar ao banco de dados após " + MAX_RECONNECT_ATTEMPTS + " tentativas.");
        return null;
    }
}
