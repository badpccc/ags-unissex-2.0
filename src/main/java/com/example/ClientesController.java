package com.example;

import com.example.backends.classes.Client;
import com.example.backends.database.data.ClientDAO;
import com.example.utils.TelegramNotifier;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientesController {

    @FXML
    private VBox listaClientes;

    @FXML
    private Button btnAdicionarCliente;

    @FXML
    public void initialize() {
        ClientDAO.getAllClients().forEach(this::adicionarCliente);
        btnAdicionarCliente.setOnAction(e -> abrirModalNovoCliente());
    }

    // ---------------------------------------------------------------------
    // ████ MODAL NOVO CLIENTE — COMPLETO, ORGANIZADO, EM TELA CHEIA ████
    // ---------------------------------------------------------------------
    private void abrirModalNovoCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("novo_cliente.fxml"));
            Parent root = loader.load();

            NovoClienteController controller = loader.getController();

            // Scroll para telas menores
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            Stage modal = new Stage();
            Scene scene = new Scene(scrollPane, 900, 650);
            modal.setScene(scene);
            modal.setTitle("Novo Cliente");
            modal.setResizable(true);

            // ------------------------------------------------------------------
            // 🔵 ABRIR AUTOMATICAMENTE EM TELA CHEIA
            // ------------------------------------------------------------------
            modal.setMaximized(true); // ocupa toda a tela
            modal.setFullScreen(false); // modo tela cheia real
            modal.setFullScreenExitHint(""); // remove mensagem do JavaFX
            modal.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);

            // Atalho opcional F11 para fullscreen
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("F11")) {
                    modal.setFullScreen(!modal.isFullScreen());
                }
            });

            modal.initOwner(btnAdicionarCliente.getScene().getWindow());
            modal.initModality(Modality.APPLICATION_MODAL);

            // ------------------------------------------------------------------
            // 🔵 CALLBACK ÚNICO E COMPLETO
            // ------------------------------------------------------------------
            controller.setOnClienteSalvo(cliente -> {
                try {

                    // -------------------- 1. VALIDAÇÃO DE EMAIL --------------------
                    if (cliente.getEmail() == null || !cliente.getEmail().contains("@")) {

                        TelegramNotifier.sendError(
                                "Tentativa de cadastro com email inválido:\n" +
                                        "👤 Nome: " + cliente.getName() + "\n" +
                                        "📧 Email informado: `" + cliente.getEmail() + "`"
                        );

                        System.out.println("ERRO: Email inválido");
                        return; // impede o cadastro
                    }

                    // -------------------- 2. SALVAR NO BANCO --------------------
                    boolean sucesso = ClientDAO.insert(cliente);

                    if (!sucesso) {
                        TelegramNotifier.sendError(
                                "Falha ao inserir cliente no banco:\n" +
                                        "👤 Nome: " + cliente.getName() + "\n" +
                                        "📞 Telefone: " + cliente.getPhoneNumber() + "\n" +
                                        "📧 Email: " + cliente.getEmail()
                        );
                        return;
                    }

                    // -------------------- 3. ATUALIZAR UI --------------------
                    adicionarCliente(cliente);

                    // -------------------- 4. ENVIAR NOTIFICAÇÃO --------------------
                    TelegramNotifier.send(
                            "📢 *Novo cliente cadastrado!*\n\n" +
                                    "👤 Nome: " + cliente.getName() + "\n" +
                                    "📞 Telefone: " + cliente.getPhoneNumber() + "\n" +
                                    "📧 Email: " + cliente.getEmail()
                    );

                    System.out.println("Cliente salvo com sucesso!");

                } catch (Exception ex) {

                    // -------------------- 5. ERRO INESPERADO --------------------
                    TelegramNotifier.sendError(
                            "Erro inesperado ao salvar cliente:\n```\n" +
                                    ex.getMessage() + "\n```"
                    );

                    ex.printStackTrace();
                }
            });

            modal.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------
    // ████ GERAR ITENS DE LISTA ████
    // ---------------------------------------------------------------------
    public void adicionarCliente(Client c) {
        listaClientes.getChildren().add(criarItemCliente(c));
    }

    private HBox criarItemCliente(Client c) {

        HBox card = new HBox();
        card.getStyleClass().add("cliente-card");
        card.setSpacing(25);

        Label lblNome = new Label(c.getName());
        lblNome.getStyleClass().add("cliente-nome");

        Label lblTelefone = new Label("Telefone: " + c.getPhoneNumber());
        lblTelefone.getStyleClass().add("cliente-info");

        Label lblEmail = new Label("Email: " + c.getEmail());
        lblEmail.getStyleClass().add("cliente-info");

        VBox colunaInfo = new VBox(lblNome, lblTelefone, lblEmail);
        colunaInfo.setSpacing(6);
        HBox.setHgrow(colunaInfo, javafx.scene.layout.Priority.ALWAYS);

        Button btnDetalhes = new Button("Ver detalhes");
        btnDetalhes.getStyleClass().add("btn-info");
        btnDetalhes.setOnAction(e -> abrirModalDetalhes(c));

        Button btnEditar = criarBotao("Editar", "btn-edit", e -> abrirEditarCliente(c));

        Button btnExcluir = criarBotao("Excluir", "btn-delete", e -> {
            try {

                boolean sucesso = ClientDAO.delete(c.getId());

                if (!sucesso) {

                    TelegramNotifier.sendError(
                            "❌ *Falha ao excluir cliente do banco*\n\n" +
                                    "👤 Nome: " + c.getName() + "\n" +
                                    "📞 Telefone: " + c.getPhoneNumber() + "\n" +
                                    "📧 Email: " + c.getEmail()
                    );

                    System.out.println("Erro ao excluir cliente.");
                    return;
                }

                // REMOVE DA LISTA
                listaClientes.getChildren().remove(card);

                // NOTIFICAÇÃO DE SUCESSO
                TelegramNotifier.send(
                        "🗑️ *Cliente excluído com sucesso!*\n\n" +
                                "👤 Nome: " + c.getName() + "\n" +
                                "📞 Telefone: " + c.getPhoneNumber() + "\n" +
                                "📧 Email: " + c.getEmail()
                );

                System.out.println("Cliente excluído com sucesso!");

            } catch (Exception ex) {

                // ERRO INESPERADO
                TelegramNotifier.sendError(
                        "❗ *Erro inesperado ao excluir cliente:*\n```\n" +
                                ex.getMessage() + "\n```"
                );

                ex.printStackTrace();
            }
        });


        VBox colunaAcoes = new VBox(8, btnDetalhes, btnEditar, btnExcluir);
        colunaAcoes.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(colunaInfo, colunaAcoes);

        return card;
    }

    private Button criarBotao(String texto, String classe, javafx.event.EventHandler<javafx.event.ActionEvent> acao) {
        Button btn = new Button(texto);
        btn.getStyleClass().add(classe);
        btn.setOnAction(acao);
        btn.setPrefWidth(90);
        return btn;
    }

    // ---------------------------------------------------------------------
    // ████ MODAL DETALHES ████
    // ---------------------------------------------------------------------
    private void abrirModalDetalhes(Client c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("detalhes_cliente.fxml"));
            Parent root = loader.load();

            DetalhesClienteController controller = loader.getController();
            controller.setCliente(c);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalhes do Cliente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------
    // ████ EDITAR CLIENTE ████
    // ---------------------------------------------------------------------
    @FXML
    private void abrirEditarCliente(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarCliente.fxml"));
            Parent root = loader.load();

            EditarClienteController controller = loader.getController();
            controller.setCliente(client); // <-- importante

            Stage stage = new Stage();
            stage.setTitle("Editar Cliente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            atualizarLista(); // recarregar lista após salvar

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void atualizarLista() {
        listaClientes.getChildren().clear();
        ClientDAO.getAllClients().forEach(this::adicionarCliente);
    }
}
