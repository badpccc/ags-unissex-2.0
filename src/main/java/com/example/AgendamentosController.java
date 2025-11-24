package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;

import com.example.backends.classes.Appointment;
import com.example.backends.classes.Client;
import com.example.backends.classes.Service;
import com.example.backends.database.data.AppointmentDAO;
import com.example.backends.database.data.ClientDAO;
import com.example.backends.database.data.ServicesDAO;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgendamentosController {

    @FXML private VBox listaAgendamentos;

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        carregarAgendamentos();
    }


    // 🔹 Abrir modal igual ao UsuariosController
    @FXML
    public void novoAgendamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModalNovoAgendamento.fxml"));
            Parent root = loader.load();

            // Controller do modal
            ModalNovoAgendamentoController controller = loader.getController();
            controller.setCallback(this::adicionarAgendamento);

            Stage stage = new Stage();
            stage.setTitle("Novo Agendamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true); // Permitir redimensionamento
            stage.setMinWidth(700);
            stage.setMinHeight(600);

            stage.showAndWait();

            carregarAgendamentos();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adicionar agendamento e recarregar lista
    private void adicionarAgendamento(Appointment a) {
        carregarAgendamentos();
    }

    // Carregar agendamentos do banco de dados
    private void carregarAgendamentos() {
        new Thread(() -> {
            final List<Appointment> agendamentos = AppointmentDAO.getAllAppointments();
            
            Platform.runLater(() -> {
                listaAgendamentos.getChildren().clear();

                if (agendamentos == null || agendamentos.isEmpty()) {
                    Label l = new Label("Nenhum agendamento encontrado.");
                    l.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 16px;");
                    listaAgendamentos.getChildren().add(l);
                    return;
                }

                for (Appointment ag : agendamentos) {
                    listaAgendamentos.getChildren().add(criarCardAgendamento(ag));
                }
            });
        }).start();
    }

    private HBox criarCardAgendamento(Appointment ag) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 18; -fx-background-radius: 12;");

        VBox info = new VBox(6);

        // Buscar nome do cliente
        Client cliente = ClientDAO.getClientByID(ag.getClientId());
        String nomeCliente = cliente != null ? cliente.getName() : "Cliente não encontrado";
        
        Label nome = new Label("👤 " + nomeCliente);
        nome.setStyle("-fx-font-size: 18; -fx-text-fill: white; -fx-font-weight: bold;");

        // Buscar serviços (múltiplos)
        List<String> nomesServicos = new ArrayList<>();
        if (ag.getServiceIds() != null) {
            for (Long serviceId : ag.getServiceIds()) {
                Service s = ServicesDAO.getServiceByID(serviceId);
                if (s != null) {
                    nomesServicos.add(s.getName());
                }
            }
        }
        String servicosTexto = nomesServicos.isEmpty() ? "Nenhum serviço" : String.join(", ", nomesServicos);
        
        Label servico = new Label("💈 Serviços: " + servicosTexto);
        servico.setStyle("-fx-text-fill: #cccccc;");

        Label data = new Label(
                "🕒 " + ag.getAppointmentDateTime().format(formatoData) + 
                " às " + ag.getAppointmentDateTime().format(formatoHora)
        );
        data.setStyle("-fx-text-fill: #bbbbbb;");

        Label preco = new Label(String.format("💵 R$ %.2f", ag.getTotalPrice()));
        preco.setStyle("-fx-text-fill: #90ee90;");

        Label status = new Label("📋 Status: " + ag.getStatus().name());
        status.setStyle("-fx-text-fill: #fbbf24;");

        info.getChildren().addAll(nome, servico, data, preco, status);

// 🔵 Botão editar
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-editar");
        editar.setOnAction(e -> abrirEdicao(ag));

// 🔴 Botão excluir
        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-excluir");
        excluir.setOnAction(e -> {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setHeaderText("Confirmar exclusão");
            confirmacao.setContentText("Deseja realmente excluir este agendamento?");
            confirmacao.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    AppointmentDAO.delete(ag.getId());
                    carregarAgendamentos();
                }
            });
        });


        // Caixa de botões (✔ AGORA CORRETO)
        HBox botoes = new HBox(10, editar, excluir);

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        card.getChildren().addAll(info, espaco, botoes);

        return card;
    }

    private void abrirEdicao(Appointment ag) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarAgendamento.fxml"));
            Parent root = loader.load();

            // Controller do modal
            EditarAgendamentoController controller = loader.getController();
            controller.carregarAgendamento(ag);
            controller.setCallback(atualizado -> carregarAgendamentos());

            Stage stage = new Stage();
            stage.setTitle("Editar Agendamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setMinWidth(700);
            stage.setMinHeight(620);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setHeaderText("Erro ao abrir modal");
            erro.setContentText("Não foi possível abrir a tela de edição.");
            erro.show();
        }
    }
}
