package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import com.example.backends.database.data.AdmDAO;
import com.example.backends.classes.Adm;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController {

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPass;

    @FXML
    private PasswordField txtPass2;
    
    @FXML
    private TextArea txtNotes;

    @FXML
    private Label lblMessage;

    // Padrões de validação
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$"
    );
    
    @FXML
    private void initialize() {
        // Aplicar formatação automática ao telefone
        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String formatted = formatPhone(newValue);
                if (!formatted.equals(newValue)) {
                    txtPhone.setText(formatted);
                    txtPhone.positionCaret(formatted.length());
                }
            }
        });
    }

    @FXML
    private void handleRegister() {
        // Limpar mensagem anterior
        lblMessage.setText("");

        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String username = txtUser.getText().trim();
        String pass = txtPass.getText();
        String pass2 = txtPass2.getText();
        String notes = txtNotes.getText().trim();

        // ======== VALIDAÇÕES ========
        
        // 1. Campos obrigatórios
        if (fullName.isEmpty()) {
            showError("❌ O nome completo é obrigatório!");
            txtFullName.requestFocus();
            return;
        }
        
        if (username.isEmpty()) {
            showError("❌ O usuário é obrigatório!");
            txtUser.requestFocus();
            return;
        }
        
        if (email.isEmpty()) {
            showError("❌ O email é obrigatório!");
            txtEmail.requestFocus();
            return;
        }
        
        if (pass.isEmpty()) {
            showError("❌ A senha é obrigatória!");
            txtPass.requestFocus();
            return;
        }
        
        if (pass2.isEmpty()) {
            showError("❌ A confirmação de senha é obrigatória!");
            txtPass2.requestFocus();
            return;
        }

        // 2. Validação de tamanho mínimo
        if (fullName.length() < 3) {
            showError("❌ O nome completo deve ter pelo menos 3 caracteres!");
            txtFullName.requestFocus();
            return;
        }
        
        if (username.length() < 4) {
            showError("❌ O usuário deve ter pelo menos 4 caracteres!");
            txtUser.requestFocus();
            return;
        }
        
        if (pass.length() < 6) {
            showError("❌ A senha deve ter pelo menos 6 caracteres!");
            txtPass.requestFocus();
            return;
        }

        // 3. Validação de formato de email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("❌ Email inválido! Use o formato: usuario@dominio.com");
            txtEmail.requestFocus();
            return;
        }
        
        // 4. Validação de telefone (se preenchido)
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            showError("❌ Telefone inválido! Use o formato: (11) 98765-4321");
            txtPhone.requestFocus();
            return;
        }

        // 5. Validação de senhas iguais
        if (!pass.equals(pass2)) {
            showError("❌ As senhas não coincidem!");
            txtPass2.requestFocus();
            return;
        }
        
        // 6. Validação de caracteres especiais no usuário
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("❌ O usuário pode conter apenas letras, números e underscore (_)!");
            txtUser.requestFocus();
            return;
        }

        // 7. Validação de força da senha
        if (!isStrongPassword(pass)) {
            showError("⚠️ Senha fraca! Use letras maiúsculas, minúsculas e números.");
            txtPass.requestFocus();
            return;
        }

        // ======== CRIAR OBJETO ADM ========
        Adm adm = new Adm();
        adm.setFullName(fullName);
        adm.setEmail(email);
        adm.setPhoneNumber(phone.isEmpty() ? null : phone);
        adm.setUsername(username);
        
        // Criptografar senha com BCrypt
        String hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt(12));
        
        // Print da senha normal e criptografada no terminal
        System.out.println("\n======== REGISTRO DE ADMINISTRADOR ========");
        System.out.println("Usuário: " + username);
        System.out.println("Senha Normal: " + pass);
        System.out.println("Senha Criptografada: " + hashedPassword);
        System.out.println("==========================================\n");
        
        adm.setPasswordHash(hashedPassword);
        adm.setNotes(notes.isEmpty() ? null : notes);

        // ======== SALVAR NO BANCO ========
        try {
            boolean sucesso = AdmDAO.insert(adm);

            if (sucesso) {
                showSuccess("✅ Registrado com sucesso!");
                
                // Aguardar 1.5 segundo antes de voltar
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(() -> {
                            try {
                                App.setRoot("primary");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                
            } else {
                showError("❌ Erro: usuário ou email já existe!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("❌ Erro ao registrar no banco: " + e.getMessage());
        }
    }
    
    /**
     * Valida se a senha é forte (tem letras maiúsculas, minúsculas e números)
     */
    private boolean isStrongPassword(String password) {
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        
        return hasUpper && hasLower && hasDigit;
    }

    private void showError(String msg) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: #FF4444; -fx-font-weight: bold;");
    }
    
    private void showSuccess(String msg) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        clearFields();
    }

    private void clearFields() {
        txtFullName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtUser.clear();
        txtPass.clear();
        txtPass2.clear();
        txtNotes.clear();
    }
    
    /**
     * Formata o telefone automaticamente enquanto o usuário digita
     * Formato: (00) 00000-0000 ou (00) 0000-0000
     */
    private String formatPhone(String phone) {
        // Remove tudo que não é número
        String numbers = phone.replaceAll("[^0-9]", "");
        
        // Limita a 11 dígitos
        if (numbers.length() > 11) {
            numbers = numbers.substring(0, 11);
        }
        
        // Aplica a formatação progressiva
        StringBuilder formatted = new StringBuilder();
        
        if (numbers.length() > 0) {
            formatted.append("(");
            formatted.append(numbers.substring(0, Math.min(2, numbers.length())));
            
            if (numbers.length() > 2) {
                formatted.append(") ");
                
                if (numbers.length() <= 6) {
                    // (00) 0000
                    formatted.append(numbers.substring(2));
                } else if (numbers.length() <= 10) {
                    // (00) 0000-0000
                    formatted.append(numbers.substring(2, 6));
                    formatted.append("-");
                    formatted.append(numbers.substring(6));
                } else {
                    // (00) 00000-0000
                    formatted.append(numbers.substring(2, 7));
                    formatted.append("-");
                    formatted.append(numbers.substring(7));
                }
            }
        }
        
        return formatted.toString();
    }
}
