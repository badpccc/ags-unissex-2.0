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
import javafx.geometry.Pos;

import com.example.backends.classes.Service;
import com.example.backends.database.data.ServicesDAO;

import java.util.List;

public class ServicosController {

    @FXML private VBox listaServicos;
    @FXML private TextField txtBuscar;

    @FXML
    private void initialize() {
        System.out.println("Tela SERVIÇOS carregada");
        carregarServicos();
    }

    @FXML
    private void novoServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal_novoservico.fxml"));
            Parent root = loader.load();

            ModalNovoServicoController controller = loader.getController();

            controller.setCallback(() -> {
                System.out.println("SERVIÇO SALVO → atualizar tabela");
                carregarServicos();
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Novo Serviço");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editarServico(Service servico) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editarservico.fxml"));
            Parent root = loader.load();

            EditarServicoController controller = loader.getController();
            controller.carregarServico(servico);
            controller.setCallback(atualizado -> carregarServicos());

            Stage stage = new Stage();
            stage.setTitle("Editar Serviço");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarServicos() {
        new Thread(() -> {
            final List<Service> servicos = ServicesDAO.getAllServices();
            
            Platform.runLater(() -> {
                listaServicos.getChildren().clear();

                if (servicos == null || servicos.isEmpty()) {
                    Label l = new Label("Nenhum serviço encontrado.");
                    l.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 16px;");
                    listaServicos.getChildren().add(l);
                    return;
                }

                for (Service s : servicos) {
                    listaServicos.getChildren().add(criarCardServico(s));
                }
            });
        }).start();
    }

    private HBox criarCardServico(Service servico) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 18; -fx-background-radius: 12;");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(6);

        Label nome = new Label("💈 " + servico.getName());
        nome.setStyle("-fx-font-size: 18; -fx-text-fill: white; -fx-font-weight: bold;");

        String descricao = servico.getDescription() != null ? servico.getDescription() : "Sem descrição";
        Label lblDesc = new Label("📝 " + descricao);
        lblDesc.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px;");

        String duracao = servico.getDuration() != null ? servico.getDuration().toMinutes() + " min" : "N/A";
        Label lblDuracao = new Label("⏱️ Duração: " + duracao);
        lblDuracao.setStyle("-fx-text-fill: #bbbbbb; -fx-font-size: 13px;");

        String categoria = servico.getCategory() != null ? servico.getCategory() : "Geral";
        Label lblCategoria = new Label("🏷️ " + categoria);
        lblCategoria.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 12px;");

        Label status = new Label(servico.isActive() ? "✅ ATIVO" : "❌ INATIVO");
        status.setStyle(servico.isActive() ? 
            "-fx-text-fill: #10b981; -fx-font-weight: bold;" : 
            "-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        info.getChildren().addAll(nome, lblDesc, lblDuracao, lblCategoria, status);

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        Label preco = new Label(String.format("R$ %.2f", servico.getPrice()));
        preco.setStyle("-fx-font-size: 22; -fx-text-fill: #10b981; -fx-font-weight: bold;");

        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add("btn-editar");
        btnEditar.setOnAction(e -> editarServico(servico));

        Button btnExcluir = new Button("Excluir");
        btnExcluir.getStyleClass().add("btn-excluir");
        btnExcluir.setOnAction(e -> {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setHeaderText("Confirmar exclusão");
            confirmacao.setContentText("Deseja realmente excluir o serviço '" + servico.getName() + "'?");
            confirmacao.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    ServicesDAO.delete(servico.getId());
                    carregarServicos();
                }
            });
        });

        HBox botoes = new HBox(10, btnEditar, btnExcluir);

        card.getChildren().addAll(info, espaco, preco, botoes);

        return card;
    }
}
