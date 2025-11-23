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

public class ServicosController {

    @FXML private VBox listaServicos;

    @FXML
    private void initialize() {
        System.out.println("Tela SERVIÇOS carregada");

        // Exemplo: adicionar serviços iniciais
        adicionarCard(new Service("Corte Masculino", new BigDecimal("25.00"), Duration.ofMinutes(30)));
        adicionarCard(new Service("Barba", new BigDecimal("15.00"), Duration.ofMinutes(15)));
    }

    @FXML
    private void novoServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal_novoservico.fxml"));
            Parent root = loader.load();

            ModalNovoServicoController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Novo Serviço");
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
        HBox card = new HBox(20);
        card.getStyleClass().add("ag-card");

        // Informações do serviço
        VBox infoBox = new VBox(4);
        Label lblNome = new Label(service.getName());
        lblNome.getStyleClass().add("ag-nome");

        Label lblDuracao = new Label("Duração: " + service.getFormattedDuration());
        lblDuracao.getStyleClass().add("ag-info");

        infoBox.getChildren().addAll(lblNome, lblDuracao);

        // Spacer
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Preço formatado
        Label lblPreco = new Label(String.format("R$ %.2f", service.getPrice()));
        lblPreco.getStyleClass().add("ag-info");

        // Botão Editar
        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add("btn-editar");
        btnEditar.setOnAction(e -> editarServico(service, lblNome, lblDuracao, lblPreco));

        // Botão Excluir
        Button btnExcluir = new Button("Excluir");
        btnExcluir.getStyleClass().add("btn-excluir");
        btnExcluir.setOnAction(e -> listaServicos.getChildren().remove(card));

        card.getChildren().addAll(infoBox, spacer, lblPreco, btnEditar, btnExcluir);
        listaServicos.getChildren().add(card);
    }

    private void editarServico(Service service, Label lblNome, Label lblDuracao, Label lblPreco) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal_novoservico.fxml"));
            Parent root = loader.load();

            ModalNovoServicoController controller = loader.getController();
            controller.preencherFormulario(service);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Editar Serviço");
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
