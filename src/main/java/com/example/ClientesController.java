package com.example;

import com.example.backends.classes.Client;
import com.example.backends.database.data.ClientDAO;

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

        // Carrega todos os clientes do banco
        ClientDAO.getAllClients().forEach(this::adicionarCliente);

        btnAdicionarCliente.setOnAction(e -> abrirModalNovoCliente());
    }

    // ------------ MODAL NOVO CLIENTE ----------------
    private void abrirModalNovoCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("novo_cliente.fxml"));
            Parent root = loader.load();

            NovoClienteController controller = loader.getController();

            // Criar ScrollPane para permitir rolagem
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            Stage modal = new Stage();
            Scene scene = new Scene(scrollPane, 800, 600); // Tamanho inicial maior
            modal.setScene(scene);
            modal.setTitle("Novo Cliente");
            modal.setResizable(true);
            
            // Configura tamanhos mínimo e permite maximização
            modal.setMinWidth(600);
            modal.setMinHeight(500);
            
            // Permite fullscreen com F11 no modal
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("F11")) {
                    modal.setFullScreen(!modal.isFullScreen());
                }
            });
            
            modal.initOwner(btnAdicionarCliente.getScene().getWindow());
            modal.initModality(Modality.APPLICATION_MODAL);

            controller.setOnClienteSalvo(cliente -> {
                System.out.println("=== CALLBACK EXECUTADO ===");
                System.out.println("Nome: " + cliente.getName());
                System.out.println("Telefone: " + cliente.getPhoneNumber());
                System.out.println("Email: " + cliente.getEmail());
                
                boolean sucesso = ClientDAO.insert(cliente);
                System.out.println("Inserção no banco: " + sucesso);
                System.out.println("ID gerado: " + cliente.getId());
                
                if (sucesso) {
                    adicionarCliente(cliente);
                    System.out.println("Cliente adicionado na interface");
                } else {
                    System.out.println("ERRO: Falha ao inserir no banco!");
                }
                System.out.println("=== FIM CALLBACK ===");
            });

            modal.show();
            modal.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------ ADICIONAR ITEM NA LISTA --------------
    public void adicionarCliente(Client c) {
        listaClientes.getChildren().add(criarItemCliente(c));
    }

    // ------------ CRIA O CARD COMPLETO DO CLIENTE --------
    public HBox criarItemCliente(Client c) {

        HBox linha = new HBox();
        linha.getStyleClass().add("cliente-card");
        linha.setSpacing(40);

        // ----------- COLUNA 1 (DADOS PESSOAIS) -----------------
        VBox col1 = new VBox(
                criarLabel("Nome: ", c.getName()),
                criarLabel("Telefone: ", c.getPhoneNumber()),
                criarLabel("Email: ", c.getEmail()),
                criarLabel("Endereço: ", c.getAddress()),
                criarLabel("Ativo: ", c.isActive() ? "Sim" : "Não"),
                criarLabel("Cadastro: ", c.getRegistrationDate() != null ? c.getRegistrationDate().toString() : "—")
        );
        col1.setSpacing(6);

        // ----------- COLUNA 2 (DADOS CAPILARES + VISITA) ---------
        VBox col2 = new VBox(
                criarLabel("Tipo de cabelo: ", c.getHairType()),
                criarLabel("Textura: ", c.getHairTexture()),
                criarLabel("Couro cabeludo: ", c.getScalp()),
                criarLabel("Alergias: ", getOr(c.getAllergies(), "Nenhuma")),
                criarLabel("Última visita: ",
                        c.getLastVisit() != null ? c.getLastVisit().toLocalDate().toString() : "—"),
                criarLabel("Observações: ", getOr(c.getObservations(), "—"))
        );
        col2.setSpacing(6);

        // --------------------- BOTÕES ----------------------------
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-edit");
        editar.setOnAction(e -> abrirEditarCliente(c));

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-delete");
        excluir.setOnAction(e -> {
            System.out.println("=== BOTÃO EXCLUIR CLICADO ===");
            System.out.println("Cliente: " + c.getName() + " (ID: " + c.getId() + ")");
            
            boolean sucesso = ClientDAO.delete(c.getId());
            System.out.println("Resultado da exclusão: " + sucesso);
            
            if (sucesso) {
                listaClientes.getChildren().remove(linha);
                System.out.println("Cliente removido da interface");
            } else {
                System.out.println("ERRO: Não foi possível excluir o cliente!");
            }
        });

        VBox boxAcoes = new VBox(10, editar, excluir);
        boxAcoes.setStyle("-fx-alignment: center-right;");

        // Adiciona tudo ao card
        linha.getChildren().addAll(col1, col2, boxAcoes);

        return linha;
    }

    // ------------ CRIA LABEL FORMATADA ---------------
    private Label criarLabel(String titulo, String conteudo) {
        Label lbl = new Label(titulo + (conteudo != null ? conteudo : "—"));
        lbl.getStyleClass().add("cliente-info");
        return lbl;
    }

    private String getOr(String valor, String padrao) {
        return (valor != null && !valor.isEmpty()) ? valor : padrao;
    }

    // ------------ MODAL EDITAR CLIENTE -----------------
    @FXML
    private void abrirEditarCliente(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarCliente.fxml"));
            Parent root = loader.load();

            EditarClienteController controller = loader.getController();
            // TODO: Implementar EditarClienteController.setClient(client);
            // controller.setClient(client);

            Stage stage = new Stage();
            stage.setTitle("Editar Cliente");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            atualizarLista();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------ ATUALIZAR LISTA COMPLETA -------------
    private void atualizarLista() {
        listaClientes.getChildren().clear();
        ClientDAO.getAllClients().forEach(this::adicionarCliente);
    }
}
