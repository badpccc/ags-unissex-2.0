package com.example.backends.database.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.SQLException;

public class Connect {
    private static HikariDataSource dataSource;
    private static final Dotenv dotenv = Dotenv.configure()
                                               .filename(".env.development")
                                               .ignoreIfMissing()
                                               .load();

    static {
        initializeConnectionPool();
    }

    private static void initializeConnectionPool() {
        try {
            HikariConfig config = new HikariConfig();
            
            config.setJdbcUrl(dotenv.get("DATABASE_URL_JDBC"));
            config.setUsername(dotenv.get("POSTGRES_USER"));
            config.setPassword(dotenv.get("POSTGRES_PASSWORD"));
            config.setDriverClassName("org.postgresql.Driver");
            
            config.setMaximumPoolSize(Integer.parseInt(dotenv.get("DB_MAX_POOL_SIZE", "10")));
            config.setMinimumIdle(Integer.parseInt(dotenv.get("DB_MIN_IDLE", "2")));
            config.setConnectionTimeout(Long.parseLong(dotenv.get("DB_CONNECTION_TIMEOUT", "30000")));
            config.setIdleTimeout(Long.parseLong(dotenv.get("DB_IDLE_TIMEOUT", "600000")));
            config.setMaxLifetime(Long.parseLong(dotenv.get("DB_MAX_LIFETIME", "1800000")));
            
            config.setLeakDetectionThreshold(60000); 
            config.setAutoCommit(false); 
            config.setConnectionTestQuery("SELECT 1"); 
            
            config.setPoolName("AgsUnissexDB-Pool");
            
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            
            dataSource = new HikariDataSource(config);
            
            System.out.println("✅ Pool de conexões HikariCP inicializado com sucesso!");
            System.out.println("📊 Pool Info:");
            System.out.println("   - URL: " + dotenv.get("DATABASE_URL_JDBC"));
            System.out.println("   - Usuário: " + dotenv.get("POSTGRES_USER"));
            System.out.println("   - Pool máximo: " + config.getMaximumPoolSize());
            System.out.println("   - Pool mínimo: " + config.getMinimumIdle());
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar pool de conexões: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Falha na inicialização do pool de conexões", e);
        }
    }

    public static Connection getConnection() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                initializeConnectionPool();
            }
            
            Connection connection = dataSource.getConnection();
            System.out.println("🔗 Conexão obtida do pool (Ativas: " + 
                             dataSource.getHikariPoolMXBean().getActiveConnections() + 
                             "/" + dataSource.getMaximumPoolSize() + ")");
            return connection;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao obter conexão do pool: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static Connection startConnection() {
        return getConnection();
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            System.out.println("🔒 Fechando pool de conexões...");
            dataSource.close();
            System.out.println("✅ Pool de conexões fechado com sucesso!");
        }
    }

    public static void printPoolStatus() {
        if (dataSource != null && !dataSource.isClosed()) {
            var mxBean = dataSource.getHikariPoolMXBean();
            System.out.println("📊 Status do Pool de Conexões:");
            System.out.println("   - Conexões ativas: " + mxBean.getActiveConnections());
            System.out.println("   - Conexões inativas: " + mxBean.getIdleConnections());
            System.out.println("   - Total de conexões: " + mxBean.getTotalConnections());
            System.out.println("   - Threads aguardando: " + mxBean.getThreadsAwaitingConnection());
        } else {
            System.out.println("❌ Pool de conexões não está ativo!");
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Teste de conexão bem-sucedido!");
                return true;                return true;

            }
        } catch (SQLException e) {
            System.err.println("❌ Teste de conexão falhou: " + e.getMessage());
        }
        return false;
    }
}
