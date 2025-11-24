package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.Pos;

import com.example.backends.classes.Client;
import com.example.backends.classes.Employee;
import com.example.backends.classes.Service;
import com.example.backends.classes.Appointment;
import com.example.backends.database.data.ClientDAO;
import com.example.backends.database.data.EmployeeDAO;
import com.example.backends.database.data.ServicesDAO;
import com.example.backends.database.data.AppointmentDAO;
import com.example.backends.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class EditarAgendamentoController {

    @FXML private ComboBox<Client> cmbCliente;
    @FXML private ComboBox<Employee> cmbFuncionario;
    @FXML private ComboBox<Service> cmbServico;
    @FXML private ComboBox<AppointmentStatus> cmbStatus;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHora;
    @FXML private Label lblPrecoTotal;
    @FXML private TextArea txtObservacoes;
    @FXML private FlowPane flowServicos;
    @FXML private Button btnAdicionarServico;

    // Lista de serviços selecionados
    private List<Service> servicosSelecionados = new ArrayList<>();
    
    // Agendamento sendo editado
    private Appointment agendamentoOriginal;
    
    // Callback para atualizar lista após salvar
    private Consumer<Appointment> callback;

    public void setCallback(Consumer<Appointment> callback) {
        this.callback = callback;
    }

    @FXML
    private void initialize() {
        carregarDados();
        configurarEventos();
    }

    private void carregarDados() {
        // Carregar dados em background para não travar a UI
        new Thread(() -> {
            final List<Client> clientes = ClientDAO.getAllClients();
            final List<Employee> funcionarios = EmployeeDAO.getAllEmployees();
            final List<Service> servicos = ServicesDAO.getAllServices();

            Platform.runLater(() -> {
                cmbCliente.getItems().setAll(clientes);
                cmbFuncionario.getItems().setAll(funcionarios);
                cmbServico.getItems().setAll(servicos);
                
                // Carregar todos os status possíveis
                cmbStatus.getItems().setAll(AppointmentStatus.values());
            });
        }).start();
    }

    private void configurarEventos() {
        // Configurar hint de hora
        txtHora.setPromptText("Ex: 14:30");
    }

    /**
     * Carrega os dados do agendamento para edição
     */
    public void carregarAgendamento(Appointment agendamento) {
        this.agendamentoOriginal = agendamento;
        
        new Thread(() -> {
            // Buscar cliente
            final Client cliente = ClientDAO.getClientByID(agendamento.getClientId());
            
            // Buscar funcionário
            Employee funcionario = null;
            if (agendamento.getStylistId() != null) {
                funcionario = EmployeeDAO.getEmployeeByID(agendamento.getStylistId());
            }
            final Employee funcFinal = funcionario;
            
            // Buscar serviços selecionados
            final List<Service> servicosCarregados = new ArrayList<>();
            if (agendamento.getServiceIds() != null) {
                for (Long serviceId : agendamento.getServiceIds()) {
                    Service s = ServicesDAO.getServiceByID(serviceId);
                    if (s != null) {
                        servicosCarregados.add(s);
                    }
                }
            }
            
            Platform.runLater(() -> {
                // Preencher cliente
                cmbCliente.setValue(cliente);
                
                // Preencher funcionário
                if (funcFinal != null) {
                    cmbFuncionario.setValue(funcFinal);
                }
                
                // Preencher data e hora
                dpData.setValue(agendamento.getAppointmentDateTime().toLocalDate());
                txtHora.setText(agendamento.getAppointmentDateTime().toLocalTime().toString());
                
                // Preencher status
                cmbStatus.setValue(agendamento.getStatus());
                
                // Preencher observações
                txtObservacoes.setText(agendamento.getNotes());
                
                // Limpar serviços anteriores (se houver)
                servicosSelecionados.clear();
                flowServicos.getChildren().clear();
                
                // Adicionar serviços carregados do banco
                for (Service s : servicosCarregados) {
                    servicosSelecionados.add(s);
                    criarTagServico(s);
                }
                
                // Atualizar preço total
                atualizarPrecoTotal();
            });
        }).start();
    }

    /**
     * Adiciona serviço selecionado à lista e cria tag visual
     */
    @FXML
    private void adicionarServico() {
        Service servico = cmbServico.getValue();
        
        if (servico == null) {
            mostrarAlerta("Selecione um serviço para adicionar!", Alert.AlertType.WARNING);
            return;
        }
        
        // Verificar se já foi adicionado
        if (servicosSelecionados.contains(servico)) {
            mostrarAlerta("Este serviço já foi adicionado!", Alert.AlertType.WARNING);
            return;
        }
        
        // Adicionar à lista
        servicosSelecionados.add(servico);
        
        // Criar tag visual
        criarTagServico(servico);
        
        // Atualizar preço total
        atualizarPrecoTotal();
        
        // Limpar seleção
        cmbServico.setValue(null);
    }

    /**
     * Cria tag visual para o serviço
     */
    private void criarTagServico(Service servico) {
        HBox tag = new HBox(10);
        tag.setAlignment(Pos.CENTER_LEFT);
        tag.setStyle(
            "-fx-background-color: #10b981; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 8 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        
        // Nome do serviço
        Label lblNome = new Label(servico.getName());
        lblNome.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        // Preço
        Label lblPreco = new Label(String.format("R$ %.2f", servico.getPrice()));
        lblPreco.setStyle("-fx-text-fill: #e0f2fe; -fx-font-size: 12px;");
        
        // Botão remover
        Button btnRemover = new Button("✕");
        btnRemover.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 0 5;"
        );
        btnRemover.setOnAction(e -> removerServico(servico, tag));
        
        tag.getChildren().addAll(lblNome, lblPreco, btnRemover);
        flowServicos.getChildren().add(tag);
    }

    /**
     * Remove serviço da lista e da interface
     */
    private void removerServico(Service servico, HBox tag) {
        servicosSelecionados.remove(servico);
        flowServicos.getChildren().remove(tag);
        atualizarPrecoTotal();
    }

    /**
     * Atualiza o preço total baseado nos serviços selecionados
     */
    private void atualizarPrecoTotal() {
        BigDecimal total = servicosSelecionados.stream()
            .map(Service::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        lblPrecoTotal.setText(String.format("R$ %.2f", total));
    }

    @FXML
    private void salvarAgendamento() {
        // Validações
        if (cmbCliente.getValue() == null) {
            mostrarAlerta("Selecione um cliente!", Alert.AlertType.WARNING);
            return;
        }
        
        if (servicosSelecionados.isEmpty()) {
            mostrarAlerta("Adicione pelo menos um serviço!", Alert.AlertType.WARNING);
            return;
        }
        
        if (dpData.getValue() == null) {
            mostrarAlerta("Selecione uma data!", Alert.AlertType.WARNING);
            return;
        }
        
        if (txtHora.getText().isEmpty()) {
            mostrarAlerta("Digite o horário!", Alert.AlertType.WARNING);
            return;
        }
        
        if (cmbStatus.getValue() == null) {
            mostrarAlerta("Selecione o status do agendamento!", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            // Atualizar dados do agendamento original
            agendamentoOriginal.setClientId(cmbCliente.getValue().getId());
            
            if (cmbFuncionario.getValue() != null) {
                agendamentoOriginal.setStylistId(cmbFuncionario.getValue().getId());
            } else {
                agendamentoOriginal.setStylistId(null);
            }
            
            // Data e hora
            LocalDate data = dpData.getValue();
            LocalTime hora = LocalTime.parse(txtHora.getText());
            agendamentoOriginal.setAppointmentDateTime(LocalDateTime.of(data, hora));
            
            // Status
            agendamentoOriginal.setStatus(cmbStatus.getValue());
            
            // Preço total
            agendamentoOriginal.setTotalPrice(servicosSelecionados.stream()
                .map(Service::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            // Observações
            agendamentoOriginal.setNotes(txtObservacoes.getText());
            
            // IDs dos serviços
            List<Long> serviceIds = new ArrayList<>();
            for (Service s : servicosSelecionados) {
                serviceIds.add(s.getId());
            }
            agendamentoOriginal.setServiceIds(serviceIds);
            
            // Atualizar no banco
            boolean sucesso = AppointmentDAO.update(agendamentoOriginal);
            
            if (sucesso) {
                mostrarAlerta("✅ Agendamento atualizado com sucesso!", Alert.AlertType.INFORMATION);
                
                if (callback != null) {
                    callback.accept(agendamentoOriginal);
                }
                
                fecharModal();
            } else {
                mostrarAlerta("❌ Erro ao atualizar agendamento!", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("❌ Erro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void fecharModal() {
        Stage stage = (Stage) cmbCliente.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo, mensagem, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
