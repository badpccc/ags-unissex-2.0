package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import javafx.application.Platform;

import com.example.backends.classes.Appointment;
import com.example.backends.classes.Client;
import com.example.backends.classes.Service;
import com.example.backends.database.data.AppointmentDAO;
import com.example.backends.database.data.ClientDAO;
import com.example.backends.database.data.ServicesDAO;
import com.example.backends.enums.AppointmentStatus;
import com.example.utils.TelegramNotifier;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgendamentosController {

    @FXML private VBox listaAgendamentos;
    @FXML private Button btnNovoAgendamento;

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        carregarAgendamentos();

        // Desabilitar botão para funcionários
        UserSession session = UserSession.getInstance();
        if (session.isEmployee() && btnNovoAgendamento != null) {
            btnNovoAgendamento.setDisable(true);
            btnNovoAgendamento.setOpacity(0.5);
        }
    }

    // ─── NOVO AGENDAMENTO ───
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
            stage.setResizable(true);
            stage.setMinWidth(700);
            stage.setMinHeight(600);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            notificarErro("Erro ao abrir modal de novo agendamento:\n" + e.getMessage());
        }
    }

    private void adicionarAgendamento(Appointment a) {
        carregarAgendamentos();
        notificarSucesso("📅 *Novo agendamento cadastrado!*\n" +
                "👤 Cliente: " + getNomeCliente(a) + "\n" +
                "💰 Total: R$ " + String.format("%.2f", a.getTotalPrice()) + "\n" +
                "📅 Data/Hora: " + a.getAppointmentDateTime().format(formatoData) +
                " | " + a.getAppointmentDateTime().format(formatoHora));
    }

    // ─── CARREGAR LISTA ───
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
        HBox card = new HBox(25);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("servico-card");
        card.setStyle("-fx-padding: 18; -fx-background-radius: 12; -fx-background-color: #2a2a2a;");
        card.setMaxWidth(700);
        card.setPrefWidth(700);

        HBox container = new HBox(card);
        container.setAlignment(Pos.CENTER);

        VBox info = new VBox(6);

        Client cliente = ClientDAO.getClientByID(ag.getClientId());
        Label nome = new Label(getNomeCliente(ag));
        nome.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

        List<String> nomesServicos = new ArrayList<>();
        if (ag.getServiceIds() != null) {
            for (Long serviceId : ag.getServiceIds()) {
                Service s = ServicesDAO.getServiceByID(serviceId);
                if (s != null) nomesServicos.add(s.getName());
            }
        }
        String servicosTexto = nomesServicos.isEmpty() ? "Nenhum serviço" : String.join(", ", nomesServicos);
        Label servico = new Label(servicosTexto.length() > 50 ? servicosTexto.substring(0, 50) + "..." : servicosTexto);
        servico.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px;");
        Tooltip.install(servico, new Tooltip(servicosTexto));

        Label data = new Label(ag.getAppointmentDateTime().format(formatoData) +
                " | " + ag.getAppointmentDateTime().format(formatoHora));
        data.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 13px;");

        Label preco = new Label(String.format("R$ %.2f", ag.getTotalPrice()));
        preco.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label status = new Label(ag.getStatus().getDisplayName());
        status.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold; -fx-font-size: 13px;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #555555;");

        info.getChildren().addAll(nome, servico, data, preco, status, sep);
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox colunaBotoes = new VBox(8);
        colunaBotoes.setAlignment(Pos.CENTER_RIGHT);

        Button mudarStatus = new Button("Mudar Status");
        mudarStatus.getStyleClass().add("btn-status");
        mudarStatus.setOnAction(e -> abrirMudarStatus(ag));

        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-editar");
        editar.setOnAction(e -> abrirEdicao(ag));

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-excluir");
        excluir.setOnAction(e -> {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setHeaderText("Confirmar exclusão");
            confirmacao.setContentText("Deseja realmente excluir este agendamento?");
            confirmacao.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean sucesso = AppointmentDAO.delete(ag.getId());
                    if (sucesso) {
                        carregarAgendamentos();
                        notificarSucesso("🗑️ *Agendamento excluído!*\n" +
                                "👤 Cliente: " + getNomeCliente(ag));
                    } else {
                        notificarErro("❌ Falha ao excluir agendamento do cliente " + getNomeCliente(ag));
                    }
                }
            });
        });

        UserSession session = UserSession.getInstance();
        if (session.isEmployee()) {
            mudarStatus.setDisable(true); mudarStatus.setOpacity(0.5);
            editar.setDisable(true); editar.setOpacity(0.5);
            excluir.setDisable(true); excluir.setOpacity(0.5);
        }

        HBox botoes = new HBox(8, mudarStatus, editar, excluir);
        colunaBotoes.getChildren().add(botoes);

        card.getChildren().addAll(info, colunaBotoes);

        return container;
    }

    // ─── MUDAR STATUS ───
    private void abrirMudarStatus(Appointment ag) {
        List<AppointmentStatus> statusList = List.of(
                AppointmentStatus.AGENDADO,
                AppointmentStatus.EM_ANDAMENTO,
                AppointmentStatus.CONCLUIDO,
                AppointmentStatus.CANCELADO,
                AppointmentStatus.NAO_COMPARECEU
        );

        ChoiceDialog<AppointmentStatus> dialog = new ChoiceDialog<>(ag.getStatus(), statusList);
        dialog.setTitle("Mudar Status");
        dialog.setHeaderText("Alterar status do agendamento");
        dialog.setContentText("Selecione o novo status:");
        dialog.getDialogPane().setStyle("-fx-background-color: #1b1b1b;");

        dialog.showAndWait().ifPresent(novoStatus -> {
            if (novoStatus != ag.getStatus()) {
                ag.setStatus(novoStatus);
                boolean sucesso = AppointmentDAO.update(ag);

                if (sucesso) {
                    carregarAgendamentos();
                    notificarSucesso("✅ *Status atualizado!*\n" +
                            "👤 Cliente: " + getNomeCliente(ag) + "\n" +
                            "📌 Novo status: " + novoStatus.getDisplayName());
                } else {
                    notificarErro("❌ Falha ao atualizar status do agendamento do cliente " + getNomeCliente(ag));
                }
            }
        });
    }

    // ─── EDITAR AGENDAMENTO ───
    private void abrirEdicao(Appointment ag) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarAgendamento.fxml"));
            Parent root = loader.load();

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
            notificarErro("Erro ao abrir modal de edição:\n" + e.getMessage());
        }
    }

    // ─── UTILITÁRIOS ───
    private String getNomeCliente(Appointment ag) {
        Client c = ClientDAO.getClientByID(ag.getClientId());
        return c != null ? c.getName() : "Cliente não encontrado";
    }

    private void notificarSucesso(String msg) {
        try {
            TelegramNotifier.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notificarErro(String msg) {
        try {
            TelegramNotifier.sendError(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
