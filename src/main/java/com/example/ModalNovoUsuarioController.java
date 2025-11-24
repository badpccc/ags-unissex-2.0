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

public class ModalNovoUsuarioController {

    // Campos comuns
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtTelefone;
    @FXML private CheckBox chkAtivo;
    @FXML private TextArea txtObservacoes;

    // Campos específicos de Administrador
    @FXML private VBox camposAdministrador;
    @FXML private TextField txtUsername;

    // Campos específicos de Funcionário
    @FXML private VBox camposFuncionario;
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

    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll("Administrador", "Funcionário");
        
        // Configurar ComboBox de nível de experiência
        cmbNivelExperiencia.getItems().addAll("Iniciante", "Intermediário", "Avançado", "Especialista");
        cmbNivelExperiencia.setValue("Iniciante");
        
        // Configurar ComboBox de tipo de cliente preferido
        cmbTipoClientePreferido.getItems().addAll("Masculino", "Feminino", "Infantil", "Todos");
        cmbTipoClientePreferido.setValue("Todos");
        
        // Listener para mostrar/ocultar campos baseado no tipo
        cmbTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                boolean isAdm = newVal.equals("Administrador");
                camposAdministrador.setVisible(isAdm);
                camposAdministrador.setManaged(isAdm);
                camposFuncionario.setVisible(!isAdm);
                camposFuncionario.setManaged(!isAdm);
                lblErro.setText("");
            }
        });
        
        // Inicialmente esconder todos os campos específicos
        camposAdministrador.setVisible(false);
        camposAdministrador.setManaged(false);
        camposFuncionario.setVisible(false);
        camposFuncionario.setManaged(false);
    }

    @FXML
    private void fecharModal() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvar() {
        System.out.println("=== BOTÃO SALVAR CLICADO ===");
        lblErro.setText("");
        
        String tipo = cmbTipo.getValue();
        System.out.println("Tipo selecionado: " + tipo);
        
        if (tipo == null) {
            mostrarErro("Selecione o tipo de usuário");
            return;
        }
        
        // Validações comuns
        if (!validarCamposComuns()) {
            System.out.println("Falhou na validação de campos comuns");
            return;
        }
        
        System.out.println("Validações comuns OK, chamando salvar" + tipo);
        
        boolean sucesso = false;
        
        try {
            if (tipo.equals("Administrador")) {
                sucesso = salvarAdministrador();
            } else {
                sucesso = salvarFuncionario();
            }
            
            System.out.println("Resultado do salvamento: " + sucesso);
            
            if (sucesso) {
                System.out.println("Salvamento bem-sucedido, mostrando alerta");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText(tipo + " cadastrado com sucesso!");
                alert.showAndWait();
                fecharModal();
            } else {
                System.out.println("Salvamento falhou");
                mostrarErro("Erro ao cadastrar " + tipo.toLowerCase());
            }
        } catch (Exception e) {
            System.err.println("EXCEÇÃO no método salvar: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }
    
    private boolean validarCamposComuns() {
        System.out.println("Validando campos comuns...");
        
        if (txtNome.getText().trim().isEmpty()) {
            System.out.println("Nome vazio");
            mostrarErro("Nome é obrigatório");
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            System.out.println("Email vazio");
            mostrarErro("Email é obrigatório");
            return false;
        }
        
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.out.println("Email inválido: " + txtEmail.getText());
            mostrarErro("Email inválido");
            return false;
        }
        
        System.out.println("Validação de campos comuns OK");
        return true;
    }
    
    private boolean salvarAdministrador() {
        System.out.println("=== ENTRANDO EM salvarAdministrador ===");
        
        // Validações específicas de Administrador
        System.out.println("Username: '" + txtUsername.getText() + "'");
        System.out.println("Senha: '" + txtSenha.getText() + "'");
        
        if (txtUsername.getText().trim().isEmpty()) {
            System.out.println("Username vazio!");
            mostrarErro("Username é obrigatório para administrador");
            return false;
        }
        
        if (txtUsername.getText().length() < 4) {
            System.out.println("Username muito curto: " + txtUsername.getText().length());
            mostrarErro("Username deve ter no mínimo 4 caracteres");
            return false;
        }
        
        if (txtSenha.getText().trim().isEmpty()) {
            System.out.println("Senha vazia!");
            mostrarErro("Senha é obrigatória");
            return false;
        }
        
        if (txtSenha.getText().length() < 6) {
            System.out.println("Senha muito curta: " + txtSenha.getText().length());
            mostrarErro("Senha deve ter no mínimo 6 caracteres");
            return false;
        }
        
        System.out.println("Validações de administrador OK, criando objeto...");
        
        try {
            // Criar objeto Adm
            Adm adm = new Adm();
            adm.setFullName(txtNome.getText().trim());
            adm.setUsername(txtUsername.getText().trim());
            adm.setPasswordHash(BCrypt.hashpw(txtSenha.getText(), BCrypt.gensalt()));
            adm.setEmail(txtEmail.getText().trim());
            adm.setPhoneNumber(txtTelefone.getText().trim());
            adm.setActive(chkAtivo.isSelected());
            adm.setNotes(txtObservacoes.getText().trim());
            
            System.out.println("=== CONTROLLER: Tentando salvar administrador ===");
            System.out.println("Nome: " + adm.getFullName());
            System.out.println("Username: " + adm.getUsername());
            System.out.println("Email: " + adm.getEmail());
            System.out.println("Telefone: " + adm.getPhoneNumber());
            System.out.println("Ativo: " + adm.isActive());
            System.out.println("===================================================");
            
            boolean resultado = AdmDAO.insert(adm);
            
            System.out.println("Resultado do insert: " + resultado);
            System.out.println("ID gerado: " + adm.getId());
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("ERRO no salvarAdministrador: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro ao processar dados: " + e.getMessage());
            return false;
        }
    }
    
    private boolean salvarFuncionario() {
        System.out.println("=== ENTRANDO EM salvarFuncionario ===");
        
        // Validações específicas de Funcionário
        System.out.println("CPF: '" + txtCpf.getText() + "'");
        System.out.println("Data Contratação: " + dpDataContratacao.getValue());
        
        if (txtCpf.getText().trim().isEmpty()) {
            System.out.println("CPF vazio!");
            mostrarErro("CPF é obrigatório para funcionário");
            return false;
        }
        
        if (!validarCPF(txtCpf.getText().trim())) {
            System.out.println("CPF inválido!");
            mostrarErro("CPF inválido");
            return false;
        }
        
        if (dpDataContratacao.getValue() == null) {
            System.out.println("Data de contratação não selecionada!");
            mostrarErro("Data de contratação é obrigatória");
            return false;
        }
        
        if (dpDataContratacao.getValue().isAfter(LocalDate.now())) {
            System.out.println("Data de contratação futura!");
            mostrarErro("Data de contratação não pode ser futura");
            return false;
        }
        
        System.out.println("Validações de funcionário OK, criando objeto...");
        
        try {
            // Criar objeto Employee
            Employee employee = new Employee();
            employee.setName(txtNome.getText().trim());
            employee.setEmail(txtEmail.getText().trim());
            employee.setPhoneNumber(txtTelefone.getText().trim());
            employee.setCpf(txtCpf.getText().trim());
            employee.setHireDate(dpDataContratacao.getValue());
            employee.setActive(chkAtivo.isSelected());
            employee.setNotes(txtObservacoes.getText().trim());
            
            // Campos específicos de funcionário
            employee.setSpecialties(txtEspecialidades.getText().trim());
            employee.setExperienceLevel(cmbNivelExperiencia.getValue());
            employee.setWorkingHours(txtHorarioTrabalho.getText().trim());
            employee.setPosition(txtCargo.getText().trim());
            employee.setCanPerformChemicalTreatments(chkQuimicos.isSelected());
            employee.setPreferredClientType(cmbTipoClientePreferido.getValue());
            
            // Salário base (opcional)
            if (!txtSalarioBase.getText().trim().isEmpty()) {
                try {
                    BigDecimal salario = new BigDecimal(txtSalarioBase.getText().trim().replace(",", "."));
                    employee.setBaseSalary(salario);
                } catch (NumberFormatException e) {
                    System.out.println("Salário inválido: " + txtSalarioBase.getText());
                    mostrarErro("Salário base inválido");
                    return false;
                }
            }
            
            // Comissão (opcional)
            if (!txtComissao.getText().trim().isEmpty()) {
                try {
                    BigDecimal comissao = new BigDecimal(txtComissao.getText().trim().replace(",", "."));
                    if (comissao.compareTo(BigDecimal.ZERO) < 0 || comissao.compareTo(BigDecimal.ONE) > 0) {
                        System.out.println("Comissão fora do range: " + comissao);
                        mostrarErro("Comissão deve estar entre 0 e 1 (ex: 0.30 para 30%)");
                        return false;
                    }
                    employee.setCommissionRate(comissao);
                } catch (NumberFormatException e) {
                    System.out.println("Comissão inválida: " + txtComissao.getText());
                    mostrarErro("Comissão inválida");
                    return false;
                }
            }
            
            System.out.println("=== CONTROLLER: Tentando salvar funcionário ===");
            System.out.println("Nome: " + employee.getName());
            System.out.println("CPF: " + employee.getCpf());
            System.out.println("Email: " + employee.getEmail());
            System.out.println("Data contratação: " + employee.getHireDate());
            System.out.println("===============================================");
            
            boolean resultado = EmployeeDAO.insert(employee);
            
            System.out.println("Resultado do insert: " + resultado);
            System.out.println("ID gerado: " + employee.getId());
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("ERRO no salvarFuncionario: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro ao processar dados: " + e.getMessage());
            return false;
        }
    }
    
    private boolean validarCPF(String cpf) {
        // Remove pontos e traços
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // CPF deve ter 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Validação dos dígitos verificadores
        try {
            int[] numeros = new int[11];
            for (int i = 0; i < 11; i++) {
                numeros[i] = Integer.parseInt(cpf.substring(i, i + 1));
            }
            
            // Primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += numeros[i] * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) primeiroDigito = 0;
            
            if (primeiroDigito != numeros[9]) {
                return false;
            }
            
            // Segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += numeros[i] * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) segundoDigito = 0;
            
            return segundoDigito == numeros[10];
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private void mostrarErro(String mensagem) {
        lblErro.setText("⚠ " + mensagem);
        lblErro.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
    }
}
