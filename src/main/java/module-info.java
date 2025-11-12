module com.example {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    // Dependências para banco de dados
    requires java.sql;
    requires java.desktop;

    // Dependências das bibliotecas adicionadas
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari;
    requires io.github.cdimascio.dotenv.java;

    opens com.example to javafx.fxml;
    exports com.example;
}