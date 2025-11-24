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
import com.example.utils.TelegramNotifier;

import java.util.List;

public class ServicosController {

    @FXML private VBox listaServicos;
    @FXML private TextField txtBuscar;
    @FXML private Button btnNovoServico;

    @FXML
    private void initialize() {
        System.out.println("Tela SERVIÇOS carregada");
        carregarServicos();

        // Desabilitar botão para funcionários
        UserSession session = UserSession.getInstance();
        if (session.isEmployee() && btnNovoServico != null) {
            btnNovoServico.setDisable(true);
            btnNovoServico.setOpacity(0.5);
        }
    }

    // ─── NOVO SERVIÇO ───
    @FXML
    private void novoServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal_novoservico.fxml"));
            Parent root = loader.load();

            ModalNovoServicoController controller = loader.getController();
            controller.setCallback((Service novoServico) -> {
                carregarServicos();
                notificarSucesso(
                        "🆕 *Novo serviço cadastrado!*\n" +
                                "📝 Nome: " + novoServico.getName() + "\n" +
                                "💰 Preço: R$ " + String.format("%.2f", novoServico.getPrice())
                );
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Novo Serviço");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            notificarErro("❌ Erro ao abrir modal de novo serviço:\n" + e.getMessage());
        }
    }

    // ─── EDITAR SERVIÇO ───
    private void editarServico(Service servico) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editarservico.fxml"));
            Parent root = loader.load();

            EditarServicoController controller = loader.getController();
            controller.carregarServico(servico);
            controller.setCallback(atualizado -> {
                carregarServicos();
                notificarSucesso(
                        "✏️ *Serviço editado!*\n" +
                                "📝 Nome: " + servico.getName() + "\n" +
                                "💰 Preço: R$ " + String.format("%.2f", servico.getPrice())
                );
            });

            Stage stage = new Stage();
            stage.setTitle("Editar Serviço");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            notificarErro("❌ Erro ao abrir modal de edição:\n" + e.getMessage());
        }
    }

    // ─── CARREGAR SERVIÇOS ───
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

    // ─── CRIAR CARD ───
    private HBox criarCardServico(Service s) {
        HBox card = new HBox(25);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("servico-card");

        VBox info = new VBox(6);
        Label nome = new Label(s.getName());
        nome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        String descricao = s.getDescription() != null ? s.getDescription() : "Sem descrição";
        Label lblDesc = new Label(descricao.length() > 50 ? descricao.substring(0, 50) + "..." : descricao);
        lblDesc.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px;");
        Tooltip.install(lblDesc, new Tooltip(descricao));

        String duracao = s.getDuration() != null ? s.getDuration().toMinutes() + " minutos" : "N/A";
        Label lblDuracao = new Label("Duração: " + duracao);
        lblDuracao.setStyle("-fx-text-fill: #a0e0ff; -fx-font-size: 13px; -fx-font-style: italic;");

        String categoria = s.getCategory() != null ? s.getCategory() : "Geral";
        Label lblCategoria = new Label("Categoria: " + categoria);
        lblCategoria.setStyle("-fx-background-color: #444444; -fx-padding: 2 6; -fx-text-fill: #fbbf24; -fx-font-size: 12px; -fx-background-radius: 4;");

        Label status = new Label("Status: " + (s.isActive() ? "ATIVO" : "INATIVO"));
        status.setStyle(s.isActive() ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" :
                "-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #555555;");

        info.getChildren().addAll(nome, lblDesc, lblDuracao, lblCategoria, status, sep);
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox colunaAcoes = new VBox(8);
        colunaAcoes.setAlignment(Pos.CENTER_RIGHT);

        Label preco = new Label(String.format("R$ %.2f", s.getPrice()));
        preco.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981; -fx-font-size: 16px;");

        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add("btn-editar");
        btnEditar.setOnAction(e -> editarServico(s));

        Button btnExcluir = new Button("Excluir");
        btnExcluir.getStyleClass().add("btn-excluir");
        btnExcluir.setOnAction(e -> {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setHeaderText("Confirmar exclusão");
            confirmacao.setContentText("Deseja realmente excluir o serviço '" + s.getName() + "'?");
            confirmacao.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean sucesso = ServicesDAO.delete(s.getId());
                    carregarServicos();
                    if (sucesso) {
                        notificarSucesso("🗑️ *Serviço excluído!*\n📝 Nome: " + s.getName());
                    } else {
                        notificarErro("❌ Falha ao excluir serviço: " + s.getName());
                    }
                }
            });
        });

        UserSession session = UserSession.getInstance();
        if (session.isEmployee()) {
            btnEditar.setDisable(true); btnEditar.setOpacity(0.5);
            btnExcluir.setDisable(true); btnExcluir.setOpacity(0.5);
        }

        HBox botoes = new HBox(8, btnEditar, btnExcluir);
        colunaAcoes.getChildren().addAll(preco, botoes);

        card.getChildren().addAll(info, colunaAcoes);

        return card;
    }

    // ─── MÉTODOS DE NOTIFICAÇÃO ───
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
