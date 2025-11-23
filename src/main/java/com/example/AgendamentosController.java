package com.example;

import com.example.backends.classes.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.Duration;

public class AgendamentosController {

    @FXML private VBox listaAgendamentos;

    @FXML
    private void initialize() {
        System.out.println("Tela AGENDAMENTOS carregada");

        // Exemplo: adicionar agendamentos iniciais
        adicionarCard(new Service("Corte Masculino", new BigDecimal("25.00"), Duration.ofMinutes(30)));
        adicionarCard(new Service("Barba", new BigDecimal("15.00"), Duration.ofMinutes(15)));
    }

    @FXML
    private void novoAgendamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal_novoservico.fxml"));
            Parent root = loader.load();

            ModalNovoServicoController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Novo Agendamento");
            stage.showAndWait();

            Service novo = controller.getServiceCriado();
            if (novo != null) {
                adicionarCard(novo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adicionarCard(Service service) {
        // Card principal
        HBox card = new HBox(20);
        card.getStyleClass().add("ag-card");

        // Informações do agendamento
        VBox infoBox = new VBox(4);
        Label lblNome = new Label(service.getName());
        lblNome.getStyleClass().add("ag-nome");

        Label lblDuracao = new Label("Duração: " + service.getFormattedDuration());
        lblDuracao.getStyleClass().add("ag-info");

        Label lblPreco = new Label(String.format("R$ %.2f", service.getPrice()));
        lblPreco.getStyleClass().add("ag-preco");

        Label lblStatus = new Label("Status: AGENDADO");
        lblStatus.getStyleClass().add("ag-status-agendado");

        infoBox.getChildren().addAll(lblNome, lblDuracao, lblPreco, lblStatus);

        // Spacer para alinhamento
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Botões
        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add("btn-editar");
        btnEditar.setOnAction(e -> editarAgendamento(service, lblNome, lblDuracao, lblPreco, lblStatus));

        Button btnExcluir = new Button("Excluir");
        btnExcluir.getStyleClass().add("btn-excluir");
        btnExcluir.setOnAction(e -> listaAgendamentos.getChildren().remove(card));

        Button btnConcluir = new Button("Concluir");
        btnConcluir.getStyleClass().add("btn-concluir");
        btnConcluir.setOnAction(e -> {
            lblStatus.setText("Status: CONCLUÍDO");
            lblStatus.getStyleClass().removeAll("ag-status-agendado", "ag-status-cancelado");
            lblStatus.getStyleClass().add("ag-status-concluido");
        });

        VBox botoes = new VBox(5, btnEditar, btnExcluir, btnConcluir);

        card.getChildren().addAll(infoBox, spacer, botoes);
        listaAgendamentos.getChildren().add(card);
    }

    private void editarAgendamento(Service service, Label lblNome, Label lblDuracao, Label lblPreco, Label lblStatus) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal_novoservico.fxml"));
            Parent root = loader.load();

            ModalNovoServicoController controller = loader.getController();
            controller.preencherFormulario(service);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Editar Agendamento");
            stage.showAndWait();

            Service atualizado = controller.getServiceCriado();
            if (atualizado != null) {
                lblNome.setText(atualizado.getName());
                lblDuracao.setText("Duração: " + atualizado.getFormattedDuration());
                lblPreco.setText(String.format("R$ %.2f", atualizado.getPrice()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
