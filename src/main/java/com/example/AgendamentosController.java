package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgendamentosController {

    @FXML private VBox listaAgendamentos;

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    // 🔹 Lista local (SEM BANCO)
    private static final List<AgendamentoTemp> agendamentos = new ArrayList<>();

    @FXML
    public void initialize() {

        // Criar agendamento inicial somente uma vez
        if (agendamentos.isEmpty()) {
            agendamentos.add(new AgendamentoTemp(
                    "Arthur",
                    "Corte Masculino",
                    LocalDate.now(),
                    LocalTime.of(15, 30),
                    25.00
            ));
        }

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
            stage.setResizable(false);

            stage.showAndWait();

            carregarAgendamentos();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Adicionar agendamento na lista local
    private void adicionarAgendamento(AgendamentoTemp a) {
        agendamentos.add(a);
        carregarAgendamentos();
    }

    // 🔹 Carregar cards
    private void carregarAgendamentos() {
        listaAgendamentos.getChildren().clear();

        if (agendamentos.isEmpty()) {
            Label l = new Label("Nenhum agendamento encontrado.");
            l.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 16px;");
            listaAgendamentos.getChildren().add(l);
            return;
        }

        for (AgendamentoTemp ag : agendamentos) {
            listaAgendamentos.getChildren().add(criarCardAgendamento(ag));
        }
    }

    private HBox criarCardAgendamento(AgendamentoTemp ag) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 18; -fx-background-radius: 12;");

        VBox info = new VBox(6);

        Label nome = new Label("👤 " + ag.cliente);
        nome.setStyle("-fx-font-size: 18; -fx-text-fill: white; -fx-font-weight: bold;");

        Label servico = new Label("💈 Serviço: " + ag.servico);
        servico.setStyle("-fx-text-fill: #cccccc;");

        Label data = new Label(
                "🕒 " + ag.data.format(formatoData) + " às " + ag.hora.format(formatoHora)
        );
        data.setStyle("-fx-text-fill: #bbbbbb;");

        Label preco = new Label("💵 R$ " + ag.preco);
        preco.setStyle("-fx-text-fill: #90ee90;");

        info.getChildren().addAll(nome, servico, data, preco);

// 🔵 Botão editar
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-editar");
        editar.setOnAction(e -> abrirEdicao(ag));

// 🔴 Botão excluir
        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-excluir");
        excluir.setOnAction(e -> {
            agendamentos.remove(ag);
            carregarAgendamentos();
        });


        // Caixa de botões (✔ AGORA CORRETO)
        HBox botoes = new HBox(10, editar, excluir);

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        card.getChildren().addAll(info, espaco, botoes);

        return card;
    }

    private void abrirEdicao(AgendamentoTemp ag) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/EditarAgendamento.fxml"));
            Parent root = loader.load();

            EditarAgendamentoController controller = loader.getController();

            controller.carregarAgendamento(ag, atualizado -> {
                agendamentos.remove(ag);
                agendamentos.add(atualizado);
                carregarAgendamentos();
            });

            Stage stage = new Stage();
            stage.setTitle("Editar Agendamento");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 🔹 CLASSE INTERNA para salvar temporariamente
    public static class AgendamentoTemp {
        public String cliente;
        public String servico;
        public LocalDate data;
        public LocalTime hora;
        public double preco;

        public AgendamentoTemp(String cliente, String servico, LocalDate data, LocalTime hora, double preco) {
            this.cliente = cliente;
            this.servico = servico;
            this.data = data;
            this.hora = hora;
            this.preco = preco;
        }
    }
}
