package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.math.BigDecimal;

import com.example.backends.classes.*;
import com.example.backends.database.data.*;
import com.example.backends.enums.AppointmentStatus;

public class AgendamentosController {

    @FXML private VBox listaAgendamentos;
    @FXML private TextField txtBuscar;

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    private ObservableList<Appointment> agendamentos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        carregarAgendamentos();
        configurarBusca();
    }
    
    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarAgendamentos(newValue);
        });
    }
    
    private void filtrarAgendamentos(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            exibirAgendamentos(agendamentos);
        } else {
            List<Appointment> filtrados = agendamentos.stream()
                .filter(a -> {
                    try {
                        Client client = ClientDAO.getClientByID(a.getClientId());
                        Service service = ServicesDAO.getServiceByID(a.getServiceIds() != null && !a.getServiceIds().isEmpty() ? a.getServiceIds().get(0) : null);
                        
                        return (client != null && client.getName().toLowerCase().contains(filtro.toLowerCase())) ||
                               (service != null && service.getName().toLowerCase().contains(filtro.toLowerCase()));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
                
            exibirAgendamentos(filtrados);
        }
    }


    @FXML
    public void novoAgendamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModalNovoAgendamento.fxml"));
            Parent root = loader.load();

            ModalNovoAgendamentoController controller = loader.getController();
            controller.setCallback(this::adicionarAgendamento);

            Stage stage = new Stage();
            stage.setTitle("Novo Agendamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir modal de agendamento: " + e.getMessage());
        }
    }

    private void adicionarAgendamento(Appointment appointment) {
        carregarAgendamentos();
    }

    private void carregarAgendamentos() {
        try {
            System.out.println("Carregando agendamentos do banco...");
            List<Appointment> appointmentsList = AppointmentDAO.getAllAppointments();
            
            agendamentos.clear();
            agendamentos.addAll(appointmentsList);
            
            exibirAgendamentos(appointmentsList);
            
            System.out.println("✅ " + appointmentsList.size() + " agendamentos carregados");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar agendamentos: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro ao carregar agendamentos do banco de dados.");
        }
    }
    
    private void exibirAgendamentos(List<Appointment> appointments) {
        listaAgendamentos.getChildren().clear();

        if (appointments.isEmpty()) {
            Label l = new Label("Nenhum agendamento encontrado.");
            l.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 16px;");
            listaAgendamentos.getChildren().add(l);
            return;
        }

        for (Appointment appointment : appointments) {
            listaAgendamentos.getChildren().add(criarCardAgendamento(appointment));
        }
    }

    private HBox criarCardAgendamento(Appointment appointment) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 18; -fx-background-radius: 12;");

        VBox info = new VBox(6);

        try {
            // Buscar dados relacionados
            Client client = ClientDAO.getClientByID(appointment.getClientId());
            Employee employee = EmployeeDAO.getEmployeeByID(appointment.getStylistId());
            
            // Nome do cliente
            Label nomeCliente = new Label("👤 " + (client != null ? client.getName() : "Cliente não encontrado"));
            nomeCliente.setStyle("-fx-font-size: 18; -fx-text-fill: white; -fx-font-weight: bold;");
            
            // Funcionário
            Label nomeFuncionario = new Label("👨‍🔧 Funcionário: " + (employee != null ? employee.getName() : "Não definido"));
            nomeFuncionario.setStyle("-fx-text-fill: #cccccc;");
            
            // Buscar serviços do agendamento
            List<Long> serviceIds = AppointmentDAO.getServiceIDsByAppointment(appointment.getId());
            String servicos = "Serviços não encontrados";
            if (!serviceIds.isEmpty()) {
                Service service = ServicesDAO.getServiceByID(serviceIds.get(0));
                servicos = service != null ? service.getName() : "Serviço não encontrado";
                if (serviceIds.size() > 1) {
                    servicos += " (+ " + (serviceIds.size() - 1) + " outros)";
                }
            }
            
            Label labelServicos = new Label("💈 Serviços: " + servicos);
            labelServicos.setStyle("-fx-text-fill: #cccccc;");
            
            // Data e hora
            Label dataHora = new Label(
                    "🕒 " + appointment.getAppointmentDateTime().format(formatoData) + 
                    " às " + appointment.getAppointmentDateTime().format(formatoHora)
            );
            dataHora.setStyle("-fx-text-fill: #bbbbbb;");
            
            // Status
            Label status = new Label("🟢 Status: " + appointment.getStatus().getDisplayName());
            status.setStyle("-fx-text-fill: " + appointment.getStatus().getColor() + ";");
            
            // Preço
            Label preco = new Label("💵 R$ " + String.format("%.2f", appointment.getTotalPrice() != null ? appointment.getTotalPrice() : BigDecimal.ZERO));
            preco.setStyle("-fx-text-fill: #90ee90;");

            info.getChildren().addAll(nomeCliente, nomeFuncionario, labelServicos, dataHora, status, preco);
            
        } catch (Exception e) {
            System.err.println("Erro ao criar card do agendamento: " + e.getMessage());
            Label erro = new Label("Erro ao carregar dados do agendamento");
            erro.setStyle("-fx-text-fill: #ff6b6b;");
            info.getChildren().add(erro);
        }

        // Botões
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-editar");
        editar.setOnAction(e -> editarAgendamento(appointment));

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-excluir");
        excluir.setOnAction(e -> excluirAgendamento(appointment));
        
        Button marcarConcluido = new Button("Concluir");
        marcarConcluido.getStyleClass().add("btn-concluir");
        marcarConcluido.setOnAction(e -> marcarComoConcluido(appointment));
        
        VBox botoes = new VBox(5, editar, excluir);
        if (appointment.getStatus() == AppointmentStatus.AGENDADO) {
            botoes.getChildren().add(0, marcarConcluido);
        }

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        card.getChildren().addAll(info, espaco, botoes);

        return card;
    }
    
    private void editarAgendamento(Appointment appointment) {
        try {
            // Carregar o FXML do modal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModalNovoAgendamento.fxml"));
            Parent root = loader.load();
            
            // Obter o controller do modal
            ModalNovoAgendamentoController modalController = loader.getController();
            
            // Configurar callback para quando o agendamento for salvo
            modalController.setCallback(agendamentoAtualizado -> {
                System.out.println("✅ Agendamento editado com sucesso!");
                carregarAgendamentos(); // Recarregar a lista
            });
            
            // Configurar o modal para modo de edição
            modalController.configurarParaEdicao(appointment);
            
            // Criar e mostrar o stage
            Stage stage = new Stage();
            stage.setTitle("Editar Agendamento");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(listaAgendamentos.getScene().getWindow());
            
            // Mostrar o modal
            stage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao abrir modal de edição: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro ao abrir editor de agendamento.");
        }
    }
    
    private void excluirAgendamento(Appointment appointment) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir agendamento");
        confirmacao.setContentText("Tem certeza que deseja excluir este agendamento?");
        
        if (confirmacao.showAndWait().get() == ButtonType.OK) {
            try {
                boolean sucesso = AppointmentDAO.delete(appointment.getId());
                
                if (sucesso) {
                    carregarAgendamentos();
                    mostrarSucesso("Agendamento excluído com sucesso!");
                } else {
                    mostrarErro("Erro ao excluir agendamento.");
                }
                
            } catch (Exception e) {
                System.err.println("Erro ao excluir agendamento: " + e.getMessage());
                mostrarErro("Erro inesperado: " + e.getMessage());
            }
        }
    }
    
    private void marcarComoConcluido(Appointment appointment) {
        try {
            boolean sucesso = AppointmentDAO.updateStatus(appointment.getId(), AppointmentStatus.CONCLUIDO);
            
            if (sucesso) {
                carregarAgendamentos();
                mostrarSucesso("Agendamento marcado como concluído!");
            } else {
                mostrarErro("Erro ao atualizar status do agendamento.");
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao marcar agendamento como concluído: " + e.getMessage());
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
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
    
    private void mostrarInfo(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
