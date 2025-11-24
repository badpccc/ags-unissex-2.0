package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import com.example.backends.classes.Adm;
import com.example.backends.classes.Employee;
import com.example.backends.database.data.AdmDAO;
import com.example.backends.database.data.EmployeeDAO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EditarUsuarioController {

    // Campos comuns
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private CheckBox chkAtivo;
    @FXML private TextArea txtObservacoes;
    @FXML private Label lblTitulo;

    // Campos específicos de Administrador
    @FXML private VBox camposAdministrador;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtSenha;
    @FXML private CheckBox chkAlterarSenha;

    // Campos específicos de Funcionário
    @FXML private VBox camposFuncionario;
    @FXML private TextField txtUsernameFuncionario;
    @FXML private PasswordField txtSenhaFuncionario;
    @FXML private CheckBox chkAlterarSenhaFuncionario;
    @FXML private TextField txtCpf;
    @FXML private DatePicker dpDataContratacao;
    @FXML private TextField txtEspecialidades;
    @FXML private ComboBox<String> cmbNivelExperiencia;
    @FXML private TextField txtSalarioBase;
    @FXML private TextField txtComissao;
    @FXML private TextField txtHorarioTrabalho;
    @FXML private TextField txtCargo;
    @FXML private CheckBox chkQuimicos;
    @FXML private ComboBox<String> cmbTipoClientePreferido;

    // Labels de erro
    @FXML private Label lblErro;
    
    private Adm admAtual;
    private Employee funcionarioAtual;
    private String tipoUsuario; // "ADMIN" ou "EMPLOYEE"
    private Runnable callback;

    @FXML
    public void initialize() {
        // Configurar ComboBoxes
        cmbNivelExperiencia.getItems().addAll("Iniciante", "Intermediário", "Avançado", "Especialista");
        cmbTipoClientePreferido.getItems().addAll("Masculino", "Feminino", "Infantil", "Todos");
        
        // Desabilitar campos de senha inicialmente
        txtSenha.setDisable(true);
        txtSenhaFuncionario.setDisable(true);
        
        // Listeners para checkbox de alterar senha
        chkAlterarSenha.selectedProperty().addListener((obs, oldVal, newVal) -> {
            txtSenha.setDisable(!newVal);
            if (!newVal) txtSenha.clear();
        });
        
        chkAlterarSenhaFuncionario.selectedProperty().addListener((obs, oldVal, newVal) -> {
            txtSenhaFuncionario.setDisable(!newVal);
            if (!newVal) txtSenhaFuncionario.clear();
        });
        
        // Máscara de telefone (11) 99999-9999
        txtTelefone.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            
            String cleaned = newValue.replaceAll("[^0-9]", "");
            
            if (cleaned.length() > 11) {
                cleaned = cleaned.substring(0, 11);
            }
            
            StringBuilder formatted = new StringBuilder();
            
            if (cleaned.length() > 0) {
                formatted.append("(");
                formatted.append(cleaned.substring(0, Math.min(2, cleaned.length())));
                
                if (cleaned.length() > 2) {
                    formatted.append(") ");
                    formatted.append(cleaned.substring(2, Math.min(7, cleaned.length())));
                    
                    if (cleaned.length() > 7) {
                        formatted.append("-");
                        formatted.append(cleaned.substring(7));
                    }
                }
            }
            
            String finalText = formatted.toString();
            
            if (!finalText.equals(newValue)) {
                txtTelefone.setText(finalText);
                txtTelefone.positionCaret(finalText.length());
            }
        });
        
        // Máscara de CPF
        if (txtCpf != null) {
            txtCpf.textProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) return;
                
                String cleaned = newValue.replaceAll("[^0-9]", "");
                if (cleaned.length() > 11) cleaned = cleaned.substring(0, 11);
                
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < cleaned.length(); i++) {
                    if (i == 3 || i == 6) formatted.append(".");
                    if (i == 9) formatted.append("-");
                    formatted.append(cleaned.charAt(i));
                }
                
                String finalText = formatted.toString();
                if (!finalText.equals(newValue)) {
                    txtCpf.setText(finalText);
                    txtCpf.positionCaret(finalText.length());
                }
            });
        }
    }
    
    public void carregarAdministrador(Adm adm) {
        this.admAtual = adm;
        this.tipoUsuario = "ADMIN";
        
        lblTitulo.setText("✏️ Editar Administrador");
        
        // Mostrar apenas campos de administrador
        camposAdministrador.setVisible(true);
        camposAdministrador.setManaged(true);
        camposFuncionario.setVisible(false);
        camposFuncionario.setManaged(false);
        
        // Preencher campos
        txtNome.setText(adm.getFullName());
        txtEmail.setText(adm.getEmail());
        txtTelefone.setText(adm.getPhoneNumber());
        chkAtivo.setSelected(adm.isActive());
        txtObservacoes.setText(adm.getNotes());
        txtUsername.setText(adm.getUsername());
    }
    
    public void carregarFuncionario(Employee emp) {
        this.funcionarioAtual = emp;
        this.tipoUsuario = "EMPLOYEE";
        
        lblTitulo.setText("✏️ Editar Funcionário");
        
        // Mostrar apenas campos de funcionário
        camposAdministrador.setVisible(false);
        camposAdministrador.setManaged(false);
        camposFuncionario.setVisible(true);
        camposFuncionario.setManaged(true);
        
        // Preencher campos comuns
        txtNome.setText(emp.getName());
        txtEmail.setText(emp.getEmail());
        txtTelefone.setText(emp.getPhoneNumber());
        chkAtivo.setSelected(emp.isActive());
        txtObservacoes.setText(emp.getNotes());
        
        // Preencher campos de funcionário
        txtUsernameFuncionario.setText(emp.getUsername());
        txtCpf.setText(emp.getCpf());
        dpDataContratacao.setValue(emp.getHireDate());
        txtEspecialidades.setText(emp.getSpecialties());
        cmbNivelExperiencia.setValue(emp.getExperienceLevel());
        txtCargo.setText(emp.getPosition());
        chkQuimicos.setSelected(emp.isCanPerformChemicalTreatments());
        cmbTipoClientePreferido.setValue(emp.getPreferredClientType());
        txtHorarioTrabalho.setText(emp.getWorkingHours());
        
        if (emp.getBaseSalary() != null) {
            txtSalarioBase.setText(emp.getBaseSalary().toString());
        }
        
        if (emp.getCommissionRate() != null) {
            txtComissao.setText(emp.getCommissionRate().toString());
        }
    }
    
    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @FXML
    private void fecharModal() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvar() {
        lblErro.setText("");
        
        // Validações comuns
        if (!validarCamposComuns()) {
            return;
        }
        
        boolean sucesso = false;
        
        try {
            if ("ADMIN".equals(tipoUsuario)) {
                sucesso = salvarAdministrador();
            } else if ("EMPLOYEE".equals(tipoUsuario)) {
                sucesso = salvarFuncionario();
            }
            
            if (sucesso) {
                if (callback != null) {
                    callback.run();
                }
                fecharModal();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErroPopup("Erro", "Erro ao salvar: " + e.getMessage());
        }
    }
    
    private boolean validarCamposComuns() {
        if (txtNome.getText().trim().isEmpty()) {
            mostrarErroPopup("Campo Obrigatório", "Por favor, digite o nome completo!");
            txtNome.requestFocus();
            return false;
        }
        
        if (txtNome.getText().trim().length() < 3) {
            mostrarErroPopup("Nome Inválido", "O nome deve ter no mínimo 3 caracteres!");
            txtNome.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarErroPopup("Campo Obrigatório", "Por favor, digite o email!");
            txtEmail.requestFocus();
            return false;
        }
        
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarErroPopup("Email Inválido", "Por favor, digite um email válido!");
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar telefone se preenchido
        if (!txtTelefone.getText().trim().isEmpty()) {
            String telefone = txtTelefone.getText().replaceAll("[^0-9]", "");
            if (telefone.length() != 11) {
                mostrarErroPopup("Telefone Inválido", "O telefone deve ter 11 dígitos!\nFormato: (11) 99999-9999");
                txtTelefone.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    private boolean salvarAdministrador() {
        // Validar username
        if (txtUsername.getText().trim().isEmpty()) {
            mostrarErroPopup("Campo Obrigatório", "Por favor, digite o username!");
            txtUsername.requestFocus();
            return false;
        }
        
        if (txtUsername.getText().length() < 4) {
            mostrarErroPopup("Username Inválido", "O username deve ter no mínimo 4 caracteres!");
            txtUsername.requestFocus();
            return false;
        }
        
        // Validar senha se checkbox marcado
        if (chkAlterarSenha.isSelected()) {
            if (txtSenha.getText().trim().isEmpty()) {
                mostrarErroPopup("Campo Obrigatório", "Por favor, digite a nova senha!");
                txtSenha.requestFocus();
                return false;
            }
            
            if (txtSenha.getText().length() < 6) {
                mostrarErroPopup("Senha Inválida", "A senha deve ter no mínimo 6 caracteres!");
                txtSenha.requestFocus();
                return false;
            }
        }
        
        try {
            // Atualizar dados do admin
            admAtual.setFullName(txtNome.getText().trim());
            admAtual.setUsername(txtUsername.getText().trim());
            admAtual.setEmail(txtEmail.getText().trim());
            admAtual.setPhoneNumber(txtTelefone.getText().replaceAll("[^0-9]", ""));
            admAtual.setActive(chkAtivo.isSelected());
            admAtual.setNotes(txtObservacoes.getText().trim());
            
            // Atualizar senha se checkbox marcado
            if (chkAlterarSenha.isSelected()) {
                String senhaCriptografada = BCrypt.hashpw(txtSenha.getText(), BCrypt.gensalt());
                admAtual.setPasswordHash(senhaCriptografada);
                
                System.out.println("\n======== SENHA ATUALIZADA ========");
                System.out.println("Admin: " + admAtual.getFullName());
                System.out.println("Nova Senha: " + txtSenha.getText());
                System.out.println("Novo Hash: " + senhaCriptografada);
                System.out.println("==================================\n");
            }
            
            boolean sucesso = AdmDAO.update(admAtual);
            
            if (sucesso) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("✅ Administrador atualizado com sucesso!");
                alert.showAndWait();
                return true;
            } else {
                mostrarErroPopup("Erro", "Não foi possível atualizar o administrador.");
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErroPopup("Erro", "Erro ao atualizar administrador: " + e.getMessage());
            return false;
        }
    }
    
    private boolean salvarFuncionario() {
        // Validar username
        if (txtUsernameFuncionario.getText().trim().isEmpty()) {
            mostrarErroPopup("Campo Obrigatório", "Por favor, digite o username!");
            txtUsernameFuncionario.requestFocus();
            return false;
        }
        
        if (txtUsernameFuncionario.getText().length() < 4) {
            mostrarErroPopup("Username Inválido", "O username deve ter no mínimo 4 caracteres!");
            txtUsernameFuncionario.requestFocus();
            return false;
        }
        
        // Validar senha se checkbox marcado
        if (chkAlterarSenhaFuncionario.isSelected()) {
            if (txtSenhaFuncionario.getText().trim().isEmpty()) {
                mostrarErroPopup("Campo Obrigatório", "Por favor, digite a nova senha!");
                txtSenhaFuncionario.requestFocus();
                return false;
            }
            
            if (txtSenhaFuncionario.getText().length() < 6) {
                mostrarErroPopup("Senha Inválida", "A senha deve ter no mínimo 6 caracteres!");
                txtSenhaFuncionario.requestFocus();
                return false;
            }
        }
        
        if (dpDataContratacao.getValue() == null) {
            mostrarErroPopup("Data Obrigatória", "Por favor, selecione a data de contratação.");
            return false;
        }
        
        try {
            // Atualizar dados do funcionário
            funcionarioAtual.setName(txtNome.getText().trim());
            funcionarioAtual.setUsername(txtUsernameFuncionario.getText().trim());
            funcionarioAtual.setEmail(txtEmail.getText().trim());
            funcionarioAtual.setPhoneNumber(txtTelefone.getText().replaceAll("[^0-9]", ""));
            funcionarioAtual.setCpf(txtCpf.getText().replaceAll("[^0-9]", ""));
            funcionarioAtual.setHireDate(dpDataContratacao.getValue());
            funcionarioAtual.setActive(chkAtivo.isSelected());
            funcionarioAtual.setNotes(txtObservacoes.getText().trim());
            
            // Atualizar senha se checkbox marcado
            if (chkAlterarSenhaFuncionario.isSelected()) {
                String senhaCriptografada = BCrypt.hashpw(txtSenhaFuncionario.getText(), BCrypt.gensalt());
                funcionarioAtual.setPasswordHash(senhaCriptografada);
                
                System.out.println("\n======== SENHA ATUALIZADA ========");
                System.out.println("Funcionário: " + funcionarioAtual.getName());
                System.out.println("Nova Senha: " + txtSenhaFuncionario.getText());
                System.out.println("Novo Hash: " + senhaCriptografada);
                System.out.println("==================================\n");
            }
            
            // Campos específicos de funcionário
            funcionarioAtual.setSpecialties(txtEspecialidades.getText().trim());
            funcionarioAtual.setExperienceLevel(cmbNivelExperiencia.getValue());
            funcionarioAtual.setWorkingHours(txtHorarioTrabalho.getText().trim());
            funcionarioAtual.setPosition(txtCargo.getText().trim());
            funcionarioAtual.setCanPerformChemicalTreatments(chkQuimicos.isSelected());
            funcionarioAtual.setPreferredClientType(cmbTipoClientePreferido.getValue());
            
            // Salário base
            if (!txtSalarioBase.getText().trim().isEmpty()) {
                try {
                    BigDecimal salario = new BigDecimal(txtSalarioBase.getText().trim().replace(",", "."));
                    funcionarioAtual.setBaseSalary(salario);
                } catch (NumberFormatException e) {
                    mostrarErroPopup("Salário Inválido", "Por favor, insira um valor numérico válido.");
                    return false;
                }
            }
            
            // Comissão
            if (!txtComissao.getText().trim().isEmpty()) {
                try {
                    BigDecimal comissao = new BigDecimal(txtComissao.getText().trim().replace(",", "."));
                    funcionarioAtual.setCommissionRate(comissao);
                } catch (NumberFormatException e) {
                    mostrarErroPopup("Comissão Inválida", "Por favor, insira um valor numérico válido.");
                    return false;
                }
            }
            
            boolean sucesso = EmployeeDAO.update(funcionarioAtual);
            
            if (sucesso) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("✅ Funcionário atualizado com sucesso!");
                alert.showAndWait();
                return true;
            } else {
                mostrarErroPopup("Erro", "Não foi possível atualizar o funcionário.");
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErroPopup("Erro", "Erro ao atualizar funcionário: " + e.getMessage());
            return false;
        }
    }
    
    private void mostrarErroPopup(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ " + titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
