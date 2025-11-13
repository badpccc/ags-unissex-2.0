package com.example;


import com.example.dao.AgendamentoDAO;
import com.example.models.Agendamento;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class AgendamentosController {

    @FXML
    private VBox listaAgendamentos;

    @FXML
    private AnchorPane rootContainer; // container principal onde o card será aberto

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        carregarAgendamentos();
    }

    // 🔹 Abrir o card de novo agendamento
    @FXML
    private void abrirCardAgendamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/card_agendamento.fxml"));
            Parent root = loader.load();

            // Exemplo: abrir em novo Stage ou trocar dentro de um Pane
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 🔹 Carregar lista de agendamentos
    private void carregarAgendamentos() {
        listaAgendamentos.getChildren().clear();
        List<Agendamento> agendamentos = AgendamentoDAO.listar();

        if (agendamentos.isEmpty()) {
            Label vazio = new Label("Nenhum agendamento encontrado.");
            vazio.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 16px;");
            listaAgendamentos.getChildren().add(vazio);
            return;
        }

        for (Agendamento ag : agendamentos) {
            HBox card = criarCardAgendamento(ag);
            listaAgendamentos.getChildren().add(card);
        }
    }

    // 🔹 Criar um card visual para cada agendamento
    private HBox criarCardAgendamento(Agendamento ag) {
        HBox card = new HBox(20);
        card.getStyleClass().add("agendamento-card");
        card.setPrefWidth(900);
        card.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 20; -fx-background-radius: 12;");

        VBox info = new VBox(8);
        Label nomeCliente = new Label("👤 " + ag.getClienteNome());
        nomeCliente.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label servico = new Label("💈 Serviço: " + ag.getServico());
        servico.setStyle("-fx-text-fill: #cccccc;");

        String dataHoraFormatada = "🕒 " + ag.getDataAgendamento().format(formatoData)
                + " às " + ag.getHora().format(formatoHora);
        Label data = new Label(dataHoraFormatada);
        data.setStyle("-fx-text-fill: #bbbbbb;");

        Label status = new Label("📋 Status: " + ag.getStatus());
        status.setStyle("-fx-text-fill: #f9d94a;");

        info.getChildren().addAll(nomeCliente, servico, data, status);

        HBox botoes = new HBox(10);
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-edit");
        editar.setOnAction(e -> editarAgendamento(ag));

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-delete");
        excluir.setOnAction(e -> {
            AgendamentoDAO.excluir(ag.getId());
            carregarAgendamentos();
        });

        botoes.getChildren().addAll(editar, excluir);
        botoes.setStyle("-fx-alignment: center-right;");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        card.getChildren().addAll(info, espaco, botoes);
        return card;
    }

    // 🔹 Criar agendamento rápido (modo texto)

    @FXML
    private void novoAgendamento() throws IOException {
        abrirCardAgendamento(); // chama o card bonito
    }


    // 🔹 Editar agendamento (apenas nome)
    private void editarAgendamento(Agendamento ag) {
        TextInputDialog dialog = new TextInputDialog(ag.getClienteNome());
        dialog.setTitle("Editar Agendamento");
        dialog.setHeaderText("Editar nome do cliente");
        dialog.setContentText("Novo nome:");

        dialog.showAndWait().ifPresent(novoNome -> {
            ag.setClienteNome(novoNome);
            AgendamentoDAO.atualizar(ag);
            carregarAgendamentos();
        });
    }
}