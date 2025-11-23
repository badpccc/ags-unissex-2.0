package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.example.backends.database.data.AdmDAO;
import com.example.backends.classes.Adm;
import org.mindrot.jbcrypt.BCrypt;

public class PrimaryController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label lblMessage;

    /**
     * Método de login com validação no banco de dados e senha criptografada
     */
    @FXML
    private void switchToSecondary() throws IOException {
        // Limpar mensagem anterior
        if (lblMessage != null) {
            lblMessage.setText("");
        }
        
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validações básicas
        if (username.isEmpty()) {
            showError("❌ Digite seu usuário!");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("❌ Digite sua senha!");
            passwordField.requestFocus();
            return;
        }
        
        // Buscar administrador no banco
        try {
            Adm adm = AdmDAO.getByUsername(username);
            
            if (adm == null) {
                showError("❌ Usuário não encontrado!");
                usernameField.requestFocus();
                return;
            }
            
            // Verificar senha com BCrypt
            boolean senhaCorreta = BCrypt.checkpw(password, adm.getPasswordHash());
            
            // Print no terminal para debug
            System.out.println("\n======== TENTATIVA DE LOGIN ========");
            System.out.println("Usuário: " + username);
            System.out.println("Senha Digitada: " + password);
            System.out.println("Hash no Banco: " + adm.getPasswordHash());
            System.out.println("Senha Correta: " + senhaCorreta);
            System.out.println("====================================\n");
            
            if (!senhaCorreta) {
                showError("❌ Senha incorreta!");
                passwordField.clear();
                passwordField.requestFocus();
                return;
            }
            
            // Login bem-sucedido
            showSuccess("✅ Login realizado com sucesso!");
            
            // Aguardar 500ms antes de redirecionar
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            App.setRoot("secondary");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("❌ Erro ao conectar ao banco: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() throws IOException {
        App.setRoot("registro");
    }
    
    private void showError(String msg) {
        if (lblMessage != null) {
            lblMessage.setText(msg);
            lblMessage.setStyle("-fx-text-fill: #FF4444; -fx-font-weight: bold;");
        }
    }
    
    private void showSuccess(String msg) {
        if (lblMessage != null) {
            lblMessage.setText(msg);
            lblMessage.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        }
    }

}
