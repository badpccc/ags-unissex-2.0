package com.example.backends.database.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class Connect {
    private static final Logger logger = LoggerFactory.getLogger(Connect.class);
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
            
            logger.info("✅ Pool de conexões HikariCP inicializado com sucesso!");
            logger.info("📊 Pool Info:");
            logger.info("   - URL: {}", dotenv.get("DATABASE_URL_JDBC"));
            logger.info("   - Usuário: {}", dotenv.get("POSTGRES_USER"));
            logger.info("   - Pool máximo: {}", config.getMaximumPoolSize());
            logger.info("   - Pool mínimo: {}", config.getMinimumIdle());
            
        } catch (Exception e) {
            logger.error("❌ Erro ao inicializar pool de conexões: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na inicialização do pool de conexões", e);
        }
    }

    public static Connection getConnection() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                logger.warn("⚠️ DataSource é null ou fechado, reinicializando...");
                initializeConnectionPool();
            }
            
            Connection connection = dataSource.getConnection();
            logger.debug("🔗 Conexão obtida do pool (Ativas: {}/{})", 
                        dataSource.getHikariPoolMXBean().getActiveConnections(),
                        dataSource.getMaximumPoolSize());
            return connection;
            
        } catch (SQLException e) {
            logger.error("❌ Erro ao obter conexão do pool: {}", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    public static Connection startConnection() {
        return getConnection();
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("🔒 Fechando pool de conexões...");
            dataSource.close();
            logger.info("✅ Pool de conexões fechado com sucesso!");
        }
    }

    public static void printPoolStatus() {
        if (dataSource != null && !dataSource.isClosed()) {
            var mxBean = dataSource.getHikariPoolMXBean();
            logger.info("📊 Status do Pool de Conexões:");
            logger.info("   - Conexões ativas: {}", mxBean.getActiveConnections());
            logger.info("   - Conexões inativas: {}", mxBean.getIdleConnections());
            logger.info("   - Total de conexões: {}", mxBean.getTotalConnections());
            logger.info("   - Threads aguardando: {}", mxBean.getThreadsAwaitingConnection());
        } else {
            logger.warn("❌ Pool de conexões não está ativo!");
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Teste de conexão bem-sucedido!");
                return true;

            }
        } catch (SQLException e) {
            System.err.println("❌ Teste de conexão falhou: " + e.getMessage());
        }
        return false;
    }
}
