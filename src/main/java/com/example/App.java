package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.backends.database.connection.Connect;

import java.sql.Connection;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 800, 600);
        stage.setScene(scene);
        stage.setTitle("AGS-Unissex");
        
        // Configura redimensionamento ANTES de mostrar
        stage.setResizable(true);
        
        // Configura tamanhos mínimo
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // Permite fullscreen com F11
        scene.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("F11")) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        
        // Mostra a janela primeiro
        stage.show();
        
        // DEPOIS maximiza (isso é importante!)
        stage.setMaximized(true);
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        // Teste de conexão com fechamento adequado
        try (Connection connection = Connect.getConnection()) {
            System.out.println("✅ Conexão testada com sucesso: " + connection);
        } catch (Exception e) {
            System.err.println("❌ Erro ao testar conexão: " + e.getMessage());
        }
        
        launch();
    }
}
