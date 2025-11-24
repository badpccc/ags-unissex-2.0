package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.example.backends.database.data.AdmDAO;
import com.example.backends.database.data.EmployeeDAO;
import com.example.backends.classes.Adm;
import com.example.backends.classes.Employee;
import com.example.utils.TelegramNotifier;

import org.mindrot.jbcrypt.BCrypt;

public class PrimaryController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label lblMessage;

    /**
     * Método de login com validação no banco de dados e senha criptografada.
     * Permite login tanto de Administradores quanto de Funcionários.
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

        // Tentar login como Administrador ou Funcionário
        try {
            boolean loginSucesso = false;
            String tipoUsuario = "";
            String passwordHash = "";
            Long userId = null;
            String fullName = "";

            // Primeiro tenta buscar como Admin
            Adm adm = AdmDAO.getByUsername(username);

            if (adm != null) {
                // Verificar senha com BCrypt
                loginSucesso = BCrypt.checkpw(password, adm.getPasswordHash());
                tipoUsuario = "Administrador";
                passwordHash = adm.getPasswordHash();
                userId = adm.getId();
                fullName = adm.getFullName();
            } else {
                // Se não encontrou admin, tenta como Funcionário
                Employee employee = EmployeeDAO.getByUsername(username);

                if (employee != null) {
                    loginSucesso = BCrypt.checkpw(password, employee.getPasswordHash());
                    tipoUsuario = "Funcionário";
                    passwordHash = employee.getPasswordHash();
                    userId = employee.getId();
                    fullName = employee.getName();
                } else {
                    showError("❌ Usuário não encontrado!");
                    TelegramNotifier.send("⚠️ Falha de login:\nUsuário: " + username + "\nMotivo: Não encontrado");
                    usernameField.requestFocus();
                    return;
                }
            }

            // Print no terminal para debug
            System.out.println("\n======== TENTATIVA DE LOGIN ========");
            System.out.println("Usuário: " + username);
            System.out.println("Tipo: " + tipoUsuario);
            System.out.println("Senha Digitada: " + password);
            System.out.println("Hash no Banco: " + passwordHash);
            System.out.println("Senha Correta: " + loginSucesso);
            System.out.println("====================================\n");

            if (!loginSucesso) {
                showError("❌ Senha incorreta!");
                TelegramNotifier.send("⚠️ Falha de login:\nUsuário: " + username + "\nMotivo: Senha incorreta");
                passwordField.clear();
                passwordField.requestFocus();
                return;
            }

            // Login bem-sucedido - iniciar sessão
            UserSession session = UserSession.getInstance();
            session.login(username,
                    adm != null ? "ADMIN" : "EMPLOYEE",
                    userId,
                    fullName);

            showSuccess("✅ Login realizado com sucesso! (" + tipoUsuario + ")");
            TelegramNotifier.send(
                    "🔑 Login realizado:\n" +
                            "Usuário: " + username + "\n" +
                            "Tipo: " + tipoUsuario + "\n" +
                            "Nome: " + fullName
            );

            // Aguardar 500ms antes de redirecionar
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            App.setRoot("secondary");
                        } catch (Exception e) {
                            e.printStackTrace();
                            TelegramNotifier.send("⚠️ Erro ao abrir tela secundária:\n" + e.getMessage());
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    TelegramNotifier.send("⚠️ Thread interrompida durante redirecionamento: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            showError("❌ Erro ao conectar ao banco: " + e.getMessage());
            TelegramNotifier.send("⚠️ Erro ao conectar ao banco:\n" + e.getMessage());
        }
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
