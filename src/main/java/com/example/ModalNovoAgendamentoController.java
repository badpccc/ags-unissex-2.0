package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import com.example.backends.classes.*;
import com.example.backends.database.data.*;
import com.example.backends.enums.AppointmentStatus;

public class ModalNovoAgendamentoController {

    @FXML private ComboBox<Client> cmbCliente;
    @FXML private ComboBox<Employee> cmbFuncionario;
    @FXML private ComboBox<Service> cmbServico;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHora;
    @FXML private TextArea txtObservacoes;
    @FXML private Label lblPrecoTotal;

    private Consumer<Appointment> callback;
    private Service servicoSelecionado;
    private Appointment agendamentoParaEdicao; // Para saber se estamos editando
    private boolean modoEdicao = false;

    @FXML
    public void initialize() {
        carregarClientes();
        carregarFuncionarios();
        carregarServicos();
        configurarEventos();
        
        // Configurar data padrão para hoje
        dpData.setValue(LocalDate.now());
        
        // Configurar hora padrão
        txtHora.setText("09:00");
        
        // Máscara para hora (HH:MM)
        txtHora.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,2}:?\\d{0,2}")) {
                txtHora.setText(oldValue);
            }
        });
    }
    
    private void carregarClientes() {
        try {
            List<Client> clientes = ClientDAO.getAllClients();
            ObservableList<Client> clientesObs = FXCollections.observableArrayList(clientes);
            cmbCliente.setItems(clientesObs);
            
            // Configurar como o cliente será exibido
            cmbCliente.setCellFactory(param -> new ListCell<Client>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setText(null);
                    } else {
                        setText(client.getName() + " - " + client.getPhoneNumber());
                    }
                }
            });
            
            cmbCliente.setButtonCell(new ListCell<Client>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setText("Selecione um cliente");
                    } else {
                        setText(client.getName());
                    }
                }
            });
            
            System.out.println("✅ " + clientes.size() + " clientes carregados");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar clientes: " + e.getMessage());
            mostrarErro("Erro ao carregar lista de clientes.");
        }
    }
    
    private void carregarFuncionarios() {
        try {
            List<Employee> funcionarios = EmployeeDAO.getAllEmployees();
            ObservableList<Employee> funcionariosObs = FXCollections.observableArrayList(funcionarios);
            cmbFuncionario.setItems(funcionariosObs);
            
            // Configurar como o funcionário será exibido
            cmbFuncionario.setCellFactory(param -> new ListCell<Employee>() {
                @Override
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);
                    if (empty || employee == null) {
                        setText(null);
                    } else {
                        setText(employee.getName() + " - " + employee.getPosition());
                    }
                }
            });
            
            cmbFuncionario.setButtonCell(new ListCell<Employee>() {
                @Override
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);
                    if (empty || employee == null) {
                        setText("Selecione um funcionário");
                    } else {
                        setText(employee.getName());
                    }
                }
            });
            
            System.out.println("✅ " + funcionarios.size() + " funcionários carregados");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar funcionários: " + e.getMessage());
            mostrarErro("Erro ao carregar lista de funcionários.");
        }
    }
    
    private void carregarServicos() {
        try {
            List<Service> servicos = ServicesDAO.getAllServices();
            ObservableList<Service> servicosObs = FXCollections.observableArrayList(servicos);
            cmbServico.setItems(servicosObs);
            
            // Configurar como o serviço será exibido
            cmbServico.setCellFactory(param -> new ListCell<Service>() {
                @Override
                protected void updateItem(Service service, boolean empty) {
                    super.updateItem(service, empty);
                    if (empty || service == null) {
                        setText(null);
                    } else {
                        setText(service.getName() + " - R$ " + String.format("%.2f", service.getPrice()));
                    }
                }
            });
            
            cmbServico.setButtonCell(new ListCell<Service>() {
                @Override
                protected void updateItem(Service service, boolean empty) {
                    super.updateItem(service, empty);
                    if (empty || service == null) {
                        setText("Selecione um serviço");
                    } else {
                        setText(service.getName());
                    }
                }
            });
            
            System.out.println("✅ " + servicos.size() + " serviços carregados");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar serviços: " + e.getMessage());
            mostrarErro("Erro ao carregar lista de serviços.");
        }
    }
    
    private void configurarEventos() {
        // Atualizar preço quando selecionar serviço
        cmbServico.setOnAction(e -> {
            Service service = cmbServico.getValue();
            if (service != null) {
                servicoSelecionado = service;
                lblPrecoTotal.setText("R$ " + String.format("%.2f", service.getPrice()));
                System.out.println("Serviço selecionado: " + service.getName() + " - R$ " + service.getPrice());
            } else {
                servicoSelecionado = null;
                lblPrecoTotal.setText("R$ 0,00");
            }
        });
        
        // Configurar listeners para os outros ComboBox também
        cmbCliente.setOnAction(e -> {
            Client client = cmbCliente.getValue();
            if (client != null) {
                System.out.println("Cliente selecionado: " + client.getName());
            }
        });
        
        cmbFuncionario.setOnAction(e -> {
            Employee employee = cmbFuncionario.getValue();
            if (employee != null) {
                System.out.println("Funcionário selecionado: " + employee.getName());
            }
        });
    }

    public void setCallback(Consumer<Appointment> callback) {
        this.callback = callback;
    }
    
    public void configurarParaEdicao(Appointment appointment) {
        this.agendamentoParaEdicao = appointment;
        this.modoEdicao = true;
        
        try {
            // Buscar os objetos relacionados
            final Client cliente = ClientDAO.getClientByID(appointment.getClientId());
            final Employee funcionario = EmployeeDAO.getEmployeeByID(appointment.getStylistId());
            
            // Buscar o primeiro serviço (assumindo que há pelo menos um)
            final Service servico;
            if (appointment.getServiceIds() != null && !appointment.getServiceIds().isEmpty()) {
                servico = ServicesDAO.getServiceByID(appointment.getServiceIds().get(0));
            } else {
                servico = null;
            }
            
            // Aguardar os ComboBox serem carregados e depois preencher
            Platform.runLater(() -> {
                // Preencher os campos
                if (cliente != null) {
                    // Encontrar o cliente na lista do ComboBox
                    for (Client c : cmbCliente.getItems()) {
                        if (c.getId().equals(cliente.getId())) {
                            cmbCliente.setValue(c);
                            break;
                        }
                    }
                }
                
                if (funcionario != null) {
                    // Encontrar o funcionário na lista do ComboBox
                    for (Employee e : cmbFuncionario.getItems()) {
                        if (e.getId().equals(funcionario.getId())) {
                            cmbFuncionario.setValue(e);
                            break;
                        }
                    }
                }
                
                if (servico != null) {
                    // Encontrar o serviço na lista do ComboBox
                    for (Service s : cmbServico.getItems()) {
                        if (s.getId().equals(servico.getId())) {
                            cmbServico.setValue(s);
                            // Atualizar o serviço selecionado e preço
                            servicoSelecionado = s;
                            lblPrecoTotal.setText("R$ " + String.format("%.2f", s.getPrice()));
                            break;
                        }
                    }
                }
                
                // Configurar data e hora
                if (appointment.getAppointmentDateTime() != null) {
                    dpData.setValue(appointment.getAppointmentDateTime().toLocalDate());
                    txtHora.setText(appointment.getAppointmentDateTime().toLocalTime().toString());
                }
                
                // Configurar observações
                if (appointment.getNotes() != null) {
                    txtObservacoes.setText(appointment.getNotes());
                }
            });
            
            System.out.println("✅ Agendamento carregado para edição: ID " + appointment.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar dados do agendamento: " + e.getMessage());
            mostrarErro("Erro ao carregar dados do agendamento: " + e.getMessage());
        }
    }

    @FXML
    private void fecharModal() {
        Stage stage = (Stage) cmbCliente.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvarAgendamento() {
        if (!validarCampos()) {
            return;
        }

        try {
            Appointment appointment;
            boolean isNovoAgendamento = !modoEdicao;
            
            if (modoEdicao) {
                // Modo edição - usar o agendamento existente
                appointment = agendamentoParaEdicao;
            } else {
                // Modo criação - criar novo agendamento
                appointment = new Appointment();
                appointment.setStatus(AppointmentStatus.AGENDADO);
            }
            
            // Definir cliente
            Client clienteSelecionado = cmbCliente.getValue();
            appointment.setClientId(clienteSelecionado.getId());
            
            // Definir funcionário
            Employee funcionarioSelecionado = cmbFuncionario.getValue();
            appointment.setStylistId(funcionarioSelecionado.getId());
            
            // Definir data e hora
            LocalDate data = dpData.getValue();
            LocalTime hora = LocalTime.parse(txtHora.getText());
            appointment.setAppointmentDateTime(LocalDateTime.of(data, hora));
            
            // Definir preço total
            appointment.setTotalPrice(servicoSelecionado.getPrice());
            
            // Definir observações
            appointment.setNotes(txtObservacoes.getText());
            
            // Atualizar timestamp de modificação
            appointment.setUpdatedAt(LocalDateTime.now());
            
            // Salvar no banco
            boolean sucesso;
            if (isNovoAgendamento) {
                sucesso = AppointmentDAO.insert(appointment);
            } else {
                sucesso = AppointmentDAO.update(appointment);
            }
            
            if (sucesso) {
                String acao = isNovoAgendamento ? "criado" : "atualizado";
                System.out.println("✅ Agendamento " + acao + " com sucesso! ID: " + appointment.getId());
                
                // Gerenciar serviços associados ao agendamento
                boolean servicosAtualizados = false;
                try {
                    List<Long> serviceIds = List.of(servicoSelecionado.getId());
                    List<BigDecimal> servicePrices = List.of(servicoSelecionado.getPrice());
                    
                    if (isNovoAgendamento) {
                        // Para novo agendamento, apenas adicionar serviços
                        servicosAtualizados = AppointmentDAO.addServicesToAppointment(appointment.getId(), serviceIds, servicePrices);
                    } else {
                        // Para edição, primeiro remover serviços antigos
                        System.out.println("🔄 Removendo serviços antigos do agendamento ID: " + appointment.getId());
                        boolean servicosRemovidos = AppointmentDAO.removeServicesFromAppointment(appointment.getId());
                        
                        if (servicosRemovidos) {
                            System.out.println("✅ Serviços antigos removidos com sucesso");
                            // Depois adicionar os novos serviços
                            System.out.println("➕ Adicionando novo serviço: " + servicoSelecionado.getName());
                            servicosAtualizados = AppointmentDAO.addServicesToAppointment(appointment.getId(), serviceIds, servicePrices);
                        } else {
                            System.err.println("❌ Erro ao remover serviços antigos");
                            mostrarErro("Erro ao atualizar serviços do agendamento.");
                            return;
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("❌ Erro ao gerenciar serviços: " + e.getMessage());
                    e.printStackTrace();
                    mostrarErro("Agendamento salvo, mas houve erro ao associar serviços.");
                    return;
                }
                
                if (servicosAtualizados) {
                    System.out.println("✅ Serviços atualizados com sucesso");
                    mostrarSucesso("Agendamento " + acao + " com sucesso!");
                    
                    // Executar callback
                    if (callback != null) {
                        callback.accept(appointment);
                    }
                    
                    fecharModal();
                } else {
                    mostrarErro("Agendamento salvo, mas houve erro ao associar serviços.");
                }
                
            } else {
                String acao = isNovoAgendamento ? "salvar" : "atualizar";
                mostrarErro("Erro ao " + acao + " agendamento no banco de dados.");
            }
            
        } catch (Exception e) {
            String acao = modoEdicao ? "editar" : "salvar";
            System.err.println("Erro ao " + acao + " agendamento: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }
    
    private boolean validarCampos() {
        if (cmbCliente.getValue() == null) {
            mostrarErro("Selecione um cliente.");
            cmbCliente.requestFocus();
            return false;
        }
        
        if (cmbFuncionario.getValue() == null) {
            mostrarErro("Selecione um funcionário.");
            cmbFuncionario.requestFocus();
            return false;
        }
        
        if (cmbServico.getValue() == null) {
            mostrarErro("Selecione um serviço.");
            cmbServico.requestFocus();
            return false;
        }
        
        if (dpData.getValue() == null) {
            mostrarErro("Selecione uma data.");
            dpData.requestFocus();
            return false;
        }
        
        if (dpData.getValue().isBefore(LocalDate.now())) {
            mostrarErro("A data não pode ser no passado.");
            dpData.requestFocus();
            return false;
        }
        
        if (txtHora.getText() == null || txtHora.getText().trim().isEmpty()) {
            mostrarErro("Informe o horário.");
            txtHora.requestFocus();
            return false;
        }
        
        try {
            LocalTime.parse(txtHora.getText());
        } catch (Exception e) {
            mostrarErro("Horário inválido. Use o formato HH:MM (ex: 14:30)");
            txtHora.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
