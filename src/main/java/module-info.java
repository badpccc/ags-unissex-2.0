module com.example {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    // Dependências para banco de dados - transitive para que outros módulos possam usar
    requires transitive java.sql;
    requires java.desktop;

    // Dependências das bibliotecas adicionadas
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari;
    requires io.github.cdimascio.dotenv.java;
    
    // Dependências para logging
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    
    // BCrypt para criptografia de senhas
    requires jbcrypt;

    // Exportar todos os pacotes necessários
    exports com.example;
    exports com.example.models;
    exports com.example.dao;
    exports com.example.backends.classes;
    exports com.example.backends.enums;
    exports com.example.backends.database.connection;
    exports com.example.backends.database.data;

    // Abrir pacotes para o JavaFX
    opens com.example to javafx.fxml;
    opens com.example.models to javafx.fxml;
}