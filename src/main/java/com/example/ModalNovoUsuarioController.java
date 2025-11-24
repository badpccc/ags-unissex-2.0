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
            int length = cleaned.length();
            
            if (length > 0) {
                formatted.append("(");
                formatted.append(cleaned.substring(0, Math.min(2, length)));
                
                if (length >= 3) {
                    formatted.append(") ");
                    
                    if (length <= 7) {
                        formatted.append(cleaned.substring(2));
                    } else {
                        formatted.append(cleaned.substring(2, 7));
                        
                        if (length > 7) {
                            formatted.append("-");
                            formatted.append(cleaned.substring(7));
                        }
                    }
                }
            }
            
            String result = formatted.toString();
            if (!result.equals(newValue)) {
                int caretPosition = txtTelefone.getCaretPosition();
                txtTelefone.setText(result);
                
                // Ajustar posição do cursor
                if (caretPosition <= result.length()) {
                    txtTelefone.positionCaret(Math.min(caretPosition + 1, result.length()));
                } else {
                    txtTelefone.positionCaret(result.length());
                }
            }
        });
        
        // Máscara de CPF 000.000.000-00
        txtCpf.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String cleaned = newValue.replaceAll("[^0-9]", "");
                if (cleaned.length() > 11) {
                    cleaned = cleaned.substring(0, 11);
                }
                
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < cleaned.length(); i++) {
                    if (i == 3 || i == 6) {
                        formatted.append(".");
                    } else if (i == 9) {
                        formatted.append("-");
                    }
                    formatted.append(cleaned.charAt(i));
                }
                
                String result = formatted.toString();
                if (!result.equals(newValue)) {
                    txtCpf.setText(result);
                    txtCpf.positionCaret(result.length());
                }
            }
        });
        
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
            mostrarErroPopup("Tipo de Usuário", "Por favor, selecione o tipo de usuário (Administrador ou Funcionário)");
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
                alert.setTitle("✅ Sucesso");
                alert.setHeaderText(tipo + " cadastrado com sucesso!");
                alert.setContentText("Os dados foram salvos no sistema.");
                alert.showAndWait();
                fecharModal();
            } else {
                System.out.println("Salvamento falhou");
                mostrarErroPopup("Erro ao Salvar", "Não foi possível cadastrar o " + tipo.toLowerCase() + ".\nVerifique os logs para mais detalhes.");
            }
        } catch (Exception e) {
            System.err.println("EXCEÇÃO no método salvar: " + e.getMessage());
            e.printStackTrace();
            mostrarErroPopup("Erro Inesperado", "Ocorreu um erro ao processar os dados:\n" + e.getMessage());
        }
    }
    
    private boolean validarCamposComuns() {
        System.out.println("Validando campos comuns...");
        
        if (txtNome.getText().trim().isEmpty()) {
            System.out.println("Nome vazio");
            mostrarErroPopup("Nome Obrigatório", "Por favor, preencha o nome completo.");
            return false;
        }
        
        if (txtNome.getText().trim().length() < 3) {
            mostrarErroPopup("Nome Inválido", "O nome deve ter no mínimo 3 caracteres.");
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            System.out.println("Email vazio");
            mostrarErroPopup("Email Obrigatório", "Por favor, preencha o email.");
            return false;
        }
        
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("Email inválido: " + txtEmail.getText());
            mostrarErroPopup("Email Inválido", "Por favor, insira um email válido.\nExemplo: usuario@empresa.com");
            return false;
        }
        
        // Validar telefone se preenchido
        if (!txtTelefone.getText().trim().isEmpty()) {
            String telefoneNumeros = txtTelefone.getText().replaceAll("[^0-9]", "");
            if (telefoneNumeros.length() != 11) {
                System.out.println("Telefone incompleto: " + telefoneNumeros.length() + " dígitos");
                mostrarErroPopup("Telefone Inválido", "O telefone deve ter 11 dígitos.\nFormato: (11) 99999-9999");
                return false;
            }
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
            mostrarErroPopup("Username Obrigatório", "Por favor, preencha o username para login.");
            return false;
        }
        
        if (txtUsername.getText().length() < 4) {
            System.out.println("Username muito curto: " + txtUsername.getText().length());
            mostrarErroPopup("Username Inválido", "O username deve ter no mínimo 4 caracteres.");
            return false;
        }
        
        if (txtSenha.getText().trim().isEmpty()) {
            System.out.println("Senha vazia!");
            mostrarErroPopup("Senha Obrigatória", "Por favor, defina uma senha.");
            return false;
        }
        
        if (txtSenha.getText().length() < 6) {
            System.out.println("Senha muito curta: " + txtSenha.getText().length());
            mostrarErroPopup("Senha Inválida", "A senha deve ter no mínimo 6 caracteres.");
            return false;
        }
        
        System.out.println("Validações de administrador OK, criando objeto...");
        
        try {
            // Criar objeto Adm
            Adm adm = new Adm();
            adm.setFullName(txtNome.getText().trim());
            adm.setUsername(txtUsername.getText().trim());
            
            String senhaOriginal = txtSenha.getText();
            String senhaCriptografada = BCrypt.hashpw(senhaOriginal, BCrypt.gensalt());
            adm.setPasswordHash(senhaCriptografada);
            
            adm.setEmail(txtEmail.getText().trim());
            adm.setPhoneNumber(txtTelefone.getText().replaceAll("[^0-9]", ""));
            adm.setActive(chkAtivo.isSelected());
            adm.setNotes(txtObservacoes.getText().trim());
            
            System.out.println("=== CONTROLLER: Tentando salvar administrador ===");
            System.out.println("Nome: " + adm.getFullName());
            System.out.println("Username: " + adm.getUsername());
            System.out.println("Senha Original: " + senhaOriginal);
            System.out.println("Senha Criptografada: " + senhaCriptografada);
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
            mostrarErroPopup("Erro ao Salvar", "Erro ao processar dados do administrador:\n" + e.getMessage());
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
            mostrarErroPopup("CPF Obrigatório", "Por favor, preencha o CPF do funcionário.");
            return false;
        }
        
        if (!validarCPF(txtCpf.getText().trim())) {
            System.out.println("CPF inválido!");
            mostrarErroPopup("CPF Inválido", "O CPF informado não é válido.\nVerifique os números digitados.");
            return false;
        }
        
        if (dpDataContratacao.getValue() == null) {
            System.out.println("Data de contratação não selecionada!");
            mostrarErroPopup("Data Obrigatória", "Por favor, selecione a data de contratação.");
            return false;
        }
        
        if (dpDataContratacao.getValue().isAfter(LocalDate.now())) {
            System.out.println("Data de contratação futura!");
            mostrarErroPopup("Data Inválida", "A data de contratação não pode ser uma data futura.");
            return false;
        }
        
        System.out.println("Validações de funcionário OK, criando objeto...");
        
        try {
            // Criar objeto Employee
            Employee employee = new Employee();
            employee.setName(txtNome.getText().trim());
            employee.setEmail(txtEmail.getText().trim());
            employee.setPhoneNumber(txtTelefone.getText().replaceAll("[^0-9]", ""));
            employee.setCpf(txtCpf.getText().replaceAll("[^0-9]", ""));
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
                    if (salario.compareTo(BigDecimal.ZERO) < 0) {
                        mostrarErroPopup("Salário Inválido", "O salário base não pode ser negativo.");
                        return false;
                    }
                    employee.setBaseSalary(salario);
                } catch (NumberFormatException e) {
                    System.out.println("Salário inválido: " + txtSalarioBase.getText());
                    mostrarErroPopup("Salário Inválido", "Por favor, insira um valor numérico válido.\nExemplo: 2500.00");
                    return false;
                }
            }
            
            // Comissão (opcional)
            if (!txtComissao.getText().trim().isEmpty()) {
                try {
                    BigDecimal comissao = new BigDecimal(txtComissao.getText().trim().replace(",", "."));
                    if (comissao.compareTo(BigDecimal.ZERO) < 0 || comissao.compareTo(BigDecimal.ONE) > 0) {
                        System.out.println("Comissão fora do range: " + comissao);
                        mostrarErroPopup("Comissão Inválida", "A comissão deve estar entre 0 e 1.\nExemplo: 0.30 = 30%");
                        return false;
                    }
                    employee.setCommissionRate(comissao);
                } catch (NumberFormatException e) {
                    System.out.println("Comissão inválida: " + txtComissao.getText());
                    mostrarErroPopup("Comissão Inválida", "Por favor, insira um valor decimal válido.\nExemplo: 0.30");
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
            mostrarErroPopup("Erro ao Salvar", "Erro ao processar dados do funcionário:\n" + e.getMessage());
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
    
    private void mostrarErroPopup(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ " + titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
